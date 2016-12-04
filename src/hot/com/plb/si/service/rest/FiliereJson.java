package com.plb.si.service.rest;

import com.plb.model.Filiere;

public class FiliereJson {

	private String libelle;
	private long id;
	
	public FiliereJson(Filiere f) {
		this.libelle = f.getTitre();
		this.id = f.getId();
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
}
