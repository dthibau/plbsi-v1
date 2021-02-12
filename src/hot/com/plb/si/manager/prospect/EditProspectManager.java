package com.plb.si.manager.prospect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.plb.dto.FormationDto;
import com.plb.dto.SessionOrganismeDto;
import com.plb.model.Formation;
import com.plb.model.InformationIntra;
import com.plb.model.IntraReferenceSpe;
import com.plb.model.Prospect;
import com.plb.model.ProspectCritere;
import com.plb.model.ProspectDetail;
import com.plb.model.ProspectFormation;
import com.plb.model.ReferenceSpe;
import com.plb.model.TypeContact;
import com.plb.model.devis.Devis;
import com.plb.model.directory.Account;
import com.plb.model.event.Event;
import com.plb.model.event.IntraModificationEvent;
import com.plb.model.event.IntraNoteEvent;
import com.plb.model.event.ProspectModificationEvent;
import com.plb.model.message.Message;
import com.plb.si.dto.ProspectDto;
import com.plb.si.manager.ApplicationManager;
import com.plb.si.manager.DevisManager;
import com.plb.si.service.AccountDao;
import com.plb.si.service.DevisDao;
import com.plb.si.service.EventDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.MessageDao;
import com.plb.si.service.NotificationService;
import com.plb.si.service.ProspectDao;
import com.plb.si.util.Labels;

@Name("editProspectManager")
@Scope(ScopeType.CONVERSATION)
public class EditProspectManager implements Serializable {

	@Out
	public static String ST_NON_AFFECTE = SeamResourceBundle.getBundle()
			.getString("prospect.nonAffecte");
	@Out
	public static String ST_EN_ATTENTE = "En attente";
	@Out
	public static String ST_EN_COURS = "En cours";
	@Out
	public static String ST_ABANDON = "Abandon";
	@Out
	public static String ST_GAGNE = SeamResourceBundle.getBundle().getString(
			"prospect.gagne");
	@Out
	public static String ST_PERDU = "Perdu";
	@Out
	public static String ST_DOUBLON= SeamResourceBundle.getBundle().getString(
			"prospect.gagne");
	/**
	 * 
	 */
	private static final long serialVersionUID = -2111581844586442415L;

	@In
	EntityManager entityManager;

	@In
	FacesMessages facesMessages;

	@In
	Account loggedUser;

	@Logger
	Log log;

	// permet l'envoi de mail/notification
	@In(create = true)
	NotificationService notificationService;

	@In(create = true)
	DevisManager devisManager;

	@In(create = true)
	ApplicationManager applicationManager;

	@In(create = true)
	List<TypeContact> typesContact;

	@In(create = true)
	private List<Formation> formationsActives;

	@Out(required = false)
	private Prospect prospect;

	@Out(required = false)
	private Prospect prospectDelete;

	// Attribut pour l'annulation de la prospect
	private Prospect prospectTemp;

	private ProspectDetail prospectDetailTemp;

	// Libellé formation
	@Out(required = false)
	private String libelle;

	// Affichage nom participants
	private boolean afficheP;

	// Liste pour le menu déroulant
	private List<Account> listeCommerciale;

	private List<FormationDto> adWordsResult;

	// liste de resultats initiaux
	private List<ProspectDto> results;

	// Affichage date session
	private List<SessionOrganismeDto> listeSession;

	// Liste de prospects � supprimer
	private List<Message> messages;

	private int tailleMessagerie;

	private boolean modif;

	// Attributs pour la recherche

	private Account commercialeR;

	private String reference;

	private Float montantTotal;

	private int nbProspectSigne;

	private boolean afficheObjectif;

	private String typo;

	private TypeContact typeContact;

	private Date dateDebut;
	private Date dateFin;

	// Attribut pour remplissage heure formation

	private String heureDeb;

	private String minDeb;

	private String heureFin;

	private String minFin;

	// Attribut pour l'envoie de mail de confirmation

	private boolean confirm;

	// Attribut pour les mode visu; edit
	private static int VISU_MODE = 0;
	private static int EDIT_MODE = 1;
	private static int NEW_MODE = 2;
	int mode = EDIT_MODE;

	// Message expliquant que le bouton est grisé
	private String buttonMsg;

	// Attribut pour savoir si l'on affiche le calendrier pour la date d'envoie
	// du devis
	private boolean envoye;
	
	private boolean relance;

	private Devis lastGeneratedDevis;

	// Attribut pour gérer l'envoie de mail
	private String ancienCommercial;

	// Analyse de Spam
	private Map<Integer, Boolean> spam = new HashMap<Integer, Boolean>();

	// Prix formation
	private Map<String, Float> prix = new HashMap<String, Float>();

	private float prixFormation;

	private String lastRef;

	// Annulation intra

	private String statutIntraTemp;

	AccountDao accountDao;
	FormationDao formationDao;
	ProspectDao prospectDao;
	EventDao eventDao;
	MessageDao messageDao;
	DevisDao devisDao;

	List<Event> historique;

	ProspectCritere critere = new ProspectCritere();

	// Fonction d'initialisation
	@Create
	public void _init() {


		accountDao = new AccountDao(entityManager);
		prospectDao = new ProspectDao(entityManager);
		formationDao = new FormationDao(entityManager);
		eventDao = new EventDao(entityManager);
		messageDao = new MessageDao(entityManager);
		devisDao = new DevisDao(entityManager);

		listeCommerciale = accountDao.findAllCommercialeActive();

		adWordsResult = FormationDto.buildDtos(formationsActives);
		// Recuperation des prix des formations
		for (int i = 0; i < adWordsResult.size(); i++) {
			prix.put(adWordsResult.get(i).getFormation().getReference(),
					adWordsResult.get(i).getFormation().getPrix());
		}

	}

	// Selectionne la prospect a modifier
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void selectProspect(Prospect p) {

		// _init();
		afficheP = false;
		confirm = false;
		modif = false;
		try {
			prospect = entityManager.find(Prospect.class, p.getIdProspect());
			historique = null;
			mode = VISU_MODE;
		} catch (Exception e) {
			log.error("Une erreur est survenue : " + e);
		}
		log.debug(loggedUser + ": selectPropsect : " + prospect + " : "
				+ prospect.getNom());
		if (prospect.getProspectDetail() == null) {
			prospect.setProspectDetail(new ProspectDetail());
			// On renseigne le commercial associé au prospect pour savoir s'il a
			// été modifié
			ancienCommercial = "";
			envoye = false;
			relance = false;
		} else {
			// On renseigne le commercial associé au prospect pour savoir s'il a
			// été modifié
			ancienCommercial = prospect.getProspectDetail().getCommercial();

			envoye = (prospect.getProspectDetail().getDatedevis() != null);
			relance = (prospect.getProspectDetail().getDateRelance() != null);
			
		}
		// Last Devis généré
		_setLastGeneratedDevis();

		messages = messageDao.findAllOrderedByDate(prospect);

		// Stockage temporaire
		prospectTemp = prospect.clone();
		prospectDetailTemp = prospect.getProspectDetail().clone();
		lastRef = prospect.getReference();

		// trouve les sessions associ�es � la reference du prospect
		_findSession();
		findPrix();
		// Mise � niveau de l'heure si une demande d'intra existe
		// intialisation ds champs pour horaire formation
		if (prospect.getInformationIntra() != null) {
			heureDeb = "";
			heureFin = "";
			minDeb = "";
			minFin = "";
			if (prospect.getInformationIntra().getHeureDeb() != null
					&& !":".equals(prospect.getInformationIntra().getHeureDeb())) {
				try {
					String[] t = prospect.getInformationIntra().getHeureDeb()
							.split(":");
					if (t.length == 2) {
						heureDeb = t[0];
						minDeb = t[1];
					}
				} catch (Exception e) {
					log.debug(loggedUser + " STACKTRACE indexOf HeureDebut "
							+ prospect.getInformationIntra().getHeureDeb());
					e.printStackTrace();
				}
			}
			if (prospect.getInformationIntra().getHeureFin() != null
					&& !":".equals(prospect.getInformationIntra().getHeureFin())) {
				try {
					String[] t = prospect.getInformationIntra().getHeureFin()
							.split(":");
					if (t.length == 2) {
						heureFin = t[0];
						minFin = t[1];
					}
				} catch (Exception e) {
					log.debug(loggedUser + " STACKTRACE indexOf Heurefin : "
							+ prospect.getInformationIntra().getHeureFin());
					e.printStackTrace();
				}
			}
			statutIntraTemp = prospect.getInformationIntra().getStatutIntra();
		}
	}

	// Fonction de modification de propsect
	public void modifyProspect() {
		log.debug(loggedUser + ": modifyPropsect" + prospect + " : "
				+ prospect.getNom());

		if (prospect.getInformationIntra() != null) {
			_modifyIntra();
		}
		if ( !ST_PERDU.equals(prospect.getStatut()) ) {
			prospect.getProspectDetail().setRaisonPerte("");
		}
		// Si changement de statut et montant null
		if (!((ST_GAGNE).equals(prospect.getStatut()) && prospect
				.getProspectDetail().getMontant() == null)) {
			// Modification de l'objet prospect
			prospect.setDateModification(new Date());
			// gere date envoie devis
			if (envoye == false) {
				prospect.getProspectDetail().setDatedevis(null);
			}
			// Passe le statut du prospect à "En cours" quand le devis est
			// envoyé
			if (ST_NON_AFFECTE.equals(prospect.getStatut())
					|| ST_EN_ATTENTE.equals(prospect.getStatut())) {
				if (prospect.getProspectDetail().getDatedevis() != null) {
					prospect.setStatut(ST_EN_COURS);
				}
			}
			log.debug(loggedUser + ": Prospect modifié" + prospect);

			// Enregistrement de l'Event modification de prospect
			Event prospectModificationEvent = new ProspectModificationEvent(
					loggedUser, prospect);
			entityManager.persist(prospectModificationEvent);
			results = null;

			if (prospect.getProspectDetail().getCommercial() != null
					&& !prospect.getProspectDetail().getCommercial()
							.equals(ancienCommercial)
					&& !"".equals(ancienCommercial)) {
				log.debug(loggedUser + ":Envoie de mail Modif !");
				Account newCommercial = null;
				Account lastCommercial = null;
				for (int i = 0; i < listeCommerciale.size(); i++) {
					if (listeCommerciale
							.get(i)
							.getNomComplet()
							.equals(prospect.getProspectDetail()
									.getCommercial())) {
						newCommercial = listeCommerciale.get(i);
					}
					if (listeCommerciale.get(i).getNomComplet()
							.equals(ancienCommercial)) {
						lastCommercial = listeCommerciale.get(i);
					}
				}
				if (newCommercial != null) {
					notificationService.resolveDestinatairesProspect(
							newCommercial, lastCommercial,
							prospectModificationEvent);
					notificationService.sendMailProspect(1000, prospect,
							prospectModificationEvent);
				}
			} else if (prospect.getProspectDetail().getCommercial() != null
					&& prospect.getProspectDetail().getCommercial()
							.equals(ancienCommercial) == false
					&& "".equals(ancienCommercial)) {
				log.debug(loggedUser + ":Envoie de mail Creation !");
				Account newCommercial = null;
				for (int i = 0; i < listeCommerciale.size(); i++) {
					if (listeCommerciale
							.get(i)
							.getNomComplet()
							.equals(prospect.getProspectDetail()
									.getCommercial())) {
						newCommercial = listeCommerciale.get(i);
					}
				}
				if (newCommercial != null) {
					notificationService.resolveDestinatairesProspect(
							newCommercial, null, prospectModificationEvent);
					notificationService.sendMailProspect(1000, prospect,
							prospectModificationEvent);
				}
			}
		}
		// Enregistrement des �ventuelles messages/notes persos
		int tailleMessagerieFinal = messages.size();

		List<Message> newMessages = new ArrayList<Message>();
		boolean bNewNote = false;
		for (Message message : messages) {
			if (!entityManager.contains(message)) {
				bNewNote = true;

				entityManager.persist(message);
				newMessages.add(message);
			}
		}

		if (bNewNote) {
			log.debug(loggedUser + " Ajout de Note détecté");

			if (prospect.getInformationIntra() != null) {
				InformationIntra intra = prospect.getInformationIntra();
				IntraNoteEvent intraNoteEvent = new IntraNoteEvent(loggedUser,
						intra, newMessages);
				entityManager.persist(intraNoteEvent);
				notificationService.resolveDestinataires(loggedUser,
						intraNoteEvent);
				notificationService.sendMailIntra(1000, intra, intraNoteEvent);
			}

			String lastTwoMessage = "";
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
				log.debug(loggedUser + " STACKTRACE");
				e.printStackTrace();
			}
		}
		// re parametrage du mode de saisie
		mode = VISU_MODE;
		entityManager.flush();
		Events.instance().raiseTransactionSuccessEvent("prospectUpdated",
				prospect);
	}

	private void _modifyIntra() {
		// If statut changed in Offre, update statut in intra
		if (!prospectTemp.getStatut().equals(prospect.getStatut())) {
			// Update alose intra statut if exist
			if (prospect.getStatut().equals(ST_GAGNE)) {
				prospect.getInformationIntra().setStatutIntra(
						ApplicationManager.ST_INTRA_LOGISTIQUE);
			}
			if (prospect.getStatut().equals(ST_PERDU)
					|| prospect.getStatut().equals(ST_ABANDON) || prospect.getStatut().equals(ST_DOUBLON) ) {
				prospect.getInformationIntra().setStatutIntra(
						ApplicationManager.ST_INTRA_ANNULE);
				if (prospect.getInformationIntra().getRaisonPerte() == null
						|| prospect.getInformationIntra().getRaisonPerte()
								.isEmpty()) {
					prospect.getInformationIntra().setRaisonPerte(
							"Prospect perdu ou abandonné");
				}
			}
		}

		// Change commercial in intra
		Account commercial = _getCommercialByName(prospect.getProspectDetail()
				.getCommercial());
		prospect.getInformationIntra().setCommercial(commercial);

		// Change horaires
		if (horaireDefaut() == false) {
			horaire();
			prospect.getInformationIntra().setHeureDeb(heureDeb + ":" + minDeb);
			prospect.getInformationIntra().setHeureFin(heureFin + ":" + minFin);
		} else {
			prospect.getInformationIntra().setHeureDeb("");
			prospect.getInformationIntra().setHeureFin("");
		}

		// Detect change in statutIntra to send notifications
		if (!statutIntraTemp.equals(prospect.getInformationIntra()
				.getStatutIntra())
				&& !prospect.getInformationIntra().getStatutIntra()
						.equals(ApplicationManager.ST_INTRA_COMMERCIAL)) {
			prospect.getInformationIntra().setChangementAdminIntra(0);
			prospect.getInformationIntra().setChangementCom(1);
			prospect.getInformationIntra().setDateModification(new Date());
			// Enregistrement de l'Event modification de l'intra pour
			// l'historique
			IntraModificationEvent intraEvent = new IntraModificationEvent(
					loggedUser, prospect.getInformationIntra());
			entityManager.persist(intraEvent);

			// Envoie de mail pour notifier le changement de statut d'un
			// prospect
			notificationService.sendToIntervenantManager(intraEvent);
			notificationService.sendMailIntra(1000,
					prospect.getInformationIntra(), intraEvent, false);
			// si confirmation donc envoie de mail de confirmation au
			// commercial associ�
			if (confirm == true) {
				Account destinataire = new Account();
				destinataire = prospect.getInformationIntra().getCommercial();
				notificationService.resolveDestinataireUnique(destinataire,
						intraEvent);
				notificationService.sendMailIntra(1000,
						prospect.getInformationIntra(), intraEvent, true);
			}
		} else if (!statutIntraTemp.equals(prospect.getInformationIntra()
				.getStatutIntra())
				&& prospect.getInformationIntra().getStatutIntra()
						.equals(ApplicationManager.ST_INTRA_COMMERCIAL)) {
			prospect.getInformationIntra().setChangementAdminIntra(1);
			prospect.getInformationIntra().setChangementCom(0);
			prospect.getInformationIntra().setDateModification(new Date());
			// Enregistrement de l'Event modification de l'intra pour
			// l'historique
			IntraModificationEvent intraEvent = new IntraModificationEvent(
					loggedUser, prospect.getInformationIntra());
			entityManager.persist(intraEvent);
			// Envoie de mail pour notifier le changement de statut d'un
			// prospect
			notificationService.sendToIntervenantManager(intraEvent);
			notificationService.sendMailIntra(1000,
					prospect.getInformationIntra(), intraEvent, false);
			// si confirmation donc envoie de mail de confirmation au
			// commercial associ�
			if (confirm == true) {
				Account destinataire = new Account();
				destinataire = prospect.getInformationIntra().getCommercial();
				notificationService.resolveDestinataireUnique(destinataire,
						intraEvent);
				notificationService.sendMailIntra(1000,
						prospect.getInformationIntra(), intraEvent, true);
			}
		}

	}

	// Permet l'annulation de la modification
	public void cancel() {
		try {
			entityManager.refresh(prospect);
		} catch (PersistenceException e ) {
			log.error("Refrehing entity did not work :" + e + ". Trying to refresh the whole list !");
			Events.instance().raiseEvent("refreshNeeded");
		}
	}

	// permet l'aannulation lors de la saisie du montant
	public void cancelMontant() {
		prospect.setStatut(prospectTemp.getStatut());
		prospect.getProspectDetail()
				.setMontant(prospectDetailTemp.getMontant());
	}

	// Suppresion du prospect via la pop up
	public void deleteProspect() {
		log.debug(loggedUser + " deleteProspect prospectDelete="
				+ prospectDelete);
		if (prospectDelete != null) {
			EventDao ev = new EventDao(entityManager);
			ev.deletEventProspect(prospectDelete);
			MessageDao msgDao = new MessageDao(entityManager);
			msgDao.deleteMessages(prospect);
			entityManager.remove(prospectDelete);
			log.info("Prospect supprimé : " + prospectDelete);
			results = null;
		}
	}

	// Fonction qui trouve le libelle en rapport avec la référence
	public void findLibelle() {
		if (prospect.getReference() != null) {
			for (int i = 0; i < formationsActives.size(); i++) {
				if (formationsActives.get(i).getReference()
						.equals(prospect.getReference())) {
					libelle = formationsActives.get(i).getLibelle();
					break;
				}
			}
		}
	}

	// Fonction qui recherche les sessions en fonction d'une ref donnée
	private void _findSession() {
		try {
			if (prospect.getReference() != null) {
				Formation formation = formationDao.findByReference(prospect
						.getReference());
				listeSession = formation.getNextSessionsOrganismeDto();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}


	// Fonction qui permet de gerer la pertinence de l'avancement d'un prospect
	public void gereStatut() {
		log.debug(loggedUser + " gereStatut(), commercial ="
				+ prospect.getProspectDetail().getCommercial());
		ResourceBundle bundle = SeamResourceBundle.getBundle();
		String statut1 = bundle.getString("prospect.nonAffecte");
		if (prospect.getProspectDetail().getCommercial() == null) {
			prospect.setStatut(statut1);
		}
	}

	// Fonction permettant d'indiquer le prix d'une formation donnée
	public void findPrix() {
		log.debug(loggedUser + " findPrix()");
		setPrixFormation(0);
		try {
			if (lastRef != null && !(lastRef).equals(prospect.getReference())) {
				prospect.getProspectDetail().setDate(null);
			}
			if (prospect.getReference() != null) {
				for (FormationDto fDto : adWordsResult) {
					if (prospect.getReference().equals(
							fDto.getFormation().getReference())) {
						setPrixFormation(fDto.getFormation().getPrix());
						break;
					}
				}
			}
		} catch (Exception e) {
			prospect.getProspectDetail().setDate(null);
		}
		_findSession();
	}

	// Fonction de detection de spam
	public void spamDetect(Prospect p) {
		Boolean estSpam = false;
		String champ = "";
		if (p.getProspectDetail() != null) {
			champ = p.getProspectDetail().getHeure();
		}
		if (!"".equals(champ) && champ != null) {
			// Boolean permetant de delibérer si le prospect est un span ou non
			boolean testChiffre = false;
			boolean testSpace = false;
			String temp = "";
			// Test sur la longueur du champ
			int longueurChamp = champ.length();
			// test s'il y a au moin un chiffre
			for (int i = 0; i < 25; i++) {
				temp = i + "";
				if (champ.indexOf(temp) != -1) {
					testChiffre = true;
				}
			}
			// test s'il y a un espace
			testSpace = champ.indexOf(" ") != -1;
			// Test s'il y a pas plus de deux majuscules
			String lettre;
			int accMaj = 0;
			for (int i = 0; i < longueurChamp; i++) {
				lettre = champ.charAt(i) + "";
				if (!lettre.equals(" ") && !lettre.equals("0")
						&& !lettre.equals("1") && !lettre.equals("2")
						&& !lettre.equals("3") && !lettre.equals("4")
						&& !lettre.equals("5") && !lettre.equals("6")
						&& !lettre.equals("7") && !lettre.equals("8")
						&& !lettre.equals("9")
						&& lettre.equals(lettre.toUpperCase())) {
					accMaj = accMaj + 1;
				}
			}
			if (testChiffre == true && longueurChamp < 5) {
				estSpam = false;
			}
			if (testChiffre == false && longueurChamp >= 5) {
				estSpam = true;
			}
			if (longueurChamp >= 5 && accMaj >= 2 && testSpace == false) {
				estSpam = true;
			}
		}
		spam.put(p.getIdProspect(), estSpam);
	}

	/**
	 * Affiche la dialogue de génération de devis.
	 */
	public void prepareDevis() {
		Formation formationDevis = null;
		if (prospect.getReference() != null)
			formationDevis = formationDao.findByReference(prospect
					.getReference());

		devisManager.createDevis(prospect, formationDevis);

	}

	// Affiche ou non les nom des participants(Ajax)
	public void afficheParticipant() {
		if (afficheP == true) {
			afficheP = false;
		} else {
			afficheP = true;
		}
	}

	public void objectif() {
		afficheObjectif = true;
		nbProspectSigne = 0;
		montantTotal = (float) 0;
		ResourceBundle bundle = SeamResourceBundle.getBundle();
		String statut = bundle.getString("prospect.gagne");
		for (ProspectDto pDto : results) {
			if ((statut).equals(pDto.getStatut())) {
				nbProspectSigne = nbProspectSigne + 1;
				if (pDto.getProspectDetail().getMontant() != null) {
					montantTotal = montantTotal
							+ pDto.getProspectDetail().getMontant();
				}
			}

		}
	}

	// rajoute une ligne lors de l'ajout d'une reference
	public void addReference() {
		log.debug(loggedUser + ":addReference()");
		ProspectFormation p = new ProspectFormation();
		p.setProspect(prospect);
		prospect.getFormations().add(p);
	}

	public void addReferenceForModif() {
		log.debug(loggedUser + ":addReferenceForModif()");
		ProspectFormation p = new ProspectFormation();
		p.setProspect(prospect);
		prospect.getFormations().add(p);
	}

	public void addNote() {
		log.debug(loggedUser + ":addNote()");
		Message m = new Message();
		m.setDate(new Date());
		m.setProspect(prospect);
		m.setAccount(loggedUser);
		messages.add(m);
		modif = true;
	}

	public void addSpe() {
		log.debug(loggedUser + ":addSpe()");
		IntraReferenceSpe i = new IntraReferenceSpe();
		i.setInformationIntra(prospect.getInformationIntra());
		ReferenceSpe r = new ReferenceSpe();
		entityManager.persist(r);
		i.setReferenceSpe(r);
		prospect.getInformationIntra().getIntraReferenceSpe().add(i);
	}

	// supprime une ligne dans le tableau de sélection de formation
	public void removeReference(int index) {
		log.debug(loggedUser + ":removeReference()");
		entityManager.remove(prospect.getFormations().get(index));
		prospect.getFormations().remove(index);
	}

	public void removeReferenceForModif(int index) {
		log.debug(loggedUser + ":removeReferenceForModif()");
		entityManager.remove(prospect.getFormations().get(index));
		prospect.getFormations().remove(index);
	}

	public void removeSpe(int index) {
		log.debug(loggedUser + ":removeSpe()");
		entityManager.remove(prospect.getInformationIntra()
				.getIntraReferenceSpe().get(index));
		prospect.getInformationIntra().getIntraReferenceSpe().remove(index);
	}

	// Fonction pour horaire demande intra
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

	public List<Event> getHistorique() {

		if (historique == null && prospect != null) {
			log.debug("getHistorique()");
			historique = eventDao.findAll(prospect,
					prospect.getInformationIntra());
		}
		return historique;
	}

	@Observer("devisGenerated")
	public void _setLastGeneratedDevis() {
		lastGeneratedDevis = devisDao.findLastByProspect(prospect);
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
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

	public void setEditMode() {
		mode = EDIT_MODE;
	}

	public void setVisuMode() {
		mode = VISU_MODE;
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

	public ProspectDetail getProspectDetail() {
		if (prospect != null) {
			return prospect.getProspectDetail();
		} else {
			return null;
		}
	}

	public Account getCommercialeR() {
		return commercialeR;
	}

	public void setCommercialeR(Account commercialeR) {
		this.commercialeR = commercialeR;
	}


	public String getAncienCommercial() {
		return ancienCommercial;
	}

	public void setAncienCommercial(String ancienCommercial) {
		this.ancienCommercial = ancienCommercial;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public boolean isAfficheP() {
		return afficheP;
	}

	public void setAfficheP(boolean afficheP) {
		this.afficheP = afficheP;
	}

	public Map<Integer, Boolean> getSpam() {
		return spam;
	}

	public void setSpam(Map<Integer, Boolean> spam) {
		this.spam = spam;
	}

	public Boolean getMapValue(int key) {
		Boolean result = false;
		try {
			result = spam.get(key);
		} catch (Exception e) {
			log.error("Une erreur est survenue : " + e);
		}
		return result;
	}

	public Float getPrice(String key) {
		Float result = null;
		if (!"".equals(key) && key != null) {
			try {
				result = prix.get(key);
			} catch (Exception e) {
				log.error("Une erreur est survenue : " + e);
			}
		}
		return result;
	}

	public Map<String, Float> getPrix() {
		return prix;
	}

	public void setPrix(Map<String, Float> prix) {
		this.prix = prix;
	}

	public List<FormationDto> getAdWordsResult() {
		return adWordsResult;
	}

	public void setAdWordsResult(List<FormationDto> adWordsResult) {
		this.adWordsResult = adWordsResult;
	}

	public Float getMontantTotal() {
		return montantTotal;
	}

	public void setMontantTotal(Float montantTotal) {
		this.montantTotal = montantTotal;
	}

	public int getNbProspectSigne() {
		return nbProspectSigne;
	}

	public void setNbProspectSigne(int nbProspectSigne) {
		this.nbProspectSigne = nbProspectSigne;
	}

	public boolean isAfficheObjectif() {
		return afficheObjectif;
	}

	public void setAfficheObjectif(boolean afficheObjectif) {
		this.afficheObjectif = afficheObjectif;
	}

	public boolean isEnvoye() {
		return envoye;
	}

	public void setEnvoye(boolean envoye) {
		this.envoye = envoye;
		ProspectDetail prospectDetail = getProspectDetail();
		if (prospectDetail != null) {
			if (envoye) {
				prospectDetail.setDatedevis(new Date());
				setRelance(true);
			} else {
				prospectDetail.setDatedevis(null);
				setRelance(false);
			}
		}

	}
	public boolean isRelance() {
		return relance;
	}

	public void setRelance(boolean relance) {
		this.relance = relance;
		ProspectDetail prospectDetail = getProspectDetail();
		if (prospectDetail != null) {
			if (relance) {
				Calendar cal = Calendar.getInstance(Locale.FRENCH);
				cal.setTime(prospectDetail.getDatedevis());
				cal.add(Calendar.DAY_OF_YEAR,ApplicationManager.DEVIS_RELANCE);
				prospectDetail.setDateRelance(cal.getTime());
			} else {
				prospectDetail.setDateRelance(null);
			}
		}

	}

	public List<SessionOrganismeDto> getListeSession() {
		return listeSession;
	}

	public void setListeSession(List<SessionOrganismeDto> listeSession) {
		this.listeSession = listeSession;
	}

	public float getPrixFormation() {
		return prixFormation;
	}

	public void setPrixFormation(float prixFormation) {
		this.prixFormation = prixFormation;
	}

	public String getLastRef() {
		return lastRef;
	}

	public void setLastRef(String lastRef) {
		this.lastRef = lastRef;
	}

	public String getTypo() {
		return typo;
	}

	public void setTypo(String typo) {
		this.typo = typo;
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

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
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

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public TypeContact getTypeContact() {
		return typeContact;
	}

	public void setTypeContact(TypeContact typeContact) {
		this.typeContact = typeContact;
	}

	public List<Account> getListeCommerciale() {
		return listeCommerciale;
	}

	private Account _getCommercialByName(String fullName) {

		for (Account commercial : listeCommerciale) {
			if (commercial.getNomComplet().equals(fullName)) {
				return commercial;
			}
		}
		return null;
	}

	public Devis getLastGeneratedDevis() {
		return lastGeneratedDevis;
	}

	public void setLastGeneratedDevis(Devis lastGeneratedDevis) {
		this.lastGeneratedDevis = lastGeneratedDevis;
	}

	public String getPotentielAsString() {
		if (prospect != null && prospect.getProspectDetail() != null) {
			return Labels.getString("prospect.potentiel."
					+ prospect.getProspectDetail().getPotentiel());
		} else {
			return "";
		}
	}
	
	public String getButtonMsg() {
		return buttonMsg;
	}

	public void setButtonMsg(String buttonMsg) {
		this.buttonMsg = buttonMsg;
	}

	public boolean isFormValid() {
		buttonMsg = "";
		if ( prospect != null ) {
		if (ST_GAGNE.equals(prospect.getStatut()) && (prospect.getProspectDetail().getMontant() == null || prospect.getProspectDetail().getMontant() == 0)  ) {
			buttonMsg += "Veuillez remplir un montant";
			return false;
		} else if (ST_PERDU.equals(prospect.getStatut()) && (prospect.getProspectDetail().getRaisonPerte() == null || prospect.getProspectDetail().getRaisonPerte().length() ==0) ) {
			buttonMsg += "Veuillez remplir la raison de la perte";
			return false;
		}
		}
		return true;
	}
}
