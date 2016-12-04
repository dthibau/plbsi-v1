package com.plb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="formation_mutualisees")
public class FormationMutualisees {

	@Id @GeneratedValue
	private int id;
	
	@OneToMany(mappedBy="formationMutualisees")
	private List<Formation> formations = new ArrayList<Formation>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Formation> getFormations() {
		return formations;
	}

	public void setFormations(List<Formation> formations) {
		this.formations = formations;
	}
	public void addFormation(Formation formation) {
		formations.add(formation);
		formation.setFormationMutualisees(this);
	}
	public void removeFormation(Formation formation) {
		formations.remove(formation);
		formation.setFormationMutualisees(null);
	}
	
}
