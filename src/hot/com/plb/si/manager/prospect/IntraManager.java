package com.plb.si.manager.prospect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.plb.model.Formation;
import com.plb.model.InformationIntra;
import com.plb.model.IntervenantIntra;
import com.plb.model.IntraReferenceSpe;
import com.plb.model.Prospect;
import com.plb.model.ProspectFormation;
import com.plb.model.ReferenceSpe;
import com.plb.model.directory.Account;
import com.plb.model.event.Event;
import com.plb.model.event.IntraCreationEvent;
import com.plb.model.event.IntraModificationEvent;
import com.plb.model.event.IntraNoteEvent;
import com.plb.model.event.IntraValidationEvent;
import com.plb.model.intervenant.Intervenant;
import com.plb.model.message.Message;
import com.plb.si.dto.ProspectUpdate;
import com.plb.si.manager.ApplicationManager;
import com.plb.si.service.AccountDao;
import com.plb.si.service.EventDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.InformationIntraDao;
import com.plb.si.service.IntervenantDao;
import com.plb.si.service.MessageDao;
import com.plb.si.service.NotificationService;
import com.plb.si.util.Labels;

@Name("intraManager")
@Scope(ScopeType.CONVERSATION)
public class IntraManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6013637297689370656L;

	@In
	EntityManager entityManager;

	@In
	FacesMessages facesMessages;

	@In
	Account loggedUser;

	@In(required = false)
	Prospect prospect;

	@Logger
	Log log;

	// permet l'envoi de mail/notification
	@In(create = true)
	NotificationService notificationService;

	@In(create = true)
	ApplicationManager applicationManager;
	
	@In(create=true)
	private List<Formation> formationsActives;


	private Date lastSearchDate;

	InformationIntra intra;

	// Liste de resultat

	private List<InformationIntra> results;

	private int intraModif;

	private List<Event> historique;

	// Attribut pour la recherche

	private List<Intervenant> listeIntervenant;

	private List<Account> listeCommercial;

	private Account commercial;

	private String statut;

	private Formation formation;

	private Intervenant intervenant;

	private boolean all;

	// intra selectionné

	private String intraSelected;

	// Attribut pour les mode visu; edit

	private static int VISU_MODE = 0;
	private static int EDIT_MODE = 1;
	private static int NEW_MODE = 2;
	int mode = EDIT_MODE;

	// Attribut pour remplissage heure formation

	private String heureDeb;

	private String minDeb;

	private String heureFin;

	private String minFin;

	// Attribut de detection d'évolution de statut

	private String statutIntraTemp;

	private int valide;

	// Attribut pour l'affichage

	private boolean afficheIntraModif;

	private boolean menuDeroulant;

	// Attribut pour l'export excel

	private Map<Integer, String> detaisFormation = new HashMap<Integer, String>();

	// Attribut pour l'envoie de mail de confirmation

	private boolean confirm;

	// Attribut pour la redirection final : staut changé ou non

	private boolean statutChange;

	// Liste de prospects é supprimer
	private List<Message> messages;

	private int tailleMessagerie;

	private boolean modif;

	IntervenantDao intervenantDao;
	AccountDao accountDao;
	FormationDao formationDao;
	InformationIntraDao informationIntraDao;
	MessageDao messsageDao;

	// DEBUT FONCTIONS
	@Create
	@Begin(join = true)
	public String init() {
		// Initialisation des listes
		intervenantDao = new IntervenantDao(entityManager);
		accountDao = new AccountDao(entityManager);
		formationDao = new FormationDao(entityManager);
		informationIntraDao = new InformationIntraDao(entityManager);
		messsageDao = new MessageDao(entityManager);

//		historique = new ArrayList<Event>();
		listeIntervenant = intervenantDao.findAll();
		listeCommercial = accountDao.findAllCommercialeActive();
		results = informationIntraDao.findAll();
		intraModif = informationIntraDao.searchModified().size();
		afficheIntraModif = false;
		menuDeroulant = true;
		lastSearchDate = new Date();
		return "/mz/search/searchIntra.xhtml";
	}

	public List<ProspectUpdate> getUpdatedProspects() {
		return applicationManager.getUpdatedProspects(lastSearchDate);
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	/**
	 * Fonction de création d'intra via le formulaire. Démarrage du mode
	 * automatique pour la sauvegarde des données.
	 * 
	 * @return
	 */
	@Begin(join = true, flushMode = FlushModeType.AUTO)
	public String createIntra() {
		// initialisation
		listeCommercial = accountDao.findAllCommercialeActive();

		intra = new InformationIntra();
		_persistIntra();
		
		// Notification
		confirm = false;
		// Recherche du commercial afin de trouver l'Account associé dans le but
		// d'avoir le plus d'informations possible
		for (int i = 0; i < listeCommercial.size(); i++) {
			if (listeCommercial.get(i).getNomComplet()
					.equals(prospect.getProspectDetail().getCommercial())) {
				intra.setCommercial(listeCommercial.get(i));
			}
		}
		// On ajoute la référence a la liste de formation s'il y en a une
		if (prospect.getReference() != null
				&& !("").equals(prospect.getReference())) {
			try {
				if (prospect.getFormations() == null
						|| prospect.getFormations().size() == 0) {
					Formation f = formationDao.findByReference(prospect
							.getReference());
					ProspectFormation proForm = new ProspectFormation();
					proForm.setProspect(prospect);
					proForm.setFormation(f);
					proForm.setSession((prospect.getProspectDetail()
							.getDate_souhaite()));
					proForm.setParticipant(prospect.getProspectDetail()
							.getNb_participants() + "");
					prospect.getFormations().add(proForm);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Partie note Perso
		// Chargement des messages de note perso relatif au prospect selectionné

		try {
			messages = new ArrayList<Message>();
			messages = messsageDao.findAllOrderedByDate(prospect);
			try {
				tailleMessagerie = messages.size();
			} catch (Exception e) {
				tailleMessagerie = 0;
			}
			
			if ( prospect.getCommentaire() != null && !prospect.getCommentaire().isEmpty() ) {
				Message m = new Message();
				m.setLibelle("Remarque : " + prospect.getCommentaire());
				m.setAccount(loggedUser);
				m.setProspect(prospect);
				m.setDate(prospect.getDateCreation());
				messages.add(m);
				modif = true;
			}
			if ( prospect.getConsigne() != null && !prospect.getConsigne().isEmpty() ) {
				Message m = new Message();
				m.setLibelle("Consigne : " + prospect.getConsigne());
				m.setAccount(loggedUser);
				m.setProspect(prospect);
				m.setDate(prospect.getDateCreation());
				messages.add(m);	
				modif = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/mz/Intra/createIntra.xhtml";
	}
	
	private IntraCreationEvent _persistIntra() {
		intra.setDateCreation(new Date());
		intra.setStatutIntra(ApplicationManager.ST_INTRA_RECHERCHE);
		intra.setProspect(prospect);
		intra.setChangementAdminIntra(0);
		intra.setChangementCom(1);
		intra.setValide(0);
		// Enregistrement de l'event création de demande d'intra
		IntraCreationEvent intraCreationEvent = new IntraCreationEvent(loggedUser,
				intra);
		entityManager.persist(intra);
		entityManager.persist(intraCreationEvent);
		
		return intraCreationEvent;
	}

	public String saveIntra() {
		IntraCreationEvent intraCreationEvent = _persistIntra();
		// Envoie de mail pour notifier la création d'un intra
		notificationService.sendToIntervenantManager(intraCreationEvent);
		notificationService.sendMailIntra(1000, intra, intraCreationEvent);
		// si confirmation donc envoie de mail de confirmation au role
		// Intervenant_Manager
		if (confirm == true) {
			Account destinataire = new Account();
			destinataire = intra.getCommercial();
			notificationService.resolveDestinataireUnique(destinataire,
					intraCreationEvent);
			notificationService.sendMailIntra(1000, intra, intraCreationEvent);
		}
		// Enregistrement des éventuelles messages/notes persos
		int tailleMessagerieFinal = messages.size();
		if (tailleMessagerie != tailleMessagerieFinal) {
			String lastTwoMessage = "";
			if (tailleMessagerie == 0) {
				for (int i = 0; i < tailleMessagerieFinal; i++) {
					entityManager.persist(messages.get(i));
				}
			} else {
				for (int i = tailleMessagerie; i < tailleMessagerieFinal; i++) {
					entityManager.persist(messages.get(i));
				}
			}
			try {
				if (tailleMessagerieFinal == 1) {
					lastTwoMessage = "<b style='font-size:12px'>Date : </b>"
							+ Labels.formatDate(messages.get(
									tailleMessagerieFinal - 1).getDate())
							+ " - <b style='font-size:12px'>Emetteur : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getAccount().getNomComplet()
							+ " <br /><b style='font-size:12px'>Message : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getLibelle();
				} else {
					lastTwoMessage = "<b style='font-size:12px'>Date : </b>"
							+ Labels.formatDate(messages.get(
									tailleMessagerieFinal - 2).getDate())
							+ " - <b style='font-size:12px'>Emetteur : </b>"
							+ messages.get(tailleMessagerieFinal - 2)
									.getAccount().getNomComplet()
							+ " <br /><b style='font-size:12px'>Message : </b>"
							+ messages.get(tailleMessagerieFinal - 2)
									.getLibelle() + "<br />";
					lastTwoMessage = lastTwoMessage
							+ "<b style='font-size:12px'>Date : </b>"
							+ Labels.formatDate(messages.get(
									tailleMessagerieFinal - 1).getDate())
							+ " - <b style='font-size:12px'>Emetteur : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getAccount().getNomComplet()
							+ " <br /><b style='font-size:12px'>Message : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getLibelle();
				}
				prospect.setNotePerso(lastTwoMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Events.instance().raiseTransactionSuccessEvent("prospectUpdated",
				prospect);
		results = null;
		historique = null;
		return "/mz/search/searchProspects.xhtml";
	}

	/**
	 * Sélection d'un intra. Modification sur le bouton save. Démarrage du mode
	 * manuel pour la sauvegarde des données.
	 */
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void selectIntra() {
		modif = false;
		try {
			intra = entityManager.find(InformationIntra.class,
					Integer.parseInt(intraSelected));
			mode = VISU_MODE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// intialisation ds champs pour horaire formation
		heureDeb = intra.getOnlyHeureDeb();
		heureFin = intra.getOnlyHeureFin();
		minDeb = intra.getOnlyMinuteDeb();
		minFin = intra.getOnlyMinuteFin();

		statutIntraTemp = intra.getStatutIntra();
		// Envoie de mail de confirmation par défaut à false
		confirm = false;
		// Detection de changement de statut initialiser à false
		statutChange = false;
		// Chargement des messages de note perso relatif au prospect selectionné
		try {
			messages = new ArrayList<Message>();
			messages = messsageDao.findAllOrderedByDate(intra.getProspect());
			try {
				tailleMessagerie = messages.size();
			} catch (Exception e) {
				tailleMessagerie = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		valide = intra.getValide();
	}

	/**
	 * Sauvegarde les données en base.
	 */
	public void modifyIntra() {
		// remplissage heure de formation
		if ( !horaireDefaut() ) {
			horaire();
			intra.setHeureDeb(heureDeb + ":" + minDeb);
			intra.setHeureFin(heureFin + ":" + minFin);
		} else {
			intra.setHeureDeb("");
			intra.setHeureFin("");
		}
		Account destinataire = new Account();
		if (!statutIntraTemp.equals(intra.getStatutIntra())
				&& !ApplicationManager.ST_INTRA_AUDIT.equals(intra.getStatutIntra())
				&& !ApplicationManager.ST_INTRA_RECHERCHE.equals(intra.getStatutIntra())
				&& !ApplicationManager.ST_INTRA_RECHERCHE_MODIFIE.equals(intra.getStatutIntra())) {
			intra.setChangementAdminIntra(1);
			intra.setChangementCom(0);
			intra.setDateModification(new Date());
			// Enregistrement de l'Event modification de l'intra pour
			// l'historique
			IntraModificationEvent intraEvent = new IntraModificationEvent(
					loggedUser, intra);
			entityManager.persist(intraEvent);
			// Envoie de mail pour notifier le changement de statut d'un intra
			destinataire = intra.getCommercial();
			notificationService.resolveDestinataireUnique(destinataire,
					intraEvent);
			notificationService.sendMailIntra(1000, intra, intraEvent, false);
			// si confirmation donc envoie de mail de confirmation au role
			// Intervenant_Manager
			if (confirm == true) {
				notificationService.sendToIntervenantManager(intraEvent);
				notificationService
						.sendMailIntra(1000, intra, intraEvent, true);
			}
		} else if (!statutIntraTemp.equals(intra.getStatutIntra())
				&& (ApplicationManager.ST_INTRA_AUDIT.equals(intra.getStatutIntra())
						|| ApplicationManager.ST_INTRA_RECHERCHE.equals(intra.getStatutIntra()) || ApplicationManager.ST_INTRA_RECHERCHE_MODIFIE
							.equals(intra.getStatutIntra()))) {
			intra.setChangementAdminIntra(0);
			intra.setChangementCom(1);
			intra.setDateModification(new Date());
			// Enregistrement de l'Event modification de l'intra pour
			// l'historique
			IntraModificationEvent intraEvent = new IntraModificationEvent(
					loggedUser, intra);
			entityManager.persist(intraEvent);
			// Envoie de mail pour notifier le changement de statut d'un intra
			if ( intra.getCommercial() != null ) {
				destinataire = intra.getCommercial();
				notificationService.resolveDestinataireUnique(destinataire,
						intraEvent);
				notificationService.sendMailIntra(1000, intra, intraEvent, false);
			}
			// si confirmation donc envoie de mail de confirmation au role
			// Intervenant_Manager
			if (confirm == true) {
				notificationService.sendToIntervenantManager(intraEvent);
				notificationService
						.sendMailIntra(1000, intra, intraEvent, true);
			}
		}
		if (intra.getValide() == 1 && valide == 0) {
			intra.setChangementAdminIntra(1);
			intra.setChangementCom(1);
			if (all == false) {
				for (int i = 0; i < results.size(); i++) {
					if (intra.getIdInforamtionIntra() == results.get(i)
							.getIdInforamtionIntra()) {
						results.remove(i);
					}
				}
			}
			intra.setDateModification(new Date());
			IntraValidationEvent intraValide = new IntraValidationEvent(
					loggedUser, intra);
			entityManager.persist(intraValide);
			// Envoie de mail pour notifier la validation de l'intra
			destinataire = intra.getCommercial();
			notificationService.resolveDestinataireUnique(destinataire,
					intraValide);
			notificationService.sendMailIntra(1000, intra,intraValide);
			// si confirmation donc envoie de mail de confirmation de validation
			// au role Intervenant_Manager
			if (confirm == true) {
				notificationService.sendToIntervenantManager(intraValide);
				notificationService.sendMailIntra(1000, intra,	intraValide);
			}
		}
		log.debug(loggedUser + " Intra modifié");
		// Reactualisation de la liste d'intra non modifié
		intraModif = informationIntraDao.searchModified().size();
		// Enregistrement des éventuelles messages/notes persos
		int tailleMessagerieFinal = messages.size();
		if (tailleMessagerie != tailleMessagerieFinal) {
			log.debug(loggedUser + " Ajout de Note détecté");
			String lastTwoMessage = "";
			List<Message> newMessages = new ArrayList<Message>();
			for (int i = tailleMessagerie; i < tailleMessagerieFinal; i++) {
				entityManager.persist(messages.get(i));
				newMessages.add(messages.get(i));
			}
			IntraNoteEvent intraNoteEvent = new IntraNoteEvent(loggedUser, intra, newMessages);
			entityManager.persist(intraNoteEvent);
			notificationService.resolveDestinataires(loggedUser, intraNoteEvent);
			notificationService.sendMailIntra(1000, intra,intraNoteEvent);
			try {
				if (tailleMessagerieFinal == 1) {
					lastTwoMessage = "<b style='font-size:12px'>Date : </b>"
							+ Labels.formatDate(messages.get(
									tailleMessagerieFinal - 1).getDate())
							+ " - <b style='font-size:12px'>Emetteur : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getAccount().getNomComplet()
							+ " <br /><b style='font-size:12px'>Message : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getLibelle();
				} else {
					lastTwoMessage = "<b style='font-size:12px'>Date : </b>"
							+ Labels.formatDate(messages.get(
									tailleMessagerieFinal - 2).getDate())
							+ " - <b style='font-size:12px'>Emetteur : </b>"
							+ messages.get(tailleMessagerieFinal - 2)
									.getAccount().getNomComplet()
							+ " <br /><b style='font-size:12px'>Message : </b>"
							+ messages.get(tailleMessagerieFinal - 2)
									.getLibelle() + "<br />";
					lastTwoMessage = lastTwoMessage
							+ "<b style='font-size:12px'>Date : </b>"
							+ Labels.formatDate(messages.get(
									tailleMessagerieFinal - 1).getDate())
							+ " - <b style='font-size:12px'>Emetteur : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getAccount().getNomComplet()
							+ " <br /><b style='font-size:12px'>Message : </b>"
							+ messages.get(tailleMessagerieFinal - 1)
									.getLibelle();
				}
				intra.getProspect().setNotePerso(lastTwoMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// re parametrage du mode de saisie
		mode = VISU_MODE;
		entityManager.flush();
		Events.instance().raiseTransactionSuccessEvent("prospectUpdated",
				prospect);
		results = null;
		historique = null;
	}

	// Permet d'ajouter dynamiqument une note perso/Message
	public void addNote() {
		log.debug("addNote()");
		Message m = new Message();
		m.setDate(new Date());
		m.setProspect(intra.getProspect());
		m.setAccount(loggedUser);
		messages.add(m);
		modif = true;
	}

	public void addNote2() {
		log.debug("addNote2()");
		Message m = new Message();
		m.setDate(new Date());
		m.setProspect(prospect);
		m.setAccount(loggedUser);
		messages.add(m);
		modif = true;
	}

	// rajoute une ligne lors de l'ajout d'une reference
	public void addReference() {
		log.debug("addReference()");
		ProspectFormation p = new ProspectFormation();
		p.setProspect(prospect);
		prospect.getFormations().add(p);
	}

	public void addReferenceForModif() {
		log.debug("addReferenceForModif()");
		ProspectFormation p = new ProspectFormation();
		p.setProspect(intra.getProspect());
		intra.getProspect().getFormations().add(p);
	}

	public void addSpe() {
		log.debug("addSpe()");
		IntraReferenceSpe i = new IntraReferenceSpe();
		i.setInformationIntra(intra);
		ReferenceSpe r = new ReferenceSpe();
		entityManager.persist(r);
		i.setReferenceSpe(r);
		intra.getIntraReferenceSpe().add(i);
	}

	// rajoute une ligne lors de l'ajout d'un intervenant
	public void addIntervenant(int index, int indexI) {
		log.debug("addIntervenant()");
		IntervenantIntra i = new IntervenantIntra();
		i.setProspectFormation(intra.getProspect().getFormations().get(index));
		intra.getProspect().getFormations().get(index).getIntervenants().add(i);
	}

	public void addIntervenantSpe(int index, int indexI) {
		log.debug("addIntervenantSpe()");
		IntervenantIntra i = new IntervenantIntra();
		i.setIntraReferenceSpe(intra.getIntraReferenceSpe().get(index));
		intra.getIntraReferenceSpe().get(index).getIntervenants().add(i);
	}

	// supprime une ligne dans le tableau de sélection de formation
	public void removeReference(int index) {
		log.debug("removeReference()");
		entityManager.remove(prospect.getFormations().get(index));
		prospect.getFormations().remove(index);
	}

	public void removeReferenceForModif(int index) {
		log.debug("removeReferenceForModif()");
		entityManager.remove(intra.getProspect().getFormations().get(index));
		intra.getProspect().getFormations().remove(index);
	}

	public void removeSpe(int index) {
		log.debug("removeSpe()");
		entityManager.remove(intra.getIntraReferenceSpe().get(index));
		intra.getIntraReferenceSpe().remove(index);
	}

	// supprime une ligne dans le tableau de sélection d'intervenant
	public void removeIntervenant(String id, int index, int indexI) {
		log.debug("removeIntervenant()");
		IntervenantIntra i = entityManager
				.find(ProspectFormation.class, Integer.parseInt(id))
				.getIntervenants().get(indexI);
		entityManager.remove(i);
		intra.getProspect().getFormations().get(index).getIntervenants()
				.remove(indexI);
	}

	public void removeIntervenantSpe(String id, int index, int indexI) {
		log.debug("removeIntervenantSpe()");
		IntervenantIntra i = entityManager
				.find(IntraReferenceSpe.class, Integer.parseInt(id))
				.getIntervenants().get(indexI);
		entityManager.remove(i);
		intra.getIntraReferenceSpe().get(index).getIntervenants()
				.remove(indexI);
	}

	// Fonction de recherche
	public void recherche() {
		results = informationIntraDao.search(commercial, intervenant, statut,
				all, formation);
		lastSearchDate = new Date();
	}

	// Fonction de rafraichissement prend tous les objets liés et les
	// rafraichient
	public void refresh() {
		entityManager.clear();
		recherche();
		intraModif = informationIntraDao.searchModified().size();
		listeIntervenant = intervenantDao.findAll();
	}

	// Fonction de remise à zero
	public void reset() {
		commercial = null;
		intervenant = null;
		formation = null;
		all = false;
		statut = null;
		// Rafraichissement de la mémoir cache pour avoir les derniere données
		// provenant de la base
		results = informationIntraDao.findAll();
	}

	public void horaire() {
		if (heureDeb.length() == 1) {
			heureDeb = "0" + heureDeb;
		}
		if (heureFin.length() == 1) {
			heureFin = "0" + heureFin;
		}
		if (minDeb.length() == 1) {
			minDeb = "0" + minDeb;
		}
		if (minFin.length() == 1) {
			minFin = "0" + minFin;
		}
	}

	public boolean horaireDefaut() {
		boolean rep = false;
		if ("0".equals(heureDeb) && "0".equals(heureFin) && "0".equals(minDeb)
				&& "0".equals(minFin)) {
			rep = true;
		}
		return rep;
	}

	// Fonction permettant d'aattribuer une couleur a un statut de prospect
	public String coloriage(String _statut) {
		String couleur = "";
		
		if (ApplicationManager.ST_INTRA_ANNULE.equals(_statut)) {
			couleur = " #ffff66";
		} else if (ApplicationManager.ST_INTRA_RECHERCHE_MODIFIE.equals(_statut)) {
			couleur = "#ffa343";
		} else if (ApplicationManager.ST_INTRA_AUDIT.equals(_statut)) {
			couleur = "#01B0F0";
		} else if (ApplicationManager.ST_INTRA_LOGISTIQUE.equals(_statut)) {
			couleur = "#4166f5 ";
		} else if (ApplicationManager.ST_INTRA_RECHERCHE.equals(_statut)) {
			couleur = "#fe6f5e";
		} else if (ApplicationManager.ST_INTRA_OK.equals(_statut)) {
			couleur = "#98fb98";
		}
		return couleur;
	}

	// fonction pour l'export excel : traitement de certaines informations
	// impossible à afficher (Liste de formations)
	public String exportExcel() {
		String details = "";
		for (int i = 0; i < results.size(); i++) {
			details = "";
			for (int j = 0; j < results.get(i).getProspect().getFormations()
					.size(); j++) {
				ProspectFormation prospectFormation = results.get(i).getProspect().getFormations().get(j);
				log.debug(prospectFormation);
				if ( prospectFormation.getFormation()== null) {
					continue;
				}
				details = details
						+ " Référence : "
						+ prospectFormation.getFormation().getReference();
				details = details
						+ " / Libellé : "
						+ prospectFormation.getFormation().getLibelle();
				details = details
						+ " / Durée : "
						+ prospectFormation.getDureeVoulu();
				details = details
						+ " / Date : "
						+ prospectFormation.getSession();
				details = details
						+ " / Participant(s) : "
						+ prospectFormation.getParticipant();
				details = details + " \n/";
			}
			for (int j = 0; j < results.get(i).getIntraReferenceSpe().size(); j++) {
				details = details + " Référence : " + "Spé";
				details = details
						+ " / Libellé : "
						+ results.get(i).getIntraReferenceSpe().get(j)
								.getReferenceSpe().getLibelle();
				details = details
						+ " / Durée : "
						+ results.get(i).getIntraReferenceSpe().get(j)
								.getReferenceSpe().getDuree();
				details = details
						+ " / Date : "
						+ results.get(i).getIntraReferenceSpe().get(j)
								.getReferenceSpe().getSession();
				details = details
						+ " / Participant(s) : "
						+ results.get(i).getIntraReferenceSpe().get(j)
								.getReferenceSpe().getParticipant();
				details = details + " \n/";
			}
			detaisFormation
					.put(results.get(i).getIdInforamtionIntra(), details);
		}
		return "/mz/searchIntra_xls.xhtml";
	}

	public void showModif() {
		afficheIntraModif = true;
		results = informationIntraDao.searchModified();
	}


	public String findTimeLapse(Date date, Integer valide) {
		Long tempEcoule = null;
		String resultat = "";
		int nbJours = 0;
		Long reste = null;
		try {
			if (date != null && valide == 0) {
				Date dateActuelle = new Date();
				tempEcoule = dateActuelle.getTime() - date.getTime();
				tempEcoule = tempEcoule / 3600000;
				if (tempEcoule > 24) {
					nbJours = (int) (tempEcoule / 24);
					reste = tempEcoule % 24;
					resultat = nbJours + " jours et " + reste + " heures";
				} else {
					resultat = tempEcoule + " heures";
				}
			} else {
				resultat = "N/C";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultat;
	}

	// FOnction permettant de rediriger vers le bon bouton de validation en
	// analysant le changement de
	// statut de la demande d'intra
	public void statutChange() {
		if (!statutIntraTemp.equals(intra.getStatutIntra())) {
			statutChange = true;
		} else {
			statutChange = false;
		}
	}

	// GETTEURS AND SETTEURS

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}

	public InformationIntra getIntra() {
		return intra;
	}

	public void setIntra(InformationIntra intra) {
		this.intra = intra;
	}

	public List<Intervenant> getListeIntervenant() {
		return listeIntervenant;
	}

	public void setListeIntervenant(List<Intervenant> listeIntervenant) {
		this.listeIntervenant = listeIntervenant;
	}

	public List<Account> getListeCommercial() {
		return listeCommercial;
	}

	public void setListeCommercial(List<Account> listeCommercial) {
		this.listeCommercial = listeCommercial;
	}


	public List<InformationIntra> getResults() {
		if (results == null) {
			recherche();
		}
		return results;
	}

	public void setResults(List<InformationIntra> results) {
		this.results = results;
	}

	public String getIntraSelected() {
		return intraSelected;
	}

	public void setIntraSelected(String intraSelected) {
		this.intraSelected = intraSelected;
	}

	public static int getVISU_MODE() {
		return VISU_MODE;
	}

	public static void setVISU_MODE(int vISU_MODE) {
		VISU_MODE = vISU_MODE;
	}

	public static int getEDIT_MODE() {
		return EDIT_MODE;
	}

	public static void setEDIT_MODE(int eDIT_MODE) {
		EDIT_MODE = eDIT_MODE;
	}

	public void setEditMode() {
		mode = EDIT_MODE;
	}

	public static int getNEW_MODE() {
		return NEW_MODE;
	}

	public static void setNEW_MODE(int nEW_MODE) {
		NEW_MODE = nEW_MODE;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public boolean isVisuMode() {
		return mode == VISU_MODE;
	}

	public boolean isEditMode() {
		return mode == EDIT_MODE;
	}

	public boolean isCreationMode() {
		return mode == NEW_MODE;
	}

	public Account getCommercial() {
		return commercial;
	}

	public void setCommercial(Account commercial) {
		this.commercial = commercial;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Intervenant getIntervenant() {
		return intervenant;
	}

	public void setIntervenant(Intervenant intervenant) {
		this.intervenant = intervenant;
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public String getHeureDeb() {
		return heureDeb;
	}

	public void setHeureDeb(String heureDeb) {
		this.heureDeb = heureDeb;
	}

	public String getMinDeb() {
		return minDeb;
	}

	public void setMinDeb(String minDeb) {
		this.minDeb = minDeb;
	}

	public String getHeureFin() {
		return heureFin;
	}

	public void setHeureFin(String heureFin) {
		this.heureFin = heureFin;
	}

	public String getMinFin() {
		return minFin;
	}

	public void setMinFin(String minFin) {
		this.minFin = minFin;
	}

	public String getStatutIntraTemp() {
		return statutIntraTemp;
	}

	public void setStatutIntraTemp(String statutIntraTemp) {
		this.statutIntraTemp = statutIntraTemp;
	}

	public int getIntraModif() {
		return intraModif;
	}

	public void setIntraModif(int intraModif) {
		this.intraModif = intraModif;
	}

	public boolean isAfficheIntraModif() {
		return afficheIntraModif;
	}

	public void setAfficheIntraModif(boolean afficheIntraModif) {
		this.afficheIntraModif = afficheIntraModif;
	}

	public boolean isMenuDeroulant() {
		return menuDeroulant;
	}

	public void setMenuDeroulant(boolean menuDeroulant) {
		this.menuDeroulant = menuDeroulant;
	}

	public void changeMenuDeroulant() {
		if (menuDeroulant == false) {
			menuDeroulant = true;
		} else {
			menuDeroulant = false;
		}
	}

	public Map<Integer, String> getDetaisFormation() {
		return detaisFormation;
	}

	public void setDetaisFormation(Map<Integer, String> detaisFormation) {
		this.detaisFormation = detaisFormation;
	}

	public Formation getFormation() {
		return formation;
	}

	public void setFormation(Formation formation) {
		this.formation = formation;
	}

	public List<Event> getHistorique() {
		if (historique == null && intra != null) {
			EventDao eventDao = new EventDao(entityManager);
			historique = eventDao.findAll(intra);
		}
		return historique;
	}

	public void setHistorique(List<Event> historique) {
		this.historique = historique;
	}

	public boolean isStatutChange() {
		return statutChange;
	}

	public void setStatutChange(boolean statutChange) {
		this.statutChange = statutChange;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public int getTailleMessagerie() {
		return tailleMessagerie;
	}

	public void setTailleMessagerie(int tailleMessagerie) {
		this.tailleMessagerie = tailleMessagerie;
	}

	public boolean isModif() {
		return modif;
	}

	public void setModif(boolean modif) {
		this.modif = modif;
	}

	public int getValide() {
		return valide;
	}

	public void setValide(int valide) {
		this.valide = valide;
	}
}
