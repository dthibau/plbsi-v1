package com.plb.si.dto;

import java.util.Collections;
import java.util.List;

import com.plb.model.Formation;
import com.plb.model.intervenant.Competence;

public class FormationCompetencesDto {

	private Formation formation;
	private List<Competence> competences;
	
	private CompetenceRangComparator rangComparator = new CompetenceRangComparator();
	

	public FormationCompetencesDto(Formation formation,
			List<Competence> competences) {
		super();
		this.formation = formation;
		this.competences = competences;
		Collections.sort(this.competences,rangComparator);	
	}
	
	public Formation getFormation() {
		return formation;
	}
	public void setFormation(Formation formation) {
		this.formation = formation;
	}

	public List<Competence> getCompetences() {
		
		return competences;
	}

	public void setCompetences(List<Competence> competences) {

		this.competences = competences;
		Collections.sort(this.competences,rangComparator);	
	}




	
}
