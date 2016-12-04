package com.plb.si.manager.prospect;

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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.plb.dto.FormationDto;
import com.plb.model.Formation;
import com.plb.model.Prospect;
import com.plb.model.ProspectDetail;
import com.plb.model.TypeContact;
import com.plb.model.directory.Account;
import com.plb.model.event.Event;
import com.plb.model.event.ProspectCreationEvent;
import com.plb.si.service.AccountDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.NotificationService;
import com.plb.si.service.TypeContactDao;

@Name("prospectManager")
@Scope(ScopeType.CONVERSATION)
public class ProspectManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7908298584605188122L;
	
	@In
	EntityManager entityManager;
	
	@In
	FacesMessages facesMessages;
	
	//permet l'envoi de mail/notification
	@In(create=true)
	NotificationService notificationService;
	
	@In
	Account loggedUser;
	
	@In(create=true)
	Map<String, Float> tarifsInter;
	
	@In(create=true)
	List<Formation> formationsActives;
	
	@Logger
	Log log;
	
	@Out(required = false)
	ProspectDetail prospectDetail;
	
	@Out(required = false)
	Prospect prospect;
	
	//Liste pour les menus déroulants de recherche
	private List<Account> listeCommerciale;
	
	
	private List<TypeContact> listeTypeContact;
	
	private List<FormationDto> adWordsResult;
	
	//Attribut pour formulaire Ajax
	
	private boolean intra;
	
	private float prix;
	
	/* 
	 * Debut implémenttion fonctions
	 */
	
	
	//Redirection vers JSF de création de prospect
	@Begin(join=true)
	public String init() {
		//liste comportant les resultat de la requete
		listeCommerciale = new ArrayList<Account>();
		listeTypeContact = new ArrayList<TypeContact>();
		//Recuperation des données
		AccountDao accD = new AccountDao(entityManager);
		FormationDao forD = new FormationDao(entityManager);

		TypeContactDao typD = new TypeContactDao(entityManager);
		listeCommerciale = accD.findAllCommercialeActive();


		listeTypeContact = typD.findAll();
		adWordsResult = FormationDto.buildDtos(formationsActives, tarifsInter);
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
		//Initialisation boolean permettant ou non l'affichage du champ lieu
		setIntra(false);
		//Init du prix
		prix = 0;
		//initialisation du commercial; si c'est un commercial qui est logger alors le commercial associer é
		//redirection
		return "/mz/prospect/prospect.xhtml";
	}


	//On sauvegarde le prospect
	public String saveProspect(){
		//Account pour l'envoie de mail
		Account commercialCible = new Account();
		try{
			//Voir cette condition pour eviter la levé d'exception
			ResourceBundle bundle = SeamResourceBundle.getBundle();
			String statut = bundle.getString("prospect.nonAffecte");
			if(prospectDetail.getCommercial()==null){
				prospect.setStatut(statut);
			}
			else{
				prospect.setStatut("En attente");
			}
		}
		catch(Exception e){
			log.error("Une erreur est survenue lors de l'enregistrement d'une prospect : "+e);
		}
		//Sauvegarde en bdd
		prospect.setDateCreation(new Date());
		if(!"".equals(prospect.getConsigne())){
			prospect.setConsigne(loggedUser.getNomComplet()+" : "+prospect.getConsigne());
		}
		try{
			entityManager.persist(prospect);
			entityManager.persist(prospectDetail);
			facesMessages.addFromResourceBundle(Severity.INFO, "prospect.save", prospect.getSociete());
			log.debug("Prospect sauvegardé");
			//Enregistrement de l'Event de creation
			Event prospectCreationEvent = new ProspectCreationEvent(loggedUser, prospect);
			entityManager.persist(prospectCreationEvent);
			//Envoie d'un mail pour l'éventuel commercial associé
			if(prospectDetail.getCommercial() != null && entityManager.contains(prospect)){
				for(int i = 0 ; i < listeCommerciale.size() ; i++){
					if(listeCommerciale.get(i).getNomComplet().equals(prospectDetail.getCommercial())){
						commercialCible = listeCommerciale.get(i);
					}
				}
				notificationService.resolveDestinatairesProspect(commercialCible, null, prospectCreationEvent);
				notificationService.sendMailProspect(1000, prospect, prospectCreationEvent);
			}
		}
		catch(Exception e){
			log.debug("Une erreur est survenue lors de l'enregistrement du prospect : "+e);
		}
		//On réinitialise la prospect pour pouvoir enregitrer un nouveau prospect
		return init();
	}
	
	//Fonction listener Ajax
	public void estIntra(){
		if(prospectDetail.getTypeFormation().equals("INTRA")){
			intra = true;
		}
		else{
			intra = false;
		}
	}
	
	public void findPrix(){
		prix = 0;
		if(prospect.getReference() != null){
			for(int i = 0 ; i < adWordsResult.size() ; i++){
				if(prospect.getReference().equals(adWordsResult.get(i).getFormation().getReference())){
					prix = adWordsResult.get(i).getTarifInter();
				}
			}
		}
	}
	
	//Getteurs and Setteurs
	
	public List<TypeContact> getListeTypeContact() {
		return listeTypeContact;
	}

	public void setListeTypeContact(List<TypeContact> listeTypeContact) {
		this.listeTypeContact = listeTypeContact;
	}
	
	public ProspectDetail getProspectDetail() {
		return prospectDetail;
	}


	public void setProspectDetail(ProspectDetail prospectDetail) {
		this.prospectDetail = prospectDetail;
	}

	
	public FacesMessages getFacesMessages() {
		return facesMessages;
	}


	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}


	public Account getLoggedUser() {
		return loggedUser;
	}


	public void setLoggedUser(Account loggedUser) {
		this.loggedUser = loggedUser;
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


	public float getPrix() {
		return prix;
	}


	public void setPrix(float prix) {
		this.prix = prix;
	}


	public List<FormationDto> getAdWordsResult() {
		return adWordsResult;
	}


	public void setAdWordsResult(List<FormationDto> adWordsResult) {
		this.adWordsResult = adWordsResult;
	}	
	
}