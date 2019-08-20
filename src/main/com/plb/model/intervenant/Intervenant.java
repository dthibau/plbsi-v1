//nouvelle classe 

package com.plb.model.intervenant;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.plb.model.Contact;
import com.plb.model.Fichier;

@Entity 
@Indexed
@Table(name = "intervenant")
public class Intervenant implements Contact {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Field
	private String nom;
	@Field
	private String prenom;
	private String adresse;
	private String codePostal;
	private String ville;
	private String origine;
	private String conditionGenerale;
	@Field
	private int rang=50; // par d�fault � 50
	private String numTel;
	private String numPortable;
	@Field
	private String email;
	private String statut;
	private String statutAutre;
	@Field
	private int tarif;
	@Column(name="infoTarifIntervenant")
	private String infoTarifIntervenant;
	@Column(name="infoTarif")
	private String infoTarifPLB;
	private String centres;
	@Column(columnDefinition="bit")
	private boolean anglais;
	@Field
	@Lob
	private String observations;
	
	@Lob
	private String champLibre;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateMisAJour;  
	private String certifications;
	
	@OneToOne(cascade=CascadeType.ALL)
	private Fichier cv;
	
	@OneToOne(cascade=CascadeType.ALL,mappedBy="intervenant")
	GrilleCompetence grilleCompetence;
	
	public Intervenant() {
		super();
		grilleCompetence = new GrilleCompetence(this);
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return getNom() + " " + getPrenom();
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public int getRang() {
		return rang;
	}

	public void setRang(int rang) {
		this.rang = rang;
	}

	@Transient
	public String getRangClass() {
		if ( rang >= 90 ) {
			return "redcolor";
		} else if ( rang >=80 ) {
			return "orangecolor";
		}
		return  "greencolor";
	}
	public String getNumTel() {
		return numTel;
	}

	public void setNumTel(String numTel) {
		this.numTel = numTel;
	}

	public String getNumPortable() {
		return numPortable;
	}

	public void setNumPortable(String numPortable) {
		this.numPortable = numPortable;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}
	
	public String getStatutAutre() {
		return statutAutre;
	}

	public void setStatutAutre(String statutAutre) {
		this.statutAutre = statutAutre;
	}

	public int getTarif() {
		return tarif;
	}

	public void setTarif(int tarif) {
		this.tarif = tarif;
	}

	public String getInfoTarifIntervenant() {
		return infoTarifIntervenant;
	}

	public void setInfoTarifIntervenant(String infoTarif) {
		this.infoTarifIntervenant = infoTarif;
	}

	
	public String getInfoTarifPLB() {
		return infoTarifPLB;
	}
	public void setInfoTarifPLB(String infoTarifPLB) {
		this.infoTarifPLB = infoTarifPLB;
	}
	public String getCentres() {
		return centres;
	}

	public void setCentres(String centres) {
		this.centres = centres;
	}

	public boolean isAnglais() {
		return anglais;
	}

	public void setAnglais(boolean anglais) {
		this.anglais = anglais;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public Date getDateMisAJour() {
		return dateMisAJour;
	}

	public void setDateMisAJour(Date dateMisAJour) {
		this.dateMisAJour = dateMisAJour;
	}

	@Transient
	public long getDateMisAJourAsLong() {
		return dateMisAJour != null ? dateMisAJour.getTime() : 0l;
	}

	
	public String getCertifications() {
		return certifications;
	}

	public void setCertifications(String certifications) {
		this.certifications = certifications;
	}

	public String getConditionGenerale() {
		return conditionGenerale;
	}

	public void setConditionGenerale(String conditionGenerale) {
		this.conditionGenerale = conditionGenerale;
	}

	public GrilleCompetence getGrilleCompetence() {
		return grilleCompetence;
	}

	public void setGrilleCompetence(GrilleCompetence grilleCompetence) {
		this.grilleCompetence = grilleCompetence;
	}

	public Fichier getCv() {
		return cv;
	}

	public void setCv(Fichier cv) {
		this.cv = cv;
	}

	public String getCodePostal() {
		return codePostal;
	}
	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	public String getChampLibre() {
		return champLibre;
	}
	public void setChampLibre(String champLibre) {
		this.champLibre = champLibre;
	}
	@Transient
	public Intervenant getCopy() {
		Intervenant ret = new Intervenant();
		ret.setAdresse(adresse);
		ret.setCodePostal(codePostal);
		ret.setVille(ville);
		ret.setAnglais(anglais);
		ret.setCentres(centres);
		ret.setCertifications(certifications);
		ret.setConditionGenerale(conditionGenerale);
		ret.setCv(cv);
		ret.setEmail(email);
		ret.setInfoTarifIntervenant(infoTarifIntervenant);
		ret.setNom(nom);
		ret.setNumPortable(numPortable);
		ret.setNumTel(numTel);
		ret.setObservations(observations);
		ret.setChampLibre(champLibre);
		ret.setOrigine(origine);
		ret.setPrenom(prenom);
		ret.setRang(rang);
		ret.setStatut(statut);
		ret.setStatutAutre(statutAutre);
		ret.setTarif(tarif);

		return ret;

	}
	@Transient 
	public Date getLastUpdateDate() {
		Date ret =getGrilleCompetence().getFilledDate();
		if ( ret == null ) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -1);
			ret = cal.getTime();
		}
		return ret;
	}
	@Override
	public String toString() {
		return prenom + " "  + nom ;
	}
	
}
