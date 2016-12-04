package com.plb.model;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reference_spe")

public class ReferenceSpe implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5550146796989213093L;

	
	//Attributs de l'objet
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_reference_spe")
	private int idReferenceSpe;
	
	@Column(name = "libelle")
	private String libelle;
	
	@Column(name = "duree")
	private String duree;
	
	@Column(name = "session")
	private String session;
	
	@Column(name = "participant")
	private String participant;
	
	//GETTEURS AND SETTEURS

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDuree() {
		return duree;
	}

	public void setDuree(String duree) {
		this.duree = duree;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}

	public int getIdReferenceSpe() {
		return idReferenceSpe;
	}

	public void setIdReferenceSpe(int idReferenceSpe) {
		this.idReferenceSpe = idReferenceSpe;
	}
	
}
