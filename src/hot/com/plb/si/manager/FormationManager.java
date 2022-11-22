package com.plb.si.manager;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.plb.dto.SessionDto;
import com.plb.model.Categorie;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.FormationFiliere;
import com.plb.model.FormationMutualisees;
import com.plb.model.FormationPartenaire;
import com.plb.model.Session;
import com.plb.model.directory.Account;
import com.plb.model.event.Event;
import com.plb.model.event.FormationArchiveEvent;
import com.plb.model.event.FormationCommentEvent;
import com.plb.model.event.FormationCreationEvent;
import com.plb.model.event.FormationModificationEvent;
import com.plb.model.event.FormationSessionEvent;
import com.plb.model.event.FormationSuppressionEvent;
import com.plb.model.event.FormationUnArchiveEvent;
import com.plb.si.dto.FormationCategorieDto;
import com.plb.si.service.EventDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.NotificationService;
import com.plb.si.util.Labels;
import com.plb.si.util.PlbUtil;

@Name("formationManager")
@Scope(ScopeType.CONVERSATION)
public class FormationManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;

	@Out(required = false)
	public Formation formation;

	private Formation oldFormation;
	private List<Filiere> oldFilieres;
	private List<Formation> oldFormations;
	private List<FormationPartenaire> oldFormationsPartenaire;

	private boolean showArgumentaire = false;

	private boolean autreNiveau = false;

	@RequestParameter
	String formationId;

	// new parameter
	@RequestParameter
	String ref;

	@In
	EntityManager entityManager;

	@In
	Account loggedUser;

	@In
	FacesMessages facesMessages;
	
	@In
	FacesContext facesContext;

	@In(create = true)
	NotificationService notificationService;
	@In(create = true)
	Date lastUpdateTarifs;
	Event event;

	@In(create = true)
	List<Categorie> categories;

	@In(create = true)
	List<Filiere> filieres;

	@Logger
	Log log;

	private EventDao eventDao;
	private static int VISU_MODE = 0;

	int mode = VISU_MODE;

	List<Event> historique;

	private Session newSession;
	private FormationPartenaire newFormationPartenaire;
	private FormationFiliere newFormationFiliere;
	private Formation newFormationMutualisee;
	private Formation newFormationAssociee;
	private List<Formation> allFormations;
	// private String newComment;
	private int currentYear = Calendar.getInstance().get(Calendar.YEAR);

	List<SessionDto> currentSessions;

	FormationDao formationDao;

	private boolean updateTarif;

	List<Formation> formationSuivantes;

	@Create
	public void init() {
		log.debug("Creating FormationManager : loggedUser=" + loggedUser);
		eventDao = new EventDao(entityManager);
		formationDao = new FormationDao(entityManager);
	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void select(Formation formation) {
		long ts = System.currentTimeMillis();
		log.debug("Select formation : " + formation);
		formation = entityManager.find(Formation.class, formation.getIdFormation());
		_initSelect();
		log.debug("Select formation take : " + (ts-System.currentTimeMillis()));


	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public String select() {
		long ts = System.currentTimeMillis();
		log.debug("Select formation : " + formationId);
		formation = entityManager.find(Formation.class, Integer.parseInt(formationId));
		_initSelect();
		log.debug("Select formation take : " + (ts-System.currentTimeMillis()));
		return "/mz/formation/formation.xhtml";
	}

	// Fonction qui recupere une formation en fonction de sa référence
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void selectByReference() throws IOException {
		long ts = System.currentTimeMillis();
		log.debug("Select formation : " + ref);
		formation = formationDao.findByReference(ref);
		_initSelect();
		log.debug("SelectByReference take : " + (ts-System.currentTimeMillis()));
		
		HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
		response.sendRedirect(ApplicationManager.PLBSI_V2 + "offre/formation/"+formation.getIdFormation());
		facesContext.responseComplete();
		// return "/mz/formation/formation.xhtml";
	}

	private void _initSelect() {
		autreNiveau = formation.getNiveau() == null
				|| !(hasStandardNiveau(formation) ) 
				|| (formation.getAutreObjectifSimple() != null && formation.getAutreObjectifSimple().length() > 0);
		// Doit disparaitre à terme
		mode = VISU_MODE;

	}


	public void validateReference(FacesContext context, UIComponent component, Object value) {
		log.debug("validateReference()");
		String newReference = (String) value;
		if (oldFormation == null || !oldFormation.getReference().equals(newReference)) {
			try {
				formationDao.findByReference(newReference);
				throw new ValidatorException(new FacesMessage(Labels.getString("error.reference.unique")));
			} catch (NoResultException e) {
			}
		}

	}

	public void prepareComment() {
		log.debug("prepareComment()");
		event = new FormationCommentEvent(loggedUser, formation, "");
		notificationService.resolveDestinataires(loggedUser, event);

	}

	public void addComment() {
		log.debug("addComment()");
		if (event.getMessage() != null && event.getMessage().length() > 0) {
			entityManager.persist(event);
			notificationService.sendMail(1000, formation, event);
			historique = null;
		}
		entityManager.flush();

	}

	public void cancel() {
		log.debug("cancel()");
		currentSessions = null;
		entityManager.clear();
		formation = entityManager.find(Formation.class, formation.getIdFormation());

		setVisuMode();
	}


	public void validateUrl(FacesContext context, UIComponent component, Object value) {
		log.debug("validateUrl()");
		String[] schemes = { "http" };
		UrlValidator validator = new UrlValidator(schemes);
		if (!validator.isValid("http://www.plb.fr/" + value)) {
			throw new ValidatorException(new FacesMessage(Labels.getString("error.invalidURL")));
		}
		String url = (String) value;

		if (url.indexOf('.') != -1 || url.indexOf('*') != -1 || url.indexOf("'") != -1 || url.indexOf('"') != -1
				|| url.indexOf('?') != -1 || url.indexOf('&') != -1 || url.indexOf('|') != -1 || url.indexOf('/') != -1
				|| url.indexOf('\\') != -1) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					Labels.getString("error.invalidFormationURL"), Labels.getString("error.invalidFormationURL")));
		}

	}

	

	public void setVisuMode() {
		mode = VISU_MODE;
	}



	public boolean isVisuMode() {
		return true;
	}

	
	public boolean isPrixObsolete() {
		return formation.getLastUpdatePrix() == null || formation.getLastUpdatePrix().before(lastUpdateTarifs);
	}

	public List<Event> getHistorique() {
		if (historique == null && formation.getIdFormation() != 0) {
			historique = eventDao.findAll(formation);
		}
		return historique;
	}

	public List<FormationCategorieDto> autresFormationsCategorie() {
		List<FormationCategorieDto> ret = new ArrayList<FormationCategorieDto>();

		if (formation.getCategorie() != null) {
			List<Formation> formations = formationDao.findByCategorie(formation.getCategorie());
			ret.addAll(formations.stream().map(f -> new FormationCategorieDto(f)).collect(Collectors.toList()));

			List<FormationFiliere> formationsFilieres = formationDao
					.findByCategorieSecondaire(formation.getCategorie());
			ret.addAll(
					formationsFilieres.stream().map(ff -> new FormationCategorieDto(ff)).collect(Collectors.toList()));

			Collections.sort(ret);
		}

		return ret;

	}

	public Session getNewSession() {
		return newSession;
	}

	public void setNewSession(Session newSession) {
		this.newSession = newSession;
	}

	


	public void maintainConversation() {
		log.debug("Maintaining conversation for formation " + formation);
	}

	public List<SessionDto> getCurrentSessions() {

		if (currentSessions == null) {
			currentSessions = formation.getAllSessionsDto(currentYear);
		}
		return currentSessions;

	}

	@RaiseEvent("formationUpdated")
	public void saveCurrentSessions() {
		String modif = PlbUtil.mergeSessions(formation, currentSessions, currentYear);
		if (modif.length() > 0) {
			Event event = new FormationSessionEvent(loggedUser, formation, modif);
			entityManager.persist(event);
			historique = null;
			if (loggedUser.isOnlyCommercial()) {
				notificationService.resolveDestinataires(loggedUser, event);
				notificationService.sendMail(1000, formation, event);
			}
		}
		entityManager.flush();

		// Réinitialisation
		// currentSessions = null; // Force refreshing
		mode = VISU_MODE;

		log.debug("Save current sessions for " + this.formation);
	}

	/* Ajout d'une session Partenaire */
	public void addNewSessionPartenaire() {
		formation.addSessionPartenaire(newFormationPartenaire);
		newFormationPartenaire = new FormationPartenaire(formation);
	}

	public void removeSessionPartenaire(Session session) {
		formation.removeSession(session);
	}

	/* Mise à jour de formations associee */
	public Formation getNewFormationAssociee() {
		return newFormationAssociee;
	}

	public void setNewFormationAssociee(Formation newFormationAssociee) {
		this.newFormationAssociee = newFormationAssociee;
	}

	public void addFormationAssociee() {
		newFormationAssociee = entityManager.find(Formation.class, newFormationAssociee.getIdFormation());
		log.debug("Add formation associee " + newFormationAssociee + " for " + this.formation);
		formation.getFormationAssociees().add(newFormationAssociee);
		formationSuivantes = null;

	}

	public void removeFormationAssociee(Formation formation) {
		log.debug("Removing formation associee " + formation + " for " + this.formation);
		this.formation.getFormationAssociees().remove(formation);
		formationSuivantes = null;
	}

	public List<Formation> getFormationSuivantes() {
		if (formationSuivantes == null) {
			formationSuivantes = new ArrayList<Formation>();

			if (formation.getCategorie() != null && formation.getRangCategorie() != null
					&& formation.getRangCategorie() > 0 && formation.getFormationAssociees().size() < 3) {
				List<Formation> suivantes = formationDao.findSuivantes(formation);
				int stop = (3 - formation.getFormationAssociees().size()) < suivantes.size()
						? (3 - formation.getFormationAssociees().size())
						: suivantes.size();

				for (int i = 0; i < stop; i++) {
					formationSuivantes.add(suivantes.get(i));
				}
			}
		}
		return formationSuivantes;
	}

	/* Mise à jour de formations mutualisees */
	public Formation getNewFormationMutualisee() {
		return newFormationMutualisee;
	}

	public void setNewFormationMutualisee(Formation newFormationMutualisee) {
		this.newFormationMutualisee = newFormationMutualisee;
	}

	public void addFormationMutualisee() {
		newFormationMutualisee = entityManager.find(Formation.class, newFormationMutualisee.getIdFormation());
		StringBuilder sb = new StringBuilder();
		if (formation.getFormationMutualisees() != null) {
			if (newFormationMutualisee.getFormationMutualisees() != null) {
				for (Formation f : newFormationMutualisee.getFormationMutualisees().getFormations()) {
					formation.getFormationMutualisees().addFormation(f);
					sb.append(f.getReference() + " ");

				}
				newFormationMutualisee.getFormationMutualisees().setFormations(new ArrayList<Formation>());
				entityManager.remove(newFormationMutualisee.getFormationMutualisees());
				facesMessages.addFromResourceBundle(Severity.INFO, "mutualisation.merge", sb.toString());

			} else {
				formation.getFormationMutualisees().addFormation(newFormationMutualisee);
				facesMessages.addFromResourceBundle(Severity.INFO, "mutualisation.add", newFormationMutualisee);
			}
		} else {
			if (newFormationMutualisee.getFormationMutualisees() != null) {
				for (Formation f : newFormationMutualisee.getFormationMutualisees().getFormations()) {
					sb.append(f.getReference() + " ");
				}
				newFormationMutualisee.getFormationMutualisees().addFormation(formation);
				facesMessages.addFromResourceBundle(Severity.INFO, "mutualisation.addToGroup", sb.toString());
			} else {
				FormationMutualisees formationMutualisees = new FormationMutualisees();
				formationMutualisees.addFormation(newFormationMutualisee);
				formationMutualisees.addFormation(formation);
				entityManager.persist(formationMutualisees);
				facesMessages.addFromResourceBundle(Severity.INFO, "mutualisation.new");
			}
		}
		log.debug("Added formation mutualisee " + formation);

	}

	public void removeFormationMutualisee(Formation formationMutualisee) {
		formationMutualisee = entityManager.find(Formation.class, formationMutualisee.getIdFormation());
		formation.getFormationMutualisees().removeFormation(formationMutualisee);

	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public boolean isUpdateTarif() {
		return updateTarif;
	}

	public void setUpdateTarif(boolean updateTarif) {
		this.updateTarif = updateTarif;
	}



	public boolean isShowArgumentaire() {
		return showArgumentaire;
	}

	public void setShowArgumentaire(boolean showArgumentaire) {
		this.showArgumentaire = showArgumentaire;
	}

	public boolean isAutreNiveau() {
		return autreNiveau;
	}

	public void setAutreNiveau(boolean autreNiveau) {
		this.autreNiveau = autreNiveau;
		if ( !autreNiveau ) {
			formation.setAutreObjectifSimple(null);
		}
	}

	public String getObjectifSimpleDefaultValue() {
		return hasStandardNiveau(formation) ? Labels.getString("formation.objectif.simple."+formation.getNiveau()) : "";
	}
	
	public boolean hasStandardNiveau(Formation formation) {
		return formation.getNiveau().equals(ApplicationManager.NIVEAU_FONDAMENTAL)
		|| formation.getNiveau().equals(ApplicationManager.NIVEAU_INTERMEDIAIRE)
		|| formation.getNiveau().equals(ApplicationManager.NIVEAU_AVANCE);
	}

}
