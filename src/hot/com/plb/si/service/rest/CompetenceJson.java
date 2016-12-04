package com.plb.si.service.rest;

import com.plb.model.intervenant.Competence;

public class CompetenceJson implements Comparable<CompetenceJson>{
	private long id;
	private int idFormation;
	private int idIntervenant;
	private String libelle,remarques;
	private boolean support;
	private boolean known;
	private String message;
	public CompetenceJson() {
		super();
	}
	public CompetenceJson(Competence competence) {
		this.id = competence.getId();
		this.libelle = competence.getFormation().getLibelle();
		this.idFormation = competence.getFormation().getIdFormation();
		this.support = competence.getSupport();
		this.remarques = competence.getRemarques();
		this.known = false;
		this.idIntervenant = competence.getGrilleCompetence().getIntervenant().getId();
	}
	public CompetenceJson(Competence competence, int idIntervenant) {
		this.id = competence.getId();
		this.libelle = competence.getFormation().getLibelle();
		this.idFormation = competence.getFormation().getIdFormation();
		this.support = competence.getSupport();
		this.remarques = competence.getRemarques();
		this.known = this.id > 0 ? true : false;
		this.idIntervenant = idIntervenant;
	}
	public CompetenceJson(Competence competence, boolean known) {
		this(competence);
		this.known = known;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public String getRemarques() {
		return remarques;
	}
	public void setRemarques(String remarques) {
		this.remarques = remarques;
	}
	public boolean isSupport() {
		return support;
	}
	public void setSupport(boolean support) {
		this.support = support;
	}
	public int getIdFormation() {
		return idFormation;
	}
	public void setIdFormation(int idFormation) {
		this.idFormation = idFormation;
	}
	public boolean isKnown() {
		return known;
	}
	public void setKnown(boolean known) {
		this.known = known;
	}
	public int getIdIntervenant() {
		return idIntervenant;
	}
	public void setIdIntervenant(int idIntervenant) {
		this.idIntervenant = idIntervenant;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public int compareTo(CompetenceJson o) {
		// TODO Auto-generated method stub
		return getLibelle().compareTo(o.getLibelle());
	}
	
}
