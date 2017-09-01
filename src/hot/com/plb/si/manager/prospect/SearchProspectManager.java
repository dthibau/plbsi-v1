package com.plb.si.manager.prospect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.persistence.EntityManager;

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
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.component.SortOrder;

import com.plb.dto.FormationDto;
import com.plb.dto.SessionOrganismeDto;
import com.plb.model.Formation;
import com.plb.model.Prospect;
import com.plb.model.ProspectCritere;
import com.plb.model.ProspectDetail;
import com.plb.model.TypeContact;
import com.plb.model.directory.Account;
import com.plb.model.directory.Role;
import com.plb.model.event.Event;
import com.plb.model.message.Message;
import com.plb.si.dto.ProspectDto;
import com.plb.si.dto.ProspectUpdate;
import com.plb.si.dto.TableauRowDto;
import com.plb.si.manager.ApplicationManager;
import com.plb.si.manager.DevisManager;
import com.plb.si.service.AccountDao;
import com.plb.si.service.EventDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.MessageDao;
import com.plb.si.service.NotificationService;
import com.plb.si.service.ProspectDao;
import com.plb.si.service.ProspectDetailDao;

@Name("searchProspect")
@Scope(ScopeType.CONVERSATION)
public class SearchProspectManager implements Serializable {

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
	Map<String, Float> tarifsInter;

	@In(create = true)
	DevisManager devisManager;

	@In(create = true)
	ApplicationManager applicationManager;

	@In(create = true)
	List<TypeContact> typesContact;

	@In(create = true)
	private List<Formation> formationsActives;

	@Out(required = false)
	private Prospect prospectDelete;

	@Out(required = false)
	public Formation formationDevis;

	// Attribut renseignant sur le prospect selectionnés
	@Out(required = false)
	private String prospectSelected;

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
	private List<Prospect> delete;

	// Liste de prospects � supprimer
	private List<Message> messages;

	private int tailleMessagerie;

	private boolean modif;

	// Attributs pour la recherche
	private String statut;

	private Account commercialeR;

	@Out
	public static int ALL_INT = 0;
	@Out
	public static int INTER = 1;
	@Out
	public static int INTRA = 2;

	private int interIntra = ALL_INT;
	private String reference;

	private Float montantTotal;

	private int nbProspectSigne;

	private boolean afficheObjectif;

	private String typo;

	private TypeContact typeContact;

	private Date dateDebut;
	private Date dateFin;

	private boolean asuivre;

	// Attribut pour remplissage heure formation

	private String heureDeb;

	private String minDeb;

	private String heureFin;

	private String minFin;

	// Attribut pour l'envoie de mail de confirmation

	private boolean confirm;

	// SortOrder
	private Map<String, SortOrder> orders = new HashMap<String, SortOrder>();


	// Attribu pour les mode visu; edit
	private static int VISU_MODE = 0;
	private static int EDIT_MODE = 1;
	private static int NEW_MODE = 2;
	int mode = EDIT_MODE;

	// Attribu pour remplissage obligatoire ou non du montant et raison de la
	// perte
	private boolean remplir;

	private boolean remplirPerte;

	// Attribut pour savoir si on affiche les prospect gagne ou perdu

	private int state = ProspectCritere.TODO;

	// Attribut pour savoir si l'on affiche le calendrier pour la date d'envoie
	// du devis
	private boolean envoye;

	// Attribut pour gérer l'envoie de mail
	private String ancienCommercial;

	// Analyse de Spam
	private Map<Integer, Boolean> spam = new HashMap<Integer, Boolean>();

	// Prix formation
	private Map<String, Float> prix = new HashMap<String, Float>();

	private float prixFormation;

	// Attribut pour la suppression multiple
	private boolean deleteUnique;

	private Map<Long, Boolean> selectedIds = new HashMap<Long, Boolean>();

	private String lastRef;

	// Annulation intra

	private String statutIntraTemp;

	private Date lastSearchDate;

	AccountDao accountDao;
	FormationDao formationDao;
	ProspectDao prospectDao;
	EventDao eventDao;

	List<Event> historique;

	ProspectCritere critere = new ProspectCritere();

	// Fonction d'initialisation(Quand on clique sur l'onglet Prospects via le
	// menu)
	@Create
	@Begin(join = true)
	public String init() {
		log.debug(loggedUser + " init SearchProspectManager");

		long start = System.currentTimeMillis();
		// liste comportant les resultat de la requete
		listeSession = new ArrayList<SessionOrganismeDto>();
		// initialisation des champs de recherche
		interIntra = ALL_INT;
		commercialeR = null;
		afficheObjectif = false;
		typo = null;
		typeContact = null;
		Calendar cal = Calendar.getInstance();
		dateFin = cal.getTime();
		cal.add(Calendar.MONTH, -3);
		dateDebut = cal.getTime();

		// Initialisation du remplissage obligatoire de montant
		remplir = false;
		remplirPerte = false;
		state = ProspectCritere.TODO;

		accountDao = new AccountDao(entityManager);
		prospectDao = new ProspectDao(entityManager);
		formationDao = new FormationDao(entityManager);
		eventDao = new EventDao(entityManager);

		listeCommerciale = accountDao.findAllCommercialeActive();
		_buildCritere();
		_findAFaire();

		mode = VISU_MODE;

		adWordsResult = FormationDto.buildDtos(formationsActives, tarifsInter);
		// Recuperation des prix des formations
		for (int i = 0; i < adWordsResult.size(); i++) {
			prix.put(adWordsResult.get(i).getFormation().getReference(),
					adWordsResult.get(i).getTarifInter());
		}
		unsetOrder();
		log.debug(loggedUser + " init end in "
				+ (System.currentTimeMillis() - start));
		return "/mz/search/searchProspects.xhtml";
	}

	private List<ProspectDto> _filterAsuivre() {
		List<ProspectDto> ret = new ArrayList<ProspectDto>();
		for (ProspectDto prospectDto : results) {
			if (prospectDto.getAsuivre()) {
				ret.add(prospectDto);
			}
		}
		return ret;
	}

	private void _findAFaire() {
		// Fetch results
		Set<ProspectDto> resultSet = new HashSet<ProspectDto>();
		try {
			if (loggedUser.isExtendedManager() || loggedUser.isDispatcher()) {
				resultSet
						.addAll(ProspectDto.buildDTO(
								prospectDao.findProspectInit(critere),
								Role.DISPATCHER));
			}
			if (loggedUser.isCommercial()) {
				resultSet.addAll(ProspectDto.buildDTO(prospectDao
						.findActiveAssociateProspect(loggedUser, critere),
						Role.COMMERCIAL));
			}
		} catch (Exception e) {
			log.error("Une erreur est survenue lors de l'initialisation de liste de résultat : "
					+ e);
		}
		results = _filterInterIntra(resultSet);

	}

	// Fonction de recherche en fonction du statut; commerciale; type contact
	public void recherche() {
		afficheObjectif = false;
		_buildCritere();

		lastSearchDate = new Date();

		if (state == ProspectCritere.ALL || state == ProspectCritere.ENCOURS) {

			Set<ProspectDto> resultSet = new HashSet<ProspectDto>();
			if (loggedUser.isExtendedManager() || loggedUser.isDispatcher()) {
				critere.setCommercial(commercialeR);
				resultSet.addAll(ProspectDto.buildDTO(
						prospectDao.search(critere), Role.DISPATCHER));
			}
			if (loggedUser.isCommercial()) {
				critere.setCommercial(loggedUser);
				resultSet.addAll(ProspectDto.buildDTO(
						prospectDao.search(critere), Role.COMMERCIAL));
			}
			results = _filterInterIntra(resultSet);

		} else {
			_findAFaire();
		}
		if (asuivre) {
			results = _filterAsuivre();
		}
	}

	// Fonction qui réinitialise la page
	public void encours() {
		// remise des tri a unsorted
		unsetOrder();
		// remise à zéro des champs de recherche
		commercialeR = null;
		interIntra = ALL_INT;
		statut = null;
		reference = null;
		state = ProspectCritere.ENCOURS;
		afficheObjectif = false;
		typo = null;

		recherche();
		mode = VISU_MODE;
	}

	private void _buildCritere() {
		critere.setState(state);
		critere.setStatut(statut);
		critere.setCommercial(commercialeR);
		if (state == ProspectCritere.ALL) {
			critere.setReference(reference);
			critere.setTypeContact(typeContact);
			critere.setDateDebut(dateDebut);
			critere.setDateFin(dateFin);
		} else {
			critere.setReference(null);
			critere.setDateDebut(null);
			critere.setDateFin(null);
		}
	}

	private List<ProspectDto> _filterInterIntra(Set<ProspectDto> resultSet) {
		List<ProspectDto> ret = new ArrayList<ProspectDto>();
		if (interIntra == ALL_INT) {
			for (ProspectDto pDto : resultSet) {
				if (pDto.getProspect().getInformationIntra() == null
						|| !"En cours".equals(statut)
						|| !ApplicationManager.ST_INTRA_ANNULE.equals(pDto
								.getProspect().getInformationIntra()
								.getStatutIntra())) {
					ret.add(pDto);
				}
			}
		} else if (interIntra == INTER) {
			for (ProspectDto pDto : resultSet) {
				if (pDto.getProspect().getInformationIntra() == null) {
					ret.add(pDto);
				}
			}
		} else if (interIntra == INTRA) {
			for (ProspectDto pDto : resultSet) {
				if (pDto.getProspect().getInformationIntra() != null) {
					if (!"En cours".equals(statut)
							|| !ApplicationManager.ST_INTRA_ANNULE.equals(pDto
									.getProspect().getInformationIntra()
									.getStatutIntra())) {
						ret.add(pDto);
					}
				}
			}
		}
		Collections.sort(ret);
		return ret;
	}

	public List<ProspectUpdate> getUpdatedProspects() {
		return applicationManager.getUpdatedProspects(lastSearchDate);
	}

	/**
	 * Fonction de rafraichissement prend tous les objets li�s et les
	 * rafraichient
	 */
	@Observer("refreshNeeded")
	public void refresh() {
		try {
			entityManager.clear();
			recherche();
		} catch (Exception e) {
			log.debug(loggedUser + " STACKTRACE");
			e.printStackTrace();
		}
	}

	// Selectionne la prospect à supprimer et repasse en mode AUTO
	@Begin(join = true, flushMode = FlushModeType.AUTO)
	public void selectProspectDelete() {
		log.debug(loggedUser + " selectProspectDelete");
		try {
			deleteUnique = true;
			prospectDelete = entityManager.find(Prospect.class,
					Integer.parseInt(prospectSelected));
		} catch (Exception e) {
			log.debug(loggedUser + " STACKTRACE");
			e.printStackTrace();
		}
	}

	// Selectionne les prospects à supprimer via les checkbox
	public void getSelectedItems() {
		// Get selected items.
		deleteUnique = false;
		delete = new ArrayList<Prospect>();
		for (ProspectDto prospectDto : results) {
			Prospect prospect = prospectDto.getProspect();
			if (selectedIds.get(prospect.getIdProspect()).booleanValue()) {
				delete.add(prospect);
				selectedIds.remove(prospect.getIdProspect()); // Reset.
			}
		}
		// Do your thing with the MyData items in List selectedDataList.
	}

	// Suppresion du prospect via la pop up
	public void deleteProspect() {
		log.debug(loggedUser + " deleteProspect prospectDelete="
				+ prospectDelete);
		if (prospectDelete != null) {
			EventDao ev = new EventDao(entityManager);
			ev.deletEventProspect(prospectDelete);
			MessageDao msgDao = new MessageDao(entityManager);
			msgDao.deleteMessages(prospectDelete);
			entityManager.remove(prospectDelete);
			log.info("Prospect supprimé : " + prospectDelete);
			results = null;
		}
	}

	@Begin(join = true, flushMode = FlushModeType.AUTO)
	public void deleteListProspect() {
		if (delete.size() > 0) {
			EventDao ev = new EventDao(entityManager);
			MessageDao msgDao = new MessageDao(entityManager);
			for (Prospect prospect : delete) {
				ev.deletEventProspect(prospect);
				entityManager.remove(prospect);
				msgDao.deleteMessages(prospect);
				log.info("Prospect(s) supprimé(s) : " + prospect);
			}
			results = null;
		}
	}

	// Fonction pour le code couleur dans l'interface commerciale
	public boolean isEnAttente(String _statut) {
		boolean response = false;
		if ("En attente".equals(_statut) && loggedUser.isCommercial()) {
			response = true;
		}
		return response;
	}

	public Prospect copyProspect(Prospect pro) {
		Prospect p = new Prospect();
		// Champ Prospect
		p.setIdProspect(pro.getIdProspect());
		p.setReference(pro.getReference());
		p.setNom(pro.getNom());
		p.setPrenom(pro.getPrenom());
		p.setEmail(pro.getEmail());
		p.setTel(pro.getTel());
		p.setFax(pro.getFax());
		p.setSociete(pro.getSociete());
		p.setAdresse(pro.getAdresse());
		p.setVille(pro.getVille());
		p.setCp(pro.getCp());
		p.setPays(pro.getPays());
		p.setUrl_formation(pro.getUrl_formation());
		p.setCommentaire(pro.getCommentaire());
		p.setType(pro.getType());
		p.setDateCreation(pro.getDateCreation());
		p.setDateModification(pro.getDateModification());
		p.setStatut(pro.getStatut());
		p.setConsigne(pro.getConsigne());
		p.setFormations(pro.getFormations());
		p.setInformationIntra(pro.getInformationIntra());
		return p;
	}

	public ProspectDetail copyProspectDetail(ProspectDetail det) {
		ProspectDetail d = new ProspectDetail();
		// Champ ProspectDetail
		d.setIdProspectDetail(det.getIdProspectDetail());
		d.setDate(det.getDate());
		d.setLieu(det.getLieu());
		d.setFonction(det.getFonction());
		d.setCommercial(det.getCommercial());
		d.setResp_departement(det.getResp_departement());
		d.setNb_participants(det.getNb_participants());
		d.setParticipants(det.getParticipants());
		d.setAdresse_factu(det.getAdresse_factu());
		d.setCp_factu(det.getCp_factu());
		d.setVille_factu(det.getVille_factu());
		d.setPays_factu(det.getPays_factu());
		d.setAdresse_convoc(det.getAdresse_convoc());
		d.setCp_convoc(det.getCp_convoc());
		d.setVille_convoc(det.getVille_convoc());
		d.setPays_convoc(det.getPays_convoc());
		d.setOpca(det.getOpca());
		d.setNom_opca(det.getNom_opca());
		d.setConnaissance_site(det.getConnaissance_site());
		d.setCgv(det.getCgv());
		d.setTypeFormation(det.getTypeFormation());
		d.setSexe(det.getSexe());
		d.setRempliPar(det.getRempliPar());
		d.setDate_souhaite(det.getDate_souhaite());
		d.setConcurence(det.getConcurence());
		d.setEffectifEntreprise(det.getEffectifEntreprise());
		d.setEffectifInfo(det.getEffectifInfo());
		d.setMontant(det.getMontant());
		d.setReferenceBis(det.getReferenceBis());
		d.setCatalogue(det.getCatalogue());
		d.setNews(det.getNews());
		d.setCial(det.getCial());
		d.setHeure(det.getHeure());
		d.setLieuEntreprise(det.getLieuEntreprise());
		d.setNatureClient(det.getNatureClient());
		d.setRaisonPerte(det.getRaisonPerte());
		d.setDatedevis(det.getDatedevis());
		d.setRemise(det.getRemise());
		d.setPrix_jour_animation(det.getPrix_jour_animation());
		return d;
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

	// Affiche ou non les nom des participants(Ajax)
	public void afficheParticipant() {
		if (afficheP == true) {
			afficheP = false;
		} else {
			afficheP = true;
		}
	}

	public boolean estClient(String statut) {
		boolean est = false;
		if (("CLIENT").equals(statut.toUpperCase())) {
			est = true;
		}
		return est;
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

	// Gestion de l'ordre
	public void unsetOrder() {
		orders.put("date", SortOrder.unsorted);
		orders.put("dateDevis", SortOrder.unsorted);
		orders.put("entreprise", SortOrder.unsorted);
		orders.put("typo", SortOrder.unsorted);
		orders.put("codeCours", SortOrder.unsorted);
		orders.put("statut", SortOrder.unsorted);
		orders.put("commercial", SortOrder.unsorted);
		orders.put("informationIntra", SortOrder.unsorted);

	}

	public SortOrder getOrder(String key) {
		return orders.get(key);
	}

	public SortOrder setOrder(String key, SortOrder order) {
		return orders.put(key, order);
	}

	public boolean isAscending(String key) {
		return orders.get(key) != null
				&& orders.get(key).equals(SortOrder.ascending);
	}

	public boolean isDescending(String key) {
		return orders.get(key) != null
				&& orders.get(key).equals(SortOrder.descending);
	}

	public void sortBy(String key) {
		for (String k : orders.keySet()) {
			if (!k.equals(key)) {
				orders.put(k, SortOrder.unsorted);
			} else {
				if (orders.get(key).equals(SortOrder.ascending)) {
					orders.put(key, SortOrder.descending);
				} else {
					orders.put(key, SortOrder.ascending);
				}
			}
		}
	}

	public Prospect getProspectDelete() {
		return prospectDelete;
	}

	public void setProspectDelete(Prospect prospectDelete) {
		this.prospectDelete = prospectDelete;
	}

	public List<Account> getListeCommerciale() {
		return listeCommerciale;
	}

	public void setListeCommerciale(List<Account> listeCommerciale) {
		this.listeCommerciale = listeCommerciale;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public int getInterIntra() {
		return interIntra;
	}

	public void setInterIntra(int interIntra) {
		this.interIntra = interIntra;
	}

	@Observer(value = "prospectUpdated")
	public void prospectUpdated() {
		results = null;
	}

	public List<ProspectDto> getResults() {
		if (results == null) {
			recherche();
		}
		return results;
	}

	public String getProspectSelected() {
		return prospectSelected;
	}

	public void setProspectSelected(String prospectSelected) {
		this.prospectSelected = prospectSelected;
	}

	public boolean isAsuivre() {
		return asuivre;
	}

	public void setAsuivre(boolean asuivre) {
		this.asuivre = asuivre;
	}

	public Account getCommercialeR() {
		return commercialeR;
	}

	public void setCommercialeR(Account commercialeR) {
		this.commercialeR = commercialeR;
	}

	public boolean isRemplir() {
		return remplir;
	}

	public void setRemplir(boolean remplir) {
		this.remplir = remplir;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isAll() {
		return state == ProspectCritere.ALL;
	}

	public void setAll(boolean all) {
		if (all) {
			state = ProspectCritere.ALL;
		} else {
			state = ProspectCritere.TODO;
		}
	}

	public void setEnCours(boolean encours) {
		if (encours) {
			state = ProspectCritere.ENCOURS;
		} else {
			state = ProspectCritere.TODO;
		}
	}

	public boolean isEnCours() {
		return state == ProspectCritere.ENCOURS || state == ProspectCritere.ALL;
	}

	public boolean isToDo() {
		return state == ProspectCritere.TODO;
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

	public Map<Long, Boolean> getSelectedIds() {
		return selectedIds;
	}

	public void setSelectedIds(Map<Long, Boolean> selectedIds) {
		this.selectedIds = selectedIds;
	}

	public List<Prospect> getDelete() {
		return delete;
	}

	public void setDelete(List<Prospect> delete) {
		this.delete = delete;
	}

	public boolean isDeleteUnique() {
		return deleteUnique;
	}

	public void setDeleteUnique(boolean deleteUnique) {
		this.deleteUnique = deleteUnique;
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

	public boolean isRemplirPerte() {
		return remplirPerte;
	}

	public void setRemplirPerte(boolean remplirPerte) {
		this.remplirPerte = remplirPerte;
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



	public List<TableauRowDto> getTableau() {
		List<TableauRowDto> tableau = new ArrayList<TableauRowDto>();
		for ( int i=0; i<=3; i++ ) {
			List<Object[]> counts = prospectDao.countPotentiel(i,dateDebut,dateFin);
			_addCols(tableau, i, counts);
		}
		return tableau;
	}

	private void _addCols(List<TableauRowDto> tableau, int key,
			List<Object[]> counts) {

		for (Object[] count : counts) {
			if ( count[1] != null && count[1] instanceof String && ((String)count[1]).length() > 0 ) {
			TableauRowDto row = _getCommercialRow(tableau, (String) count[1]);
			row.getCounts().put(key, (Long) count[0]);
			}
		}
	}

	private TableauRowDto _getCommercialRow(List<TableauRowDto> tableau,
			String commercial) {

		for (TableauRowDto row : tableau) {
			if (row.getCommercial().equals(commercial)) {
				return row;
			}
		}
		// Création d'une nouvelle ligne
		TableauRowDto row = new TableauRowDto();
		row.setCommercial(commercial);
		tableau.add(row);
		return row;
	}

}
