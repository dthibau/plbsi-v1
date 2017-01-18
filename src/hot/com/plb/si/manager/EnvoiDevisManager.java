package com.plb.si.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.plb.model.Fichier;
import com.plb.model.Formation;
import com.plb.model.Prospect;
import com.plb.model.ProspectFormation;
import com.plb.model.devis.Email;
import com.plb.model.directory.Account;
import com.plb.model.event.Event;
import com.plb.model.event.ProspectEnvoiMailEvent;
import com.plb.si.service.FormationDao;
import com.plb.si.service.NotificationService;
import com.plb.si.util.Labels;
import com.plb.si.util.PlbUtil;
import com.plb.si.validator.OptionnalEmailValidator;

@Name("envoiDevisManager")
@Scope(ScopeType.CONVERSATION)
public class EnvoiDevisManager {

	@In
	FacesContext facesContext;

	@In
	EntityManager entityManager;

	@In
	Account loggedUser;
	
	@In(create=true)
	NotificationService notificationService;
	
	@Logger
	Log log;

	@RequestParameter
	private String idProspect;

	private Prospect prospect;
//	private boolean ready;
	private Email email;
	private boolean editNom = false;

	FormationDao formationDao;
	List<ProspectFormation> formationsDevis = null;

	@Create
	public void init() {
		formationDao = new FormationDao(entityManager);
	}

	/**
	 * Crée un devis à partir d'une formation
	 * 
	 * @param formation
	 * @throws IOException
	 */
	@Begin(join = true)
	public void selectProspect() throws IOException {
		this.prospect = (Prospect) entityManager.find(Prospect.class,
				Integer.parseInt(idProspect));
		email = new Email();
		email.setSubject(Labels.getString("devis.envoi.subject",
				prospect.getIdProspect()));
		email.setCc(ApplicationManager.DEVIS_CC + "," + loggedUser.getEmail());
		email.setAttachments(_initAttachments());
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}

	public boolean isReady() {

		return prospect != null
				&& prospect.getEmail() != null
				&& prospect.getEmail().length() == 0
				&& prospect.getEmail().matches(
						OptionnalEmailValidator.EMAIL_FORMAT)
				&& prospect.getNomComplet() != null
				&& prospect.getNomComplet().length() == 0;

	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public void selectTemplate() {
		email.setBody(PlbUtil.evalAsString(facesContext, email
				.getEmailTemplate().getBody()));
	}

	public void switchNom() {
		editNom = !editNom;
	}

	public boolean isEditNom() {
		return editNom;
	}

	public void setEditNom(boolean editNom) {
		this.editNom = editNom;
	}

	public void addAttachment(FileUploadEvent event) throws IOException {
		UploadedFile item = event.getUploadedFile();
		Fichier attachment = new Fichier();
		attachment.setContentType(item.getContentType());
		attachment.setData(item.getData());
		attachment.setName(item.getName());
		attachment.setLength(Integer.parseInt("" + item.getSize()));
		email.getAttachments().add(attachment);
	}

	public void removeAttachment(Fichier f) {
		email.getAttachments().remove(f);
	}

	/**
	 * Genere le bon libellé en fonction du nombre de formations
	 * 
	 * @return
	 */
	public String getFormations() {
		if ( !formationsDevis.isEmpty() ) {
			StringBuffer sbf = new StringBuffer("");
			for ( int i=0; i< formationsDevis.size(); i++ ) {
				if ( i == 0 ) {
					sbf.append("« ");
				} else if ( i < formationsDevis.size()-1) {
					sbf.append(", « ");
				} else {
					sbf.append(" et « ");
				}
				sbf.append(formationsDevis.get(i).getFormation().getLibelle()).append(" »");
			}
			return sbf.toString();
		}
		return "";
	}
	
	public String getParticipants() {
		if ( !formationsDevis.isEmpty() ) {
			boolean bDiffere = false;
			String nbParticpants = formationsDevis.get(0).getParticipant();
			for ( int i=1; i< formationsDevis.size(); i++ ) {
				if ( !formationsDevis.get(i).getParticipant().equals(nbParticpants) ) {
					bDiffere = true;
					break;
				}
			}
			if ( bDiffere) {
				StringBuffer sbf = new StringBuffer("");
				for ( int i=0; i< formationsDevis.size(); i++ ) {
					if ( i == 0 ) {
						sbf.append("");
					} else if ( i < formationsDevis.size()-1) {
						sbf.append(", ");
					} else {
						sbf.append(" et ");
					}
					sbf.append(formationsDevis.get(i).getParticipant());
				}
				return sbf.toString();
			} else {
				return nbParticpants;
			}
		}
		return "";
	}

	public String getLieu() {
		return prospect.getProspectDetail().getLieu().substring(0, 1).toLowerCase() + prospect.getProspectDetail().getLieu().substring(1);
	}
	
	public String getDureeSouhaitee() {
		if ( !formationsDevis.isEmpty() ) {
			StringBuffer sbf = new StringBuffer("");
			for ( int i=0; i< formationsDevis.size(); i++ ) {
				if ( i == 0 ) {
					sbf.append("");
				} else if ( i < formationsDevis.size()-1) {
					sbf.append(", ");
				} else {
					sbf.append(" et ");
				}
				// Is it an Integer
				try {
					int duree = Integer.parseInt(formationsDevis.get(i).getDureeVoulu());
					sbf.append(duree).append(" jour(s) soit ").append(duree*7).append("heures");
				} catch (NumberFormatException e) {
					sbf.append(formationsDevis.get(i).getDureeVoulu());
				}
			}
			return sbf.toString();
		}
		return "";
	}
	public String getDuree() {
		if ( !formationsDevis.isEmpty() ) {
			StringBuffer sbf = new StringBuffer("");
			for ( int i=0; i< formationsDevis.size(); i++ ) {
				if ( i == 0 ) {
					sbf.append("");
				} else if ( i < formationsDevis.size()-1) {
					sbf.append(", ");
				} else {
					sbf.append(" et ");
				}
				// Is it an Integer
				int duree = formationsDevis.get(i).getFormation().getDuree();
				sbf.append(duree).append(" jour(s) soit ").append(duree*7).append("heures");
			}
			return sbf.toString();
		}
		return "";
	}
	
	public String getDateSouhaitee() {
		return prospect.getProspectDetail().getDate_souhaiteForDevis();
	}
	
	public String getDate() {
		if ( !formationsDevis.isEmpty() ) {
			StringBuffer sbf = new StringBuffer("");
			for ( int i=0; i< formationsDevis.size(); i++ ) {
				if ( i == 0 ) {
					sbf.append("");
				} else if ( i < formationsDevis.size()-1) {
					sbf.append(", ");
				} else {
					sbf.append(" et ");
				}
				// Is it an Integer
				sbf.append(formationsDevis.get(i).getSession());
			}
			return sbf.toString();
		}
		return "";
	}
	
	public void send() {

//		email.setRecipient(prospect.getEmail());
		email.setRecipient("david.thibau@gmail.com");
		email.setAttention(prospect.getNomComplet());
		notificationService.sendDevis(email, loggedUser);
		prospect.getProspectDetail().setDatedevis(new Date());
		Event prospectSendEvent = new ProspectEnvoiMailEvent(
				loggedUser, prospect,email);
		entityManager.persist(email);
		entityManager.persist(prospectSendEvent);
		
	}

	private List<Fichier> _initAttachments() throws IOException {
		List<Fichier> ret = new ArrayList<Fichier>();
		formationsDevis = _getFormations();
		for ( ProspectFormation pf : formationsDevis ) {			
			Fichier planFormation = new Fichier();
			planFormation.setContentType("application/pdf");
			planFormation.setName("Plan de formation de "
					+ pf.getFormation().getReference());
			byte[] data = PlbUtil.sendGetAsBytes(pf.getFormation().getUrlPdf());
			planFormation.setData(data);
			planFormation.setLength(data.length);
			ret.add(planFormation);
		}
		return ret;
	}

	private List<ProspectFormation> _getFormations() {
		if ( prospect.getInformationIntra() != null ) {
			return prospect.getFormations();
		} else { // Inter
			List<ProspectFormation> ret = new ArrayList<ProspectFormation>();
			if (prospect.getReference() != null) {
				ret.add(_constructProspectFormation(prospect.getReference()));
			}
			if (prospect.getProspectDetail().getReferenceBis() != null) {
				ret.add(_constructProspectFormation(prospect.getProspectDetail().getReferenceBis()));
			}
			return ret;
		}
	}
	
	private ProspectFormation _constructProspectFormation(String reference) {
		Formation formation = formationDao.findByReference(reference);
		ProspectFormation prospectFormation = new ProspectFormation();
		prospectFormation.setFormation(formation);
		prospectFormation.setProspect(prospect);
		prospectFormation.setParticipant(prospect.getProspectDetail().getParticipants());
		prospectFormation.setDureeVoulu(""+formation.getDuree());
		prospectFormation.setSession(prospect.getProspectDetail().getDate());	
		return prospectFormation;
	}
}
