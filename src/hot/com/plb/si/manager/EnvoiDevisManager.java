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
	private boolean ready;
	private Email email;
	private boolean editNom = false;

	FormationDao formationDao;
	Formation formationDevis = null;

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
	 * Depending on the propsect return a list or something else;
	 * 
	 * @return
	 */
	public String getFormations() {
		// First, Prospect has a reference
		if (formationDevis != null) {

			return formationDevis.getLibelle();
		}

		if (prospect.getFormations() != null) {
			return PlbUtil.getCollectionAsString(prospect.getFormations());
		}
		return "";
	}

	public void send() {

		email.setRecipient(prospect.getEmail());
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
		if (prospect.getReference() != null) {
			formationDevis = formationDao.findByReference(prospect
					.getReference());
			Fichier planFormation = new Fichier();
			planFormation.setContentType("application/pdf");
			planFormation.setName("Plan de formation de "
					+ formationDevis.getReference());
			byte[] data = PlbUtil.sendGetAsBytes(formationDevis.getUrlPdf());
			planFormation.setData(data);
			planFormation.setLength(data.length);
			ret.add(planFormation);
		}
		return ret;
	}

}
