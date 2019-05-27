package com.plb.si.manager;

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
import com.plb.model.event.FormationCommentEvent;
import com.plb.model.event.FormationCreationEvent;
import com.plb.model.event.FormationModificationEvent;
import com.plb.model.event.FormationSessionEvent;
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

	@In(create = true)
	NotificationService notificationService;
	@In(create = true)
	Date lastUpdateTarifs;
	Event event;

	@In(create = true)
	Map<String, Float> tarifsInter;

	@In
	ApplicationManager applicationManager;

	@In(create = true)
	List<Categorie> categories;

	@In(create = true)
	List<Filiere> filieres;

	@Logger
	Log log;

	private EventDao eventDao;
	private static int VISU_MODE = 0;
	private static int EDIT_MODE = 1;
	private static int NEW_MODE = 2;
	private static int EDIT_SESSION_MODE = 3;
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
		log.debug("Select formation : " + formation);
		formation = entityManager.find(Formation.class, formation.getIdFormation());
		// @3.5 : Pour être sur que le falg filière principale utilisée par le site web
		// soit en cohérence
		// Doit disparaitre à terme
		updateCategorie();
		_storeOldState();
		mode = VISU_MODE;

	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public String select() {
		log.debug("Select formation : " + formationId);
		formation = entityManager.find(Formation.class, Integer.parseInt(formationId));
		_storeOldState();
		// @3.5 : Pour être sur que le falg filière principale utilisée par le site web
		// soit en cohérence
		// Doit disparaitre à terme
		updateCategorie();
		mode = VISU_MODE;

		return "/mz/formation/formation.xhtml";
	}

	// Fonction qui recupere une formation en fonction de sa référence
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public String selectByReference() {
		log.debug("Select formation : " + ref);
		formation = formationDao.findByReference(ref);
		_storeOldState();
		mode = VISU_MODE;
		return "/mz/formation/formation.xhtml";
	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public String createNew() {
		log.debug("createNew()");
		formation = new Formation();
		formation.setType("mauve");
		newFormationPartenaire = new FormationPartenaire(formation);
		newFormationFiliere = new FormationFiliere(formation);

		mode = NEW_MODE;
		return "/mz/formation/formation.xhtml";
	}

	@RaiseEvent("formationUpdated")
	public void save() {
		log.debug("save()");
		if (entityManager.contains(formation)) {
			String modif = "";
			if (isUpdateTarif()) {
				formation.setLastUpdatePrix(new Date());
				modif = Labels.getString("formation.updateTarif") + " : Oui <br/>";
			}
			modif += PlbUtil.diffFormation(oldFormation, oldFilieres, oldFormationsPartenaire, oldFormations,
					formation);

			if (modif.length() > 0) {
				if (modif.indexOf(Labels.getString("formation.argumentaire")) != -1) {
					showArgumentaire = true;
				}
				Event event = new FormationModificationEvent(loggedUser, formation, modif);
				entityManager.persist(event);
				formation.setDateModification(new Date());
				historique = null;
				if (loggedUser.isOnlyCommercial()) {
					notificationService.resolveDestinataires(loggedUser, event);
					notificationService.sendMail(1000, formation, event);
				}
			}
		} else { // Création
			if (formation.getPrix() != 0) {
				formation.setLastUpdatePrix(new Date());
			}
			formation.setDateCreation(new Date());
			formation.setDateModification(new Date());
			entityManager.persist(formation);
			Event event = new FormationCreationEvent(loggedUser, formation);
			entityManager.persist(event);
			historique = null;
		}
		entityManager.flush();
		_storeOldState();

		mode = VISU_MODE;
		log.debug("Formation " + this.formation + " SAVED ");
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

	public void updateCategorie() {
		if (formation.getCategorie() != null) {
			_erasePrincipale();
			// @@ 3.5
			// On force la filière principale de la catégorie
			if (!formation.contains(formation.getCategorie().getFiliere())) {
				FormationFiliere ff = new FormationFiliere(formation);
				ff.setCategorie(formation.getCategorie()); // Update also filiere
				ff.setIsPrincipale("oui");
				if (formation.getRangCategorie() != null) {
					ff.setRang(formation.getRangCategorie());
				}
				formation.addFormationFiliere(ff);
			} else {
				FormationFiliere ff = formation.getFormationFiliere(formation.getCategorie().getFiliere());
				ff.setIsPrincipale("oui");
				ff.setCategorie(formation.getCategorie()); // @3.5 Mise à jour également de la catégorie, disparait à
															// terme
				if (formation.getRangCategorie() != null) {
					ff.setRang(formation.getRangCategorie());
				}
			}
			updateBaliseTitle();
		}
	}

	public void updateRangCategorie() {
		if (formation.getCategorie() != null) {
			FormationFiliere ff = formation.getFormationFiliere(formation.getCategorie().getFiliere());
			ff.setRang(formation.getRangCategorie());
		}
	}

	private void _erasePrincipale() {
		for (FormationFiliere ff : formation.getFormationFilieres()) {
			if (ff.isPrincipale()) {
				ff.setIsPrincipale("non");
			}
		}
	}

	public void updateBaliseTitle() {
		log.debug("updateBaliseTitle()");
		if (formation.getBaliseTitle() == null || formation.getBaliseTitle().length() == 0) {
			if (formation.getLibelle() != null && formation.getMotClePrimaire() != null
					&& formation.getCategorie() != null) {
				formation.setBaliseTitle(
						"FORMATION " + formation.getMotClePrimaire() + "," + formation.getLibelle() + " | PLB");
			}
		}
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

	public void setEditMode() {
		mode = EDIT_MODE;
	}

	public void setVisuMode() {
		mode = VISU_MODE;
	}

	public void setEditSessionMode() {
		mode = EDIT_SESSION_MODE;
	}

	public boolean isVisuMode() {
		return mode == VISU_MODE;
	}

	public boolean isEditMode() {
		return mode == EDIT_MODE;
	}

	public boolean isEditSessionMode() {
		return mode == EDIT_SESSION_MODE;
	}

	public boolean isCreationMode() {
		return mode == NEW_MODE;
	}

	public Float getPrixInter() {
		return tarifsInter.get(formation.getCodeTarifInter() + formation.getDuree());
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

	public List<Formation> autresFormationsCategorie() {
		if (formation.getCategorie() != null) {
			return formationDao.findByCategorie(formation.getCategorie());
		}
		return new ArrayList<Formation>();
	}

	public Session getNewSession() {
		return newSession;
	}

	public void setNewSession(Session newSession) {
		this.newSession = newSession;
	}

	/* Ajout d'une catégorie secondaire */
	public void addFormationFiliere() {

		formation.addFormationFiliere(newFormationFiliere);
		newFormationFiliere = new FormationFiliere(formation);
	}

	public void updateRang() {
		
		formation.removeFormationFiliere(newFormationFiliere);
		formation.addFormationFiliere(newFormationFiliere);
		
	}

	public boolean isFormationFiliereExists() {
		return formation.getFormationFilieres().contains(newFormationFiliere);
	}

	public FormationFiliere getNewFormationFiliere() {
		return newFormationFiliere;
	}

	public void addNewFormationFiliere() {
		this.newFormationFiliere = new FormationFiliere(formation);
	}

	public void setNewFormationFiliere(FormationFiliere newFormationFiliere) {
		this.newFormationFiliere = newFormationFiliere;
	}

	public void removeFormationFiliere(FormationFiliere ff) {
		// @ 3.5
		if (formation.getFormationFilieres().size() > 1) {
			boolean wasPrincipal = ff.isPrincipale();
			formation.removeFormationFiliere(ff);

			if (wasPrincipal) {
				formation.getFormationFilieres().get(0).setIsPrincipale("oui");
			}
		}
	}

	public List<Filiere> getSelectableFilieresSecondaires() {
		return filieres.stream().filter(f -> !f.equals(formation.getCategorie().getFiliere()) && !formation.contains(f))
				.collect(Collectors.toList());

	}

	public List<Categorie> getSelectableCategoriesSecondaires() {
		return newFormationFiliere.getFiliere() != null ? 
				categories.stream().filter(c -> c.getFiliere().equals(newFormationFiliere.getFiliere()))
				.collect(Collectors.toList())
				: categories.stream().filter(c -> !formation.contains(c.getFiliere())).collect(Collectors.toList());
	}

	public List<FormationFiliere> autresFormationsFiliere() {
		if (newFormationFiliere.getFiliere() != null) {
			return formationDao.findByFiliere(newFormationFiliere.getFiliere());
		}
		return new ArrayList<FormationFiliere>();
	}

	public List<FormationCategorieDto> autresFormationsNewCategorie() {
		List<FormationCategorieDto> ret = new ArrayList<>();
		if (newFormationFiliere.getCategorie() != null) {
			List<Formation> formations = formationDao.findByCategorie(newFormationFiliere.getCategorie());
			ret.addAll(formations.stream().map(f -> new FormationCategorieDto(f)).collect(Collectors.toList()));

			List<FormationFiliere> formationsFilieres = formationDao
					.findByCategorieSecondaire(newFormationFiliere.getCategorie());
			ret.addAll(
					formationsFilieres.stream().map(ff -> new FormationCategorieDto(ff)).collect(Collectors.toList()));

			Collections.sort(ret);
		}

		return ret;

	}

	public void setPrincipale(FormationFiliere fFiliere) {
		List<FormationFiliere> ffs = formation.getFormationFilieres();
		for (FormationFiliere ff : ffs) {
			ff.setIsPrincipale("non");
		}
		fFiliere.setIsPrincipale("oui");
	}

	public void addNewSession() {
		formation.addSession(newSession);
		newSession = new Session(formation);
	}

	public void removeSession(Session session) {
		formation.removeSession(session);
	}

	public void newSessionDebutChanged() {
		if (newSession.getDebut() != null && newSession.getFin() == null && formation.getDuree() != 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(newSession.getDebut());
			cal.add(Calendar.DAY_OF_YEAR, (int) formation.getDuree());
			newSession.setFin(cal.getTime());
		}
	}

	/* Ajout d'une formation partenaire */
	public void addFormationPartenaire() {
		formation.addFormationPartenaire(newFormationPartenaire);
		newFormationPartenaire = new FormationPartenaire(formation);
		currentSessions = null;
	}

	public FormationPartenaire getNewFormationPartenaire() {
		return newFormationPartenaire;
	}

	public void setNewFormationPartenaire(FormationPartenaire newSessionPartenaire) {
		this.newFormationPartenaire = newSessionPartenaire;
	}

	@RaiseEvent("formationUpdated")
	public void removeFormationPartenaire(FormationPartenaire fp) throws SQLException {
		formation.removeFormationPartenaire(fp);
		// for (Session s : fp.getSessions()) {
		// entityManager.remove(s);
		// }
		// entityManager.remove(fp);

		// Grosse verrue car un programme batch supprime les sessions passées sans
		// prévenir Hibernate
		org.hibernate.Session session = (org.hibernate.Session) entityManager.getDelegate();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();

		Connection c = cp.getConnection();
		PreparedStatement ps = c.prepareStatement(
				"delete from formation_partenaire_formation_session where formation_partenaire_id = ?");
		ps.setInt(1, fp.getId());
		ps.executeUpdate();
		ps.close();
		c.close();
		log.debug("Remove Formation Partenaire " + fp + " for " + this.formation);
		currentSessions = null;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public void offsetYear(int offset) {
		currentYear += offset;
		currentSessions = null;
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

	public List<Formation> getAllFormations() {
		if (allFormations == null) {
			allFormations = formationDao.findAll();
		}
		return allFormations;
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

	@End
	@RaiseEvent("formationUpdated")
	public String deleteFormation() {
		log.debug("About to delete formation " + formation);
		formationDao.deleteFormation(formation);
		historique = null;
		entityManager.flush();
		entityManager.clear();

		return "list";
	}

	@End
	@RaiseEvent("formationUpdated")
	public String archiveFormation() {
		log.debug("About to archive formation " + formation);
		formationDao.archiveFormation(formation);
		historique = null;
		entityManager.flush();
		entityManager.clear();

		return "list";
	}

	private void _storeOldState() {
		oldFormation = formation.getCopy();
		oldFilieres = formation.getFilieres();
		oldFormations = formation.getFormations();
		oldFormationsPartenaire = formation.getCopyFormationsPartenaire();

		newSession = new Session(formation);
		newFormationPartenaire = new FormationPartenaire(formation);
		newFormationFiliere = new FormationFiliere(formation);
	}

	public boolean isShowArgumentaire() {
		return showArgumentaire;
	}

	public void setShowArgumentaire(boolean showArgumentaire) {
		this.showArgumentaire = showArgumentaire;
	}

	public String getKibanaLink() {
		return applicationManager.getKibanaLink(formation.getReference());
	}
}
