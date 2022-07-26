package com.plb.si.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.plb.dto.FormationDto;
import com.plb.dto.SessionOrganismeDto;
import com.plb.model.Formation;
import com.plb.model.Prospect;
import com.plb.model.ProspectDetail;
import com.plb.model.directory.Account;
import com.plb.si.manager.prospect.IntraManager;
import com.plb.si.service.AccountDao;
import com.plb.si.service.FormationDao;

@Name("demandeClientManager")
@Scope(ScopeType.CONVERSATION)
public class DemandeClientManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5750305364727844524L;

	@In
	EntityManager entityManager;
	
	@In
	FacesMessages facesMessages;
	
	@In
	Account loggedUser;
	
	@In(create=true)
	IntraManager intraManager;
	
	@In(create=true)
	List<Formation> formationsActives;
	
	@Logger
	Log log;
	
	@Out(required = false)
	ProspectDetail prospectDetail;
	
	@Out(required = false)
	Prospect prospect;
	
	@RequestParameter
	String id_prospectSelected;
	
	@RequestParameter
	String ref;
	
	//Liste pour les menus déroulants de recherche
	private List<Account> listeCommerciale;
	
	
	//Attribut pour formulaire Ajax
	
	private boolean intra;
	
	private List<SessionOrganismeDto> listeSession;
	
	private List<FormationDto> adWordsResult;
	
	private float prix;
	
	//Attributs pour la création d'une demande d'intra
	
	private boolean createIntra;
	
	private boolean afficheFormulaire;
	
	//Renseignement statut
	
	private boolean prospection;
	/* 
	 * Debut implémenttion fonctions
	 */
	
	
	//Fonction init pour une creation de demande client
	@Create
	@Begin(join=true)
	public String initDemande(){
		//liste comportant les resultat de la requete
		listeCommerciale = new ArrayList<Account>();
		listeSession = new ArrayList<SessionOrganismeDto>();
		//Recuperation des données
		AccountDao accD = new AccountDao(entityManager);
		FormationDao forD = new FormationDao(entityManager);
		listeCommerciale = accD.findAllCommercialeActive();
		adWordsResult = FormationDto.buildDtos(formationsActives);
		//Affichage formulaire
		afficheFormulaire = false;
		//Initialisation des valeurs
		prospectDetail = new ProspectDetail();
		prospect = new Prospect();
		prospectDetail.setProspect(prospect);
		prospectDetail.setNb_participants(null);
		//recuperation date et heure courante
		Calendar calendar = Calendar.getInstance();
		prospect.setDateCreation(calendar.getTime());
		//Validation obligatoire des conditions generales de vente
		prospectDetail.setCgv(1);
		//Recuperation de l'utilisateur connecte pour le remplissage du formulaire
		prospectDetail.setRempliPar(loggedUser.getNomComplet());
		prospectDetail.setCommercial(loggedUser.getNomComplet());
		//Initialisation boolean permettant ou non l'affichage du champ lieu
		setIntra(false);
		//Dupplication ou nouvelle demande?
		if(id_prospectSelected != null){
			//On recupère le prospect selectionné
			Prospect prospectTemporaire = entityManager.find(Prospect.class, Integer.parseInt(id_prospectSelected));
			//On recupère les informations nécessaires à la duplication
			prospect.setSociete(prospectTemporaire.getSociete());
			prospectDetail.setNatureClient(prospectTemporaire.getProspectDetail().getNatureClient());
			prospectDetail.setLieuEntreprise(prospectTemporaire.getProspectDetail().getLieuEntreprise());
			prospectDetail.setLieu(prospectTemporaire.getProspectDetail().getLieu());
			prospectDetail.setNb_participants(prospectTemporaire.getProspectDetail().getNb_participants());
			prospectDetail.setTypeFormation(prospectTemporaire.getProspectDetail().getTypeFormation());
			if(("INTRA").equals(prospectDetail.getTypeFormation())){
				intra = true;
				afficheFormulaire = true;
			}
			else{
				intra = false;
				afficheFormulaire = true;
			}
			prospect.setNom(prospectTemporaire.getNom());
			prospect.setReference(prospectTemporaire.getReference());
			prospectDetail.setPrix_jour_animation(prospectTemporaire.getProspectDetail().getPrix_jour_animation());
			prospectDetail.setRemise(prospectTemporaire.getProspectDetail().getRemise());
			prospect.setCommentaire(prospectTemporaire.getCommentaire());
			prospectDetail.setDate(prospectTemporaire.getProspectDetail().getDate());
			log.debug("Duplication de demande client");
		}
		if(ref != null){
			prospect.setReference(ref);
		}
		findSession();
		return "/mz/prospect/createDemandeClient.xhtml";
	}
	
	//On sauvegarde la demande client
	public String saveDemandeClient(){
		try{
			//Voir cette condition pour eviter la levé d'exception
			ResourceBundle bundle = SeamResourceBundle.getBundle();
			String statut = bundle.getString("prospect.nonAffecte");
			if(prospectDetail.getCommercial()==null){
				prospect.setStatut(statut);
			}
			else{
				prospect.setStatut("En cours");
			}
		}
		catch(Exception e){
			log.error("Une erreur est survenue lors de l'enregistrement d'une prospect : "+e);
		}
		//Sauvegarde en bdd
		prospect.setDateCreation(new Date());
		if(prospection == true){
			prospect.setType("PROSPECTION");
		}
		else{
			prospect.setType("CLIENT");
		}
		if(prospect.getConsigne() != null){
			prospect.setConsigne(loggedUser.getNomComplet()+" : "+prospect.getConsigne());
		}
		entityManager.persist(prospect);
		entityManager.persist(prospectDetail);
		facesMessages.addFromResourceBundle(Severity.INFO, "prospect.save", prospect.getSociete());
		log.debug("Prospect saved");
		entityManager.flush();
		entityManager.refresh(prospect);
		//On redirige vers le suivi des offres
		return "/mz/search/searchProspects.xhtml";
	}
	
	public String saveDemandeClientIntra(){
		log.debug(loggedUser + " saveDemandeClientIntra");
		try{
			//Voir cette condition pour eviter la levé d'exception
			ResourceBundle bundle = SeamResourceBundle.getBundle();
			String statut = bundle.getString("prospect.nonAffecte");
			if(prospectDetail.getCommercial()==null){
				prospect.setStatut(statut);
			}
			else{
				prospect.setStatut("En cours");
			}
		}
		catch(Exception e){
			log.error("Une erreur est survenue lors de l'enregistrement d'une prospect : "+e);
		}
		//Sauvegarde en bdd
		prospect.setDateCreation(new Date());
		if(prospection == true){
			prospect.setType("PROSPECTION");
		}
		else{
			prospect.setType("CLIENT");
		}
		if(prospect.getConsigne() != null){
			prospect.setConsigne(loggedUser.getNomComplet()+" : "+prospect.getConsigne());
		}
		entityManager.persist(prospect);
		entityManager.persist(prospectDetail);
		facesMessages.addFromResourceBundle(Severity.INFO, "prospect.save", prospect.getSociete());
		log.debug("Prospect sauvegardé");
		entityManager.flush();
		entityManager.refresh(prospect);
		return intraManager.createIntra();
	}
	
	//Fonction listener Ajax
	public void estIntra(){
		if(prospectDetail.getTypeFormation().equals("INTRA")){
			intra = true;
			afficheFormulaire = true;
		}
		else if(prospectDetail.getTypeFormation() == null){
			afficheFormulaire = false;
		}
		else{
			intra = false;
			afficheFormulaire = true;
		}
	}
	
	public void findSession(){
		if(prospect.getReference() != null){
			FormationDao f = new FormationDao(entityManager);
			Formation formation = f.findByReference(prospect.getReference());
			listeSession = formation.getNextSessionsOrganismeDto();
		}
		//Recherche le prix d'une formation donnée
		setPrix(0);
		if(prospect.getReference() != null){
			for(int i = 0 ; i < adWordsResult.size() ; i++){
				if(prospect.getReference().equals(adWordsResult.get(i).getFormation().getReference())){
					setPrix(adWordsResult.get(i).getFormation().getPrix());
				}
			}
		}
	}
	
	//GETTEURS AND SETTEURScons
	
	public ProspectDetail getProspectDetail() {
		return prospectDetail;
	}

	public void setProspectDetail(ProspectDetail prospectDetail) {
		this.prospectDetail = prospectDetail;
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}
	
	public List<Account> getListeCommerciale() {
		return listeCommerciale;
	}

	public void setListeCommerciale(List<Account> listeCommerciale) {
		this.listeCommerciale = listeCommerciale;
	}


	public boolean isIntra() {
		return intra;
	}

	public void setIntra(boolean intra) {
		this.intra = intra;
	}

	public List<SessionOrganismeDto> getListeSession() {
		return listeSession;
	}

	public void setListeSession(List<SessionOrganismeDto> listeSession) {
		this.listeSession = listeSession;
	}

	public List<FormationDto> getAdWordsResult() {
		return adWordsResult;
	}

	public void setAdWordsResult(List<FormationDto> adWordsResult) {
		this.adWordsResult = adWordsResult;
	}

	public float getPrix() {
		return prix;
	}

	public void setPrix(float prix) {
		this.prix = prix;
	}

	public boolean isCreateIntra() {
		return createIntra;
	}

	public void setCreateIntra(boolean createIntra) {
		this.createIntra = createIntra;
	}

	public boolean isAfficheFormulaire() {
		return afficheFormulaire;
	}

	public void setAfficheFormulaire(boolean afficheFormulaire) {
		this.afficheFormulaire = afficheFormulaire;
	}

	public boolean isProspection() {
		return prospection;
	}

	public void setProspection(boolean prospection) {
		this.prospection = prospection;
	}

}
