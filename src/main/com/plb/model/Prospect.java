package com.plb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

@Entity
@Table(name = "formulaire_contact")
public class Prospect implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6661375793949705632L;

	// Attributs de l'objet FormulaireContact
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int idProspect;

	@Column(name = "for_reference")
	private String reference;

	@Column(name = "nom")
	private String nom;

	@Column(name = "prenom")
	private String prenom;

	@Column(name = "email")
	private String email;

	@Column(name = "tel")
	@Size(max = 20)
	private String tel;

	@Column(name = "fax")
	private String fax;

	@Column(name = "societe")
	private String societe;

	@Column(name = "adresse")
	private String adresse;

	@Column(name = "ville")
	private String ville;

	@Column(name = "cp")
	private String cp;

	@Column(name = "pays")
	private String pays;

	@Column(name = "url_formation")
	private String url_formation;

	@Column(name = "commentaire", columnDefinition="text")
	private String commentaire;

	@Column(name = "type")
	private String type;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datecreation")
	private Date dateCreation;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datemodification")
	private Date dateModification;

	@Column(name = "statut")
	private String statut;

	@Column(name = "consigne", columnDefinition="text")
	private String consigne;

	@Column(name = "note_perso", columnDefinition="text")
	private String notePerso;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "prospect", cascade = CascadeType.ALL)
	private ProspectDetail prospectDetail;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "prospect", cascade = CascadeType.ALL)
	private InformationIntra informationIntra;

	@OneToMany(mappedBy = "prospect", cascade = CascadeType.ALL)
	List<ProspectFormation> formations = new ArrayList<ProspectFormation>();

	// Getteurs and Setteurs

	public ProspectDetail getProspectDetail() {
		return prospectDetail;
	}

	public void setProspectDetail(ProspectDetail prospectDetail) {
		this.prospectDetail = prospectDetail;
		prospectDetail.setProspect(this);
	}

	public String getNotePerso() {
		return notePerso;
	}

	public void setNotePerso(String notePerso) {
		this.notePerso = notePerso;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public int getIdProspect() {
		return idProspect;
	}

	public void setIdProspect(int idFormulaireContact) {
		this.idProspect = idFormulaireContact;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	@Transient
	public String getNomComplet() {
		return getPrenom() + " " + getNom();
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getSociete() {
		return societe;
	}

	public void setSociete(String societe) {
		this.societe = societe;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getPays() {
		return pays;
	}

	public void setPays(String pays) {
		this.pays = pays;
	}

	public String getUrl_formation() {
		return url_formation;
	}

	public void setUrl_formation(String url_formation) {
		this.url_formation = url_formation;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConsigne() {
		return consigne;
	}

	public void setConsigne(String consigne) {
		this.consigne = consigne;
	}

	public InformationIntra getInformationIntra() {
		return informationIntra;
	}

	public void setInformationIntra(InformationIntra informationIntra) {
		this.informationIntra = informationIntra;
	}

	public List<ProspectFormation> getFormations() {
		return formations;
	}

	public void setFormations(List<ProspectFormation> formations) {
		this.formations = formations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idProspect;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prospect other = (Prospect) obj;
		if (idProspect != other.idProspect)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Prospect [idProspect=" + idProspect + ", societe=" + societe
				+ "]";
	}

	public Prospect clone() {
		Prospect p = new Prospect();
		// Champ Prospect
		p.setIdProspect(getIdProspect());
		p.setReference(getReference());
		p.setNom(getNom());
		p.setPrenom(getPrenom());
		p.setEmail(getEmail());
		p.setTel(getTel());
		p.setFax(getFax());
		p.setSociete(getSociete());
		p.setAdresse(getAdresse());
		p.setVille(getVille());
		p.setCp(getCp());
		p.setPays(getPays());
		p.setUrl_formation(getUrl_formation());
		p.setCommentaire(getCommentaire());
		p.setType(getType());
		p.setDateCreation(getDateCreation());
		p.setDateModification(getDateModification());
		p.setStatut(getStatut());
		p.setConsigne(getConsigne());
		p.setFormations(getFormations());
		p.setInformationIntra(getInformationIntra());
		return p;

	}
}
