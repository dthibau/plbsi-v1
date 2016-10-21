package com.plb.si.service.rest;

import com.plb.model.Formation;

public class FormationJson {

	private long id;
	private String libelle;
	
	public FormationJson(Formation formation) {
		this.id = formation.getIdFormation();
		this.libelle = formation.getLibelle();
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
	
	
}
