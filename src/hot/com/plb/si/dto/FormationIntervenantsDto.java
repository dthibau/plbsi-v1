package com.plb.si.dto;

import java.util.List;

import com.plb.model.Formation;
import com.plb.model.intervenant.Intervenant;

public class FormationIntervenantsDto {

	private Formation formation;
	private List<Intervenant> intervenants;
	

	public FormationIntervenantsDto(Formation formation,
			List<Intervenant> intervenants) {
		super();
		this.formation = formation;
		this.intervenants = intervenants;
	}
	
	public Formation getFormation() {
		return formation;
	}
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	public List<Intervenant> getIntervenants() {
		return intervenants;
	}
	public void setIntervenants(List<Intervenant> intervenants) {
		this.intervenants = intervenants;
	}
	
	
}
