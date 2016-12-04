package com.plb.si.service.rest;

import com.plb.model.intervenant.Intervenant;

public class IntervenantJson {

	private int id;
	private String nom, prenom, email, adresse, codePostal, ville, numTel,
			numPortable;

	private int tarif;
	private String infoTarifIntervenant, centres, statut, statutAutre,
			certifications;
	private boolean anglais;
	private String champLibre, conditionGenerale;

	private String cvName = null;

	public IntervenantJson() {
		super();
	}

	public IntervenantJson(Intervenant intervenant) {
		this.id = intervenant.getId();
		this.nom = intervenant.getNom();
		this.prenom = intervenant.getPrenom();
		this.email = intervenant.getEmail();
		this.adresse = intervenant.getAdresse();
		this.codePostal = intervenant.getCodePostal();
		this.ville = intervenant.getVille();
		this.numTel = intervenant.getNumTel();
		this.numPortable = intervenant.getNumPortable();
		this.tarif = intervenant.getTarif();
		this.infoTarifIntervenant = intervenant.getInfoTarifIntervenant();
		this.centres = intervenant.getCentres();
		this.statut = intervenant.getStatut();
		this.statutAutre = intervenant.getStatutAutre();
		this.certifications = intervenant.getCertifications();
		this.anglais = intervenant.isAnglais();
		this.conditionGenerale = intervenant.getConditionGenerale();
		this.champLibre = intervenant.getChampLibre();
		if (intervenant.getCv() != null)
			this.cvName = intervenant.getCv().getName();
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
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

	public int getTarif() {
		return tarif;
	}

	public void setTarif(int tarif) {
		this.tarif = tarif;
	}

	public String getInfoTarifIntervenant() {
		return infoTarifIntervenant;
	}

	public void setInfoTarifIntervenant(String infoTarifIntervenant) {
		this.infoTarifIntervenant = infoTarifIntervenant;
	}

	public String getCentres() {
		return centres;
	}

	public void setCentres(String centres) {
		this.centres = centres;
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

	public String getCertifications() {
		return certifications;
	}

	public void setCertifications(String certifications) {
		this.certifications = certifications;
	}

	public boolean isAnglais() {
		return anglais;
	}

	public void setAnglais(boolean anglais) {
		this.anglais = anglais;
	}

	public String getChampLibre() {
		return champLibre;
	}

	public void setChampLibre(String champLibre) {
		this.champLibre = champLibre;
	}

	public String getConditionGenerale() {
		return conditionGenerale;
	}

	public void setConditionGenerale(String conditionGenerale) {
		this.conditionGenerale = conditionGenerale;
	}

	public String getCvName() {
		return cvName;
	}

	public void setCvName(String cvName) {
		this.cvName = cvName;
	}

}
