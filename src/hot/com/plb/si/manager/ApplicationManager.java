package com.plb.si.manager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.hibernate.LazyInitializationException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.plb.dto.SessionOrganismesDto;
import com.plb.model.Categorie;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.FormationMutualisees;
import com.plb.model.FormationPartenaire;
import com.plb.model.Partenaire;
import com.plb.model.Prospect;
import com.plb.model.Session;
import com.plb.model.TarifIntra;
import com.plb.model.TypeContact;
import com.plb.model.TypeContactSI;
import com.plb.model.TypeContactWeb;
import com.plb.model.UpdateTarif;
import com.plb.model.devis.EmailTemplate;
import com.plb.model.directory.Role;
import com.plb.si.dto.ProspectUpdate;
import com.plb.si.service.CategorieDao;
import com.plb.si.service.FiliereDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.PartenaireDao;
import com.plb.si.service.TypeContactDao;
import com.plb.si.util.Labels;
import com.plb.util.Util;

@Name("applicationManager")
@Scope(ScopeType.APPLICATION)
public class ApplicationManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5553848921946372970L;

	@Out(required = false)
	private List<SelectItem> monthsItem;

	@Out(required = false)
	private List<Filiere> filieres;

	@Out(required = false)
	private List<SelectItem> filieresItem;

	@Out(required = false)
	private List<Partenaire> partenaires;

	@Out(required = false)
	private List<Categorie> categories;

	@Out(required = false)
	private List<TarifIntra> tarifsIntra;
	@In
	EntityManager entityManager;

	@Out(required = false)
	private Date lastUpdateTarifs;

	@Out(required = false)
	private List<TypeContact> typesContact;

	@Out(required = false)
	private List<TypeContactSI> typesContactSi;

	@Out(required = false)
	private List<TypeContactWeb> typesContactWeb;

	@Out(required = false)
	private List<Formation> formationsActives;

	@Out(required = false)
	private List<EmailTemplate> emailTemplates;

	private Map<Formation, List<FormationPartenaire>> allFormationsPartenaire = new HashMap<Formation, List<FormationPartenaire>>();
	private Map<Formation, FormationMutualisees> allFormationsMutualisee = new HashMap<Formation, FormationMutualisees>();
	private Map<Formation, List<Session>> allFormationsSessions = new HashMap<Formation, List<Session>>();

	@Out
	public SimpleDateFormat longMonthFormat = new SimpleDateFormat("MMM/yy");

	@Out
	public SimpleDateFormat fullMonthFormat = new SimpleDateFormat("MMMM",
			Locale.FRANCE);

	@Out
	public Locale LOCALE = Locale.FRANCE;

	@Out(required = false)
	Role[] roles;

	public final static ResourceBundle projectBundle = ResourceBundle
			.getBundle("plbsi");
	public final static boolean MAIL_ENABLED = Boolean
			.parseBoolean(projectBundle.getString("MAIL_ENABLED"));
	public final static String SERVEUR = projectBundle.getString("SERVEUR");
	@Out
	public final static String WEB_URL = projectBundle.getString("WEB_URL");

	public final static String FTP_URL = projectBundle
			.getString("FTP_URL");
	public final static String FTP_LOGIN = projectBundle
			.getString("FTP_LOGIN");
	public final static String FTP_PASSWORD = projectBundle
			.getString("FTP_PASSWORD");
	public final static String WEB_ROOT_IMG = projectBundle
			.getString("WEB_ROOT_IMG");

	@Out
	public final static String WEB_URL_IMG = projectBundle
			.getString("WEB_URL_IMG");
	@Out
	public final static String URL_CSS = projectBundle.getString("URL_CSS");

	public final static String DEVIS_CC = projectBundle.getString("DEVIS_CC");
	
	public final static int DEVIS_RELANCE = Integer.parseInt(projectBundle.getString("DEVIS_RELANCE"));

	
	// Statuts prospects
	@Out
	public static final String ST_INTRA_RECHERCHE = "statut.intra.recherche";
	@Out
	public static final String ST_INTRA_AUDIT = "statut.intra.audit";
	@Out
	public static final String ST_INTRA_COMMERCIAL = "statut.intra.commercial";
	@Out
	public static final String ST_INTRA_RECHERCHE_MODIFIE = "statut.intra.recherche_modifiee";
	@Out
	public static final String ST_INTRA_LOGISTIQUE = "statut.intra.logistique";
	@Out
	public static final String ST_INTRA_ANNULE = "statut.intra.annule";
	@Out
	public static final String ST_INTRA_OK = "statut.intra.ok";

	@Logger
	Log log;

	// Le polling doit être inférieur au timeout de session qui est de 30 mn par
	// dfaut => 25*60*1000=
	@Out(required = false)
	private long SESSION_FREQUENCY = 1500000;

	// Le polling doit être inférieur au timeout de session qui est de 30 mn par
	// dfaut => 2*60*1000=120000
	@Out(required = false)
	private long CONVERSATION_FREQUENCY = 60000;

	private Date grillesUpdateDate = new Date();

	int MAX_SIZE = 20;
	private List<ProspectUpdate> lastProspectUpdated = new LinkedList<ProspectUpdate>();

	private List<Formation> allFormations, allFormationsArchived;

	public void refresh() {
		log.debug("Refreshing filieres, categories, partenaires, ... from application cache");
		filieres = null;
		categories = null;
		partenaires = null;
		typesContact = null;
		typesContactSi = null;
		typesContactWeb = null;
		emailTemplates = null;
		refreshFormations();
	}

	@Factory("roles")
	public void initRoles() {
		log.debug("Factory for roles");
		roles = Role.values();
	}

	@Factory("filieres")
	public void fetchFilieres() {
		log.debug("Factory for filieres");
		FiliereDao fDao = new FiliereDao(entityManager);
		filieres = fDao.findAll();
	}

	@Factory("monthsItem")
	public void fetchMonthItem() {
		log.debug("Factory for monthItem");
		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		cal.set(Calendar.DAY_OF_MONTH, 15);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		SimpleDateFormat df = new SimpleDateFormat("MMMM", Locale.FRANCE);
		monthsItem = new ArrayList<SelectItem>();
		monthsItem.add(new SelectItem(Calendar.JANUARY,
				df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		monthsItem.add(new SelectItem(Calendar.FEBRUARY, df.format(cal
				.getTime())));
		cal.set(Calendar.MONTH, Calendar.MARCH);
		monthsItem
				.add(new SelectItem(Calendar.MARCH, df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.APRIL);
		monthsItem
				.add(new SelectItem(Calendar.APRIL, df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.MAY);
		monthsItem.add(new SelectItem(Calendar.MAY, df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.JUNE);
		monthsItem.add(new SelectItem(Calendar.JUNE, df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.JULY);
		monthsItem.add(new SelectItem(Calendar.JULY, df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.AUGUST);
		monthsItem
				.add(new SelectItem(Calendar.AUGUST, df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		monthsItem.add(new SelectItem(Calendar.SEPTEMBER, df.format(cal
				.getTime())));
		cal.set(Calendar.MONTH, Calendar.OCTOBER);
		monthsItem.add(new SelectItem(Calendar.OCTOBER,
				df.format(cal.getTime())));
		cal.set(Calendar.MONTH, Calendar.NOVEMBER);
		monthsItem.add(new SelectItem(Calendar.NOVEMBER, df.format(cal
				.getTime())));
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		monthsItem.add(new SelectItem(Calendar.DECEMBER, df.format(cal
				.getTime())));
	}

	@Factory("filieresItem")
	public void fetchFilieresItem() {
		log.debug("Factory for filieresItem");
		FiliereDao fDao = new FiliereDao(entityManager);
		filieres = fDao.findAll();
		filieresItem = new ArrayList<SelectItem>();
		for (Filiere f : filieres) {
			filieresItem.add(new SelectItem(f, f.getLibelle()));
		}
	}

	@Factory("partenaires")
	public void fetchPartenaires() {
		log.debug("Factory for partenaires");
		PartenaireDao pDao = new PartenaireDao(entityManager);
		partenaires = pDao.findAll();

	}

	@Factory("categories")
	public void fetchCategories() {
		log.debug("Factory for categories");
		CategorieDao cDao = new CategorieDao(entityManager);
		categories = cDao.findAll();
	}

	@SuppressWarnings("unchecked")
	@Factory("tarifsIntra")
	public void fetchTarifsIntra() {
		log.debug("Factory for tarifs Intra");
		Query q = entityManager.createQuery("from TarifIntra");
		tarifsIntra = q.getResultList();

	}

	@Factory("lastUpdateTarifs")
	public void fetchLastUpdateTarifs() {
		log.debug("Factory for tarifs lastUpdateTarifs");
		Query q = entityManager
				.createQuery("from UpdateTarif u order by u.date desc");
		List<UpdateTarif> results = (List<UpdateTarif>) q.getResultList();
		if (results.size() > 0) {
			lastUpdateTarifs = ((UpdateTarif) q.getResultList().get(0))
					.getDate();
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2006);
			lastUpdateTarifs = cal.getTime();
		}
	}

	@SuppressWarnings("unchecked")
	@Factory("emailTemplates")
	public void initEmailTemplates() {
		log.debug("Factory for emailTemplates");
		emailTemplates = entityManager.createQuery(
				"from EmailTemplate e order by e.subject").getResultList();
	}

	@Observer(value = "emailTemplateUpdated")
	public void refreshEmailTemplates() {
		log.debug("Refreshing email Templates from application cache");
		emailTemplates = null;
	}

	@Observer(value = "categorieUpdated")
	public void refreshCategories() {
		log.debug("Refreshing categories from application cache");
		categories = null;
	}

	@Observer(value = "tarifsUpdated")
	public void refreshTarisInter() {
		log.debug("Refreshing lastUpdateTarifs from application cache");
		lastUpdateTarifs = null;
	}

	public Date getGrillesUpdateDate() {
		return grillesUpdateDate;
	}

	public void setGrillesUpdateDate(Date grillesUpdateDate) {
		this.grillesUpdateDate = grillesUpdateDate;
	}

	@Observer(value = "prospectUpdated")
	public void prospectUpdate(Prospect prospect) {
		log.debug("Updating Last Prospect Modified Date");
		if (lastProspectUpdated.size() == MAX_SIZE) {
			lastProspectUpdated.remove(0);
		}
		lastProspectUpdated.add(new ProspectUpdate(prospect));
	}

	public List<ProspectUpdate> getUpdatedProspects(Date lastSearchDate) {
		log.debug("getUpdatedProspects : " + lastProspectUpdated
				+ "SearchDate " + lastSearchDate);
		if (lastSearchDate == null) {
			lastSearchDate = new Date();
		}
		List<ProspectUpdate> ret = new ArrayList<ProspectUpdate>();
		for (ProspectUpdate pu : lastProspectUpdated) {
			if (pu.getUpdateDate() != null
					&& pu.getUpdateDate().after(lastSearchDate)) {
				ret.add(pu);
			}
		}
		return ret;
	}

	@Factory("typesContact")
	public void fetchTypeContact() {
		typesContact = new TypeContactDao(entityManager).findAll();
	}

	@Factory("typesContactSi")
	public void fetchTypeContactSI() {
		if (typesContact == null) {
			fetchTypeContact();
		}
		typesContactSi = new ArrayList<TypeContactSI>();
		for (TypeContact tc : typesContact) {
			if (tc instanceof TypeContactSI) {
				typesContactSi.add((TypeContactSI) tc);
			}
		}
	}

	@Factory("typesContactWeb")
	public void fetchTypeContactWeb() {
		if (typesContact == null) {
			fetchTypeContact();
		}
		typesContactWeb = new ArrayList<TypeContactWeb>();
		for (TypeContact tc : typesContact) {
			if (tc instanceof TypeContactWeb) {
				typesContactWeb.add((TypeContactWeb) tc);
			}
		}
	}

	public boolean isWebType(String typeProspect) {
		if (typesContactWeb == null) {
			fetchTypeContactWeb();
		}
		for (TypeContactWeb tWeb : typesContactWeb) {
			if (tWeb.getLibelle().toUpperCase()
					.equals(typeProspect.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	@Factory("formationsActives")
	public void fetchFormationsActives() {
		formationsActives = new FormationDao(entityManager).findAllOrder();
	}

	@Observer("formationUpdated")
	public void refreshFormations() {
		allFormations = null;
		allFormationsArchived = null;
		formationsActives = null;
		allFormationsPartenaire = new HashMap<Formation, List<FormationPartenaire>>();
		allFormationsMutualisee = new HashMap<Formation, FormationMutualisees>();
		allFormationsSessions = new HashMap<Formation, List<Session>>();
	}

	public List<Formation> getAllFormations() {
		if (allFormations == null) {
			long start = System.currentTimeMillis();
			allFormations = new FormationDao(entityManager).findAll();
			log.info("Fetching ALL Formations took "+(System.currentTimeMillis()-start)+ "ms");
		}
		return allFormations;
	}

	public List<Formation> getAllFormationsArchived() {
		if (allFormationsArchived == null) {
			allFormationsArchived = new FormationDao(entityManager)
					.findAllWithArchived();
		}
		return allFormationsArchived;
	}

	public List<FormationPartenaire> getFormationsPartenaire(Formation formation) {
		if (formation == null) {
			return null;
		}
		List<FormationPartenaire> ret = allFormationsPartenaire.get(formation);
		if (ret == null) {
			formation = entityManager.find(Formation.class,
					formation.getIdFormation());
			ret = formation.getFormationsPartenaire();
			allFormationsPartenaire.put(formation, ret);
		}
		return ret;
	}

	public String getFormationsPartenairesAsString(Formation formation) {

		StringBuffer sbf = new StringBuffer();
		boolean bFirst = true;
		List<FormationPartenaire> fps = getFormationsPartenaire(formation);
		if (fps != null) {
			for (FormationPartenaire fp : fps) {
				if (bFirst) {
					sbf.append(fp.getPartenaire().getNom() + "(" + fp.getPrix()
							+ ")");
					bFirst = false;
				} else {
					sbf.append("/" + fp.getPartenaire().getNom() + "("
							+ fp.getPrix() + ")");
				}
			}
		}
		return sbf.toString();
	}

	public FormationMutualisees getFormationsMutualisees(Formation formation) {
		if (formation == null) {
			return null;
		}
		FormationMutualisees ret = allFormationsMutualisee.get(formation);
		if (ret == null) {
			formation = entityManager.find(Formation.class,
					formation.getIdFormation());
			ret = formation.getFormationMutualisees();
			allFormationsMutualisee.put(formation, ret);
		}
		return ret;
	}
	
	public String getFormationsMutualiseesAsString(Formation formation) {
		StringBuffer sbf = new StringBuffer();
		FormationMutualisees formationMutualisees = getFormationsMutualisees(formation);
		if (formationMutualisees != null) {
			boolean bFirst = true;
			for (Formation f : formationMutualisees.getFormations()) {
				if ( !f.equals(this) ) {
				if (bFirst) {
					sbf.append(f.getReference());
					bFirst = false;
				} else {
					sbf.append("," + f.getReference());
				}
				}
			}
		}
		return sbf.toString();
	}

	public List<Session> getFormationsSessions(Formation formation) {
		if (formation == null) {
			return null;
		}
		List<Session> ret = allFormationsSessions.get(formation);
		if (ret == null) {
			formation = entityManager.find(Formation.class,
					formation.getIdFormation());
			ret = formation.getSessions();
			allFormationsSessions.put(formation, ret);
		}
		return ret;
	}

	public String getFormationAllSessionsAsString(Formation formation,
			Date month) {
		StringBuilder sb = new StringBuilder();
		List<Date> dates = new ArrayList<Date>();
		boolean bFirst = true;
		List<Session> sessions = getFormationsSessions(formation);
		for (Session s : sessions) {
			if (Util.monthEqual(s.getDebut(), month)
					&& !dates.contains(s.getDebut())) {
				dates.add(s.getDebut());
				if (bFirst) {
					sb.append(s.getDay() + "-" + s.getDayFin());
					bFirst = false;
				} else {
					sb.append(";" + s.getDay() + "-" + s.getDayFin());
				}
			}
		}

		// Pour l'export excel on renvoie un caractère blanc
		return sb.length() > 0 ? sb.toString() : " ";
	}

	public List<SessionOrganismesDto> getFormationsNextSessions(
			Formation formation) {
		if (formation == null) {
			return null;
		}
		List<SessionOrganismesDto> ret = null;
		try {
			ret = formation.getNextSessionsOrganismesDto();
		} catch (LazyInitializationException e) {
			System.out.println("===========Next Session LAZY========");
			Formation f = entityManager.find(Formation.class,
					formation.getIdFormation());
			formation.setSessions(f.getSessions());
			formation.setFormationsPartenaire(f.getFormationsPartenaire());
			formation.setFormationMutualisees(f.getFormationMutualisees());
			ret = formation.getNextSessionsOrganismesDto();
		}

		return ret;
	}

	public List<Formation> getSameFormations(Formation formation) {
		if (formation == null) {
			return null;
		}
		List<Formation> ret = null;
		try {
			ret = formation.getSameFormations();
		} catch (LazyInitializationException e) {
			System.out.println("===========Same LAZY========");
			Formation f = entityManager.find(Formation.class,
					formation.getIdFormation());
			formation.setSessions(f.getSessions());
			formation.setFormationsPartenaire(f.getFormationsPartenaire());
			formation.setFormationMutualisees(f.getFormationMutualisees());
			ret = formation.getSameFormations();
		}

		return ret;
	}
}
