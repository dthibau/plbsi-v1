package com.plb.si.manager;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.plb.dto.SessionOrganismeDto;
import com.plb.model.Formation;
import com.plb.model.Prospect;
import com.plb.model.ProspectDetail;
import com.plb.model.Session;
import com.plb.model.devis.Devis;
import com.plb.model.devis.DevisSession;
import com.plb.model.directory.Account;
import com.plb.si.service.DevisDao;

@Name("devisManager")
@Scope(ScopeType.CONVERSATION)
public class DevisManager {

	@In
	FacesContext facesContext;

	@In
	EntityManager entityManager;

	@In(required = false)
	Formation formation;

	@In
	Account loggedUser;

	private JasperReport devisReport;

	@Out(value = ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, scope = ScopeType.SESSION, required = false)
	JasperPrint print;

	@In(create = true)
	Map<String, Float> tarifsInter;

	private Devis devis;
	
	private boolean creerDemandeClient;

	private List<String> sessionIds;

	private DevisDao devisDao;

	@Logger
	Log log;
	
	@Create
	public void init() {
		devisDao = new DevisDao(entityManager);
	}

	/**
	 * Crée un devis à partir d'une formation
	 * @param formation
	 */
	@Begin(join = true)
	public void createDevis(Formation formation) {
		log.debug(loggedUser + " createDevis()");
		devis = new Devis();
		devis.setAuteur(loggedUser);
		devis.setFormation(formation);
		devis.addSession(new DevisSession());
	}
	
	/**
	 * Crée un devis à partir d'un prospect, la formation peut être nulle.
	 * @param prospect
	 * @param formation
	 */
	@Begin(join = true)
	public void createDevis(Prospect prospect, Formation  formation) {
		log.debug(loggedUser + " createDevis() from Prospect");
		devis = new Devis(prospect);
		devis.setAuteur(loggedUser);
		
		if ( formation != null ) {
			devis.setFormation(formation);
			// Chercher une session dans le prospect
			List<SessionOrganismeDto> listeSession = formation.getNextSessionsOrganismeDto();
			DevisSession devisSession = new DevisSession();
			devisSession.setNbParticipants(prospect.getProspectDetail()
					.getNb_participants());
			for (int i = 0; i < listeSession.size(); i++) {
				if (listeSession.get(i).getSession().toString()
						.equals(prospect.getProspectDetail().getDate())) {
					devisSession.setSession(listeSession.get(i).getSession());
				}
			}
		}
	}

	@Begin(join = true)
	public void selectDevis(Devis devis) {
		log.debug(loggedUser + " selectDevis()");
		this.devis = entityManager.find(Devis.class, devis.getId());
	}

	@Begin(join = true)
	public List<Devis> getLastDevis() {
		log.debug(loggedUser + " getLastDevis()");
		return formation.getIdFormation() != 0 ? devisDao.findLast(loggedUser,
				formation) : new ArrayList<Devis>();
	}

	public void addSession() {
		log.debug(loggedUser + " addSession()");
		devis.addSession(new DevisSession());
	}

	public void removeSession(int index) {
		log.debug(loggedUser + " removeSession() index : "+index);
		devis.getSessions().remove(index);
	}

	public void generate() throws ServletException {
		log.debug(loggedUser + " generate() ");
		if (!entityManager.contains(devis)) {
			entityManager.persist(devis);
		}
		devis.setDate(new Date());
		entityManager.flush();

		if (devisReport == null) {
			devisReport = _loadReport("/devis.jasper");
		}
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		parametersMap.put("REPORT_LOCALE", Locale.FRANCE);
		parametersMap.put("idDevis", devis.getId());
		parametersMap.put("noDevis", _getNo(devis));
		if(formation == null){
			formation = devis.getFormation();
		}
		parametersMap.put("tarif", _getPrix(formation));
		parametersMap.put("sessions", _getSessions(devis));
		parametersMap.put("etranger", devis.getEtranger());
		parametersMap.put("particulier", devis.getParticulier());
		_serveDOC(devisReport, parametersMap);
		facesContext.responseComplete();
		
		//Création de la demande CLient si la case est cochée
		if(creerDemandeClient == true){
			String dateSession = "";
			Prospect prospect = new Prospect();
			ProspectDetail prospectDetail = new ProspectDetail();
			prospect.setDateCreation(devis.getDate());
			prospect.setNom(devis.getContactClient());
			prospectDetail.setDatedevis(devis.getDate());
			prospect.setSociete(devis.getClient());
			prospect.setType("CLIENT");
			prospect.setStatut("En cours");
			prospectDetail.setCgv(1);
			prospectDetail.setRempliPar(loggedUser.getNomComplet());
			prospectDetail.setCommercial(loggedUser.getNomComplet());
			prospectDetail.setNb_participants(devis.getSessions().get(devis.getSessions().size()-1).getNbParticipants());
			prospect.setReference(devis.getFormation().getReference());
			prospectDetail.setRemise(devis.getRemise());
			for(int i = 0 ; i < devis.getSessions().size() ; i++){
				if(i == 0){
					dateSession = devis.getSessions().get(i).getSession()+"";
				}
				else{
					dateSession = dateSession + " - " + devis.getSessions().get(i).getSession();
				}
			}
			prospectDetail.setDate(dateSession);
			prospectDetail.setProspect(prospect);
			if(devis.getParticulier() == false && devis.getEtranger() == false){
				prospectDetail.setNatureClient("Ste France");
			}
			else{
				if(devis.getParticulier() == true){
					prospectDetail.setNatureClient("Particulier");
				}
				else if(devis.getEtranger() == true){
					prospectDetail.setNatureClient("Etranger");
				}
			}
			System.out.println(prospectDetail.getNb_participants());
			entityManager.persist(prospect);
			entityManager.persist(prospectDetail);
		}
	}
	
	public void duplicate(Devis _devis) throws ServletException {
		log.debug(loggedUser + " duplicate() devis " + devis);
		devis = (Devis)_devis.getCopy();
		generate();
	}

	private JasperReport _loadReport(String jasperPath) throws ServletException {
		URL jasperResURL = this.getClass().getResource(jasperPath);
		JasperReport report = null;

		try {
			report = (JasperReport) JRLoader.loadObject(jasperResURL);
		} catch (JRException e) {
			throw new ServletException(e.getMessage());
		}
		return report;

	}

	private void _serveDOC(JasperReport jasperReport,
			Map<String, Object> parametersMap) throws ServletException {
		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		resp.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		resp.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + devis.getClient() + "_"
						+ devis.getNumero() + ".docx\"");
		try {
			Connection c = getConnection();
			print = JasperFillManager.fillReport(jasperReport, parametersMap,
					c);

			JRDocxExporter exporter = new JRDocxExporter();
			// AWDocExporter exporter = new AWDocExporter();

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,
					resp.getOutputStream());
			// exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
			// "result.doc");
			exporter.exportReport();
			c.close();

			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private Connection getConnection() throws SQLException {
		org.hibernate.Session session = (org.hibernate.Session) entityManager
				.getDelegate();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session
				.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();

		return cp.getConnection();

	}

	private float _getPrix(Formation formation) {
		if (formation.getCodeTarifInter() == null
				|| formation.getCodeTarifInter().equals("")
				|| formation.getCodeTarifInter().equals("EO")) {
			return formation.getPrix();
		} else {
			log.info("_getPrix tarifsInter" + tarifsInter + " Code : " + formation.getCodeTarifInter()
					+ formation.getDuree() + " Formation : "+formation);
			return tarifsInter.get(formation.getCodeTarifInter()
					+ formation.getDuree());
		}
	}

	private String _getNo(Devis devis) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat("ddMMyy");
		sb.append(df.format(new Date()));
		if (devis.getClient().length() > 3) {
			sb.append(devis.getClient().substring(0, 3).toUpperCase());
		} else {
			sb.append(devis.getClient().toUpperCase());
		}
		Query q = entityManager
				.createQuery("from Devis d where d.client=:client");
		q.setParameter("client", devis.getClient());
		int no = q.getResultList().size();
		sb.append(no);
		return sb.toString();

	}

	private List<String> _getSessions(Devis devis) {
		List<String> ret = new ArrayList<String>();

		SimpleDateFormat df = new SimpleDateFormat("dd/MM");

		for (DevisSession ds : devis.getSessions()) {
			Query q = entityManager
					.createQuery("from Session s where s.formation=:formation and s.debut >= :startDate order by s.debut");
			q.setParameter("formation", devis.getFormation());
			q.setParameter("startDate", ds.getSession().getDebut());
			@SuppressWarnings("unchecked")
			List<Session> result = (List<Session>) q.getResultList();
			StringBuffer sbf = new StringBuffer();
			if (result.size() > 0) {
				sbf.append(df.format(result.get(0).getDebut()) + "-"
						+ df.format(result.get(0).getFin()));
			}
			;
			if (result.size() > 1) {
				sbf.append("<br/>" + df.format(result.get(1).getDebut()) + "-"
						+ df.format(result.get(1).getFin()));
			}
			;
			if (result.size() > 2) {
				sbf.append("<br/>" + df.format(result.get(2).getDebut()) + "-"
						+ df.format(result.get(2).getFin()));
			}

			ret.add(sbf.toString());
		}

		return ret;

	}

	public boolean isNew() {
		return !entityManager.contains(devis);
	}

	public Devis getDevis() {
		return devis;
	}

	public void setDevis(Devis devis) {
		this.devis = devis;
	}

	public List<String> getSessionIds() {
		return sessionIds;
	}

	public void setSessionIds(List<String> sessionIds) {
		this.sessionIds = sessionIds;
	}

	public boolean isCreerDemandeClient() {
		return creerDemandeClient;
	}

	public void setCreerDemandeClient(boolean creerDemandeClient) {
		this.creerDemandeClient = creerDemandeClient;
	}

}
