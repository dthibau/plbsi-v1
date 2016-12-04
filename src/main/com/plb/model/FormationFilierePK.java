package com.plb.model;

import java.io.Serializable;


public class FormationFilierePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4285757738727034148L;
	private Formation formation;
	private Filiere filiere;
	
	public FormationFilierePK() {
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filiere == null) ? 0 : filiere.hashCode());
		result = prime * result
				+ ((formation == null) ? 0 : formation.hashCode());
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
		FormationFilierePK other = (FormationFilierePK) obj;
		if (filiere == null) {
			if (other.filiere != null)
				return false;
		} else if (!filiere.equals(other.filiere))
			return false;
		if (formation == null) {
			if (other.formation != null)
				return false;
		} else if (!formation.equals(other.formation))
			return false;
		return true;
	}
	public FormationFilierePK(Formation formation, Filiere filiere) {
		this.formation = formation;
		this.filiere = filiere;
	}
	public Formation getFormation() {
		return formation;
	}
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	public Filiere getFiliere() {
		return filiere;
	}
	public void setFiliere(Filiere filiere) {
		this.filiere = filiere;
	}
	
	
}
