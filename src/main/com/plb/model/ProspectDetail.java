package com.plb.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;



@Entity
@Table(name = "formulaire_contact_detail")

public class ProspectDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1319650692014999015L;

	//Attributs de l'objet Prospect
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int idProspectDetail;

	@Column(name = "date_session")
	private String date;

	@Column(name = "lieu")
	private String lieu;
	
	@Column(name = "fonction")
	private String fonction;
	
	@Column(name = "commercial")
	private String commercial;
	
	@Column(name = "resp_departement")
	private String resp_departement;
	
	@Column(name = "nb_participants")
	private Integer nb_participants;
	
	@Column(name = "participants", columnDefinition="text")
	private String participants;
	
	@Column(name = "adresse_factu")
	private String adresse_factu;
	
	@Column(name = "cp_factu")
	private String cp_factu;
	
	@Column(name = "ville_factu")
	private String ville_factu;
	
	@Column(name = "pays_factu")
	private String pays_factu;
	
	@Column(name = "adresse_convoc")
	private String adresse_convoc;
	
	@Column(name = "cp_convoc")
	private String cp_convoc;
	
	@Column(name = "ville_convoc")
	private String ville_convoc;
	
	@Column(name = "pays_convoc")
	private String pays_convoc;
	
	@Column(name = "opca")
	private String opca;
	
	@Column(name = "nom_opca")
	private String nom_opca;
	
	@Column(name = "connaissance_site")
	private String connaissance_site;
	
	@Column(name = "cgv", columnDefinition="tinyint")
	private int cgv;
	
	//INTER ou INTRA
	@Column(name = "typeformation")
	private String typeFormation;

	//1 = homme; 0 = femme
	@Column(name = "sexe")
	private Integer sexe;
	
	@Column(name = "rempliepar")
	private String rempliPar;
	
	@Column(name = "date_souhaite")
	private String date_souhaite;

	@Column(name = "concurence")
	private String concurence;
	
	@Column(name = "effectifentreprise")
	private Integer effectifEntreprise;

	@Column(name = "effectifinfo")
	private Integer effectifInfo;
	
	@Column(name = "montant")
	private Float montant;
	
	@Column(name = "referencebis")
	private String referenceBis;
	
	@Column(name = "catalogue", columnDefinition="tinyint")
	private Integer catalogue;
	
	@Column(name = "news")
	private Integer news;
	
	@Column(name = "cial", columnDefinition="tinyint")
	private Integer cial;
	
	@Column(name = "heure")
	private String heure;
	
	@Column(name = "natureclient")
	private String natureClient;
	
	@Column(name = "lieuentreprise")
	private String lieuEntreprise;
	
	@Column(name = "raisonperte")
	private String raisonPerte;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datedevis")
	private Date datedevis;
	
	@Column(name = "remise")
	private Float remise = 0f;
	
	@Column(name = "prix_jour_animation")
	private Float prix_jour_animation;

	//Relation avec le formulaire contact

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_formulaire_contact")
	private Prospect prospect;
	
	private int potentiel;
	
	public String getTypeFormation() {
		return typeFormation;
	}


	public void setTypeFormation(String typeFormation) {
		this.typeFormation = typeFormation;
	}
	
	public int getIdProspectDetail() {
		return idProspectDetail;
	}


	public Integer getSexe() {
		return sexe;
	}


	public void setSexe(Integer sexe) {
		this.sexe = sexe;
	}

	
	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}


	public String getReferenceBis() {
		return referenceBis;
	}


	public void setReferenceBis(String referenceBis) {
		this.referenceBis = referenceBis;
	}


	public Integer getCatalogue() {
		return catalogue;
	}


	public void setCatalogue(Integer catalogue) {
		this.catalogue = catalogue;
	}


	public Integer getNews() {
		return news;
	}


	public void setNews(Integer news) {
		this.news = news;
	}


	public Integer getCial() {
		return cial;
	}


	public void setCial(Integer cial) {
		this.cial = cial;
	}


	public String getHeure() {
		return heure;
	}


	public void setHeure(String heure) {
		this.heure = heure;
	}


	public void setIdProspectDetail(int idProspect) {
		this.idProspectDetail = idProspect;
	}

	public Float getMontant() {
		return montant;
	}


	public void setMontant(Float montant) {
		this.montant = montant;
	}

	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getLieu() {
		return lieu;
	}


	public void setLieu(String lieu) {
		this.lieu = lieu;
	}


	public String getFonction() {
		return fonction;
	}


	public void setFonction(String fonction) {
		this.fonction = fonction;
	}


	public String getCommercial() {
		return commercial;
	}


	public void setCommercial(String commercial) {
		this.commercial = commercial;
	}


	public String getResp_departement() {
		return resp_departement;
	}


	public void setResp_departement(String resp_departement) {
		this.resp_departement = resp_departement;
	}


	public Integer getNb_participants() {
		return nb_participants;
	}


	public void setNb_participants(Integer nb_participants) {
		this.nb_participants = nb_participants;
	}


	public String getParticipants() {
		return participants;
	}


	public void setParticipants(String participants) {
		this.participants = participants;
	}


	public String getAdresse_factu() {
		return adresse_factu;
	}


	public void setAdresse_factu(String adresse_factu) {
		this.adresse_factu = adresse_factu;
	}


	public String getCp_factu() {
		return cp_factu;
	}


	public void setCp_factu(String cp_factu) {
		this.cp_factu = cp_factu;
	}


	public String getVille_factu() {
		return ville_factu;
	}


	public void setVille_factu(String ville_factu) {
		this.ville_factu = ville_factu;
	}


	public String getPays_factu() {
		return pays_factu;
	}


	public void setPays_factu(String pays_factu) {
		this.pays_factu = pays_factu;
	}


	public String getAdresse_convoc() {
		return adresse_convoc;
	}


	public void setAdresse_convoc(String adresse_convoc) {
		this.adresse_convoc = adresse_convoc;
	}


	public String getCp_convoc() {
		return cp_convoc;
	}


	public void setCp_convoc(String cp_convoc) {
		this.cp_convoc = cp_convoc;
	}


	public String getVille_convoc() {
		return ville_convoc;
	}


	public void setVille_convoc(String ville_convoc) {
		this.ville_convoc = ville_convoc;
	}


	public String getPays_convoc() {
		return pays_convoc;
	}

	public void setPays_convoc(String pays_convoc) {
		this.pays_convoc = pays_convoc;
	}


	public String getOpca() {
		return opca;
	}


	public void setOpca(String opca) {
		this.opca = opca;
	}


	public String getNom_opca() {
		return nom_opca;
	}


	public void setNom_opca(String nom_opca) {
		this.nom_opca = nom_opca;
	}


	public String getConnaissance_site() {
		return connaissance_site;
	}


	public void setConnaissance_site(String connaissance_site) {
		this.connaissance_site = connaissance_site;
	}


	public int getCgv() {
		return cgv;
	}


	public void setCgv(int cgv) {
		this.cgv = cgv;
	}


	public Prospect getProspect() {
		return prospect;
	}


	public String getRempliPar() {
		return rempliPar;
	}


	public void setRempliPar(String rempliPar) {
		this.rempliPar = rempliPar;
	}


	public String getDate_souhaite() {
		return date_souhaite;
	}


	public void setDate_souhaite(String date_souhaite) {
		this.date_souhaite = date_souhaite;
	}

	@Transient
	public String getDate_souhaiteForDevis() {
		return date_souhaite != null ? date_souhaite : "A définir conjointement";
	}

	public String getConcurence() {
		return concurence;
	}


	public void setConcurence(String concurence) {
		this.concurence = concurence;
	}


	public Integer getEffectifEntreprise() {
		return effectifEntreprise;
	}


	public void setEffectifEntreprise(Integer effectifEntreprise) {
		this.effectifEntreprise = effectifEntreprise;
	}


	public Integer getEffectifInfo() {
		return effectifInfo;
	}


	public void setEffectifInfo(Integer effectifInfo) {
		this.effectifInfo = effectifInfo;
	}

	public String getNatureClient() {
		return natureClient;
	}

	public void setNatureClient(String natureClient) {
		this.natureClient = natureClient;
	}

	public String getLieuEntreprise() {
		return lieuEntreprise;
	}


	public void setLieuEntreprise(String lieuEntreprise) {
		this.lieuEntreprise = lieuEntreprise;
	}


	public String getRaisonPerte() {
		return raisonPerte;
	}


	public void setRaisonPerte(String raisonPerte) {
		this.raisonPerte = raisonPerte;
	}


	public Date getDatedevis() {
		return datedevis;
	}


	public void setDatedevis(Date datedevis) {
		this.datedevis = datedevis;
	}
	
	
	public Float getRemise() {
		return remise;
	}


	public void setRemise(Float remise) {
		this.remise = remise;
	}


	public Float getPrix_jour_animation() {
		return prix_jour_animation;
	}


	public void setPrix_jour_animation(Float prix_jour_animation) {
		this.prix_jour_animation = prix_jour_animation;
	}

	public int getPotentiel() {
		return potentiel;
	}


	public void setPotentiel(int potentiel) {
		this.potentiel = potentiel;
	}


	public ProspectDetail clone() {
		ProspectDetail d = new ProspectDetail();
		// Champ ProspectDetail
		d.setIdProspectDetail(getIdProspectDetail());
		d.setDate(getDate());
		d.setLieu(getLieu());
		d.setFonction(getFonction());
		d.setCommercial(getCommercial());
		d.setResp_departement(getResp_departement());
		d.setNb_participants(getNb_participants());
		d.setParticipants(getParticipants());
		d.setAdresse_factu(getAdresse_factu());
		d.setCp_factu(getCp_factu());
		d.setVille_factu(getVille_factu());
		d.setPays_factu(getPays_factu());
		d.setAdresse_convoc(getAdresse_convoc());
		d.setCp_convoc(getCp_convoc());
		d.setVille_convoc(getVille_convoc());
		d.setPays_convoc(getPays_convoc());
		d.setOpca(getOpca());
		d.setNom_opca(getNom_opca());
		d.setConnaissance_site(getConnaissance_site());
		d.setCgv(getCgv());
		d.setTypeFormation(getTypeFormation());
		d.setSexe(getSexe());
		d.setRempliPar(getRempliPar());
		d.setDate_souhaite(getDate_souhaite());
		d.setConcurence(getConcurence());
		d.setEffectifEntreprise(getEffectifEntreprise());
		d.setEffectifInfo(getEffectifInfo());
		d.setMontant(getMontant());
		d.setReferenceBis(getReferenceBis());
		d.setCatalogue(getCatalogue());
		d.setNews(getNews());
		d.setCial(getCial());
		d.setHeure(getHeure());
		d.setLieuEntreprise(getLieuEntreprise());
		d.setNatureClient(getNatureClient());
		d.setRaisonPerte(getRaisonPerte());
		d.setDatedevis(getDatedevis());
		d.setRemise(getRemise());
		d.setPrix_jour_animation(getPrix_jour_animation());
		d.setPotentiel(getPotentiel());
		return d;
	}

}
