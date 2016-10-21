package com.plb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "typecontact")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class TypeContact implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6123363523011473873L;

	//Attributs de l'objet Prospect
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_typecontact")
	private int idTypeContact;
	
	@Column(name = "libelle")
	@NotNull
	private String libelle;

	//GETTEURS and SETTEURS
	
	public int getIdTypeContact() {
		return idTypeContact;
	}

	public void setIdTypeContact(int idTypeContact) {
		this.idTypeContact = idTypeContact;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idTypeContact;
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
		TypeContact other = (TypeContact) obj;
		if (idTypeContact != other.idTypeContact)
			return false;
		return true;
	}
	
	
}
