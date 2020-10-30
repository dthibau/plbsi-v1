package com.plb.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "stashed_formation")
public class StashedFormation implements Serializable {

	// Clé primaire partagée (JPA 2?)
	@Id
	@OneToOne
	@JoinColumn(name = "formation_id")
	private Formation formation;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formation == null) ? 0 : formation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if ( obj instanceof Formation ) {
			return this.getFormation().equals(obj);
		}
		if ( getClass() != obj.getClass())
			return false;
		StashedFormation other = (StashedFormation) obj;
		if (formation == null) {
			if (other.formation != null)
				return false;
		} else if (!formation.equals(other.formation))
			return false;
		return true;
	}

	public Formation getFormation() {
		return formation;
	}

	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	
	
}
