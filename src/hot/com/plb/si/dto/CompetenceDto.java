package com.plb.si.dto;

import java.io.Serializable;

import com.plb.model.Formation;
import com.plb.model.intervenant.Competence;

public class CompetenceDto implements Serializable,Comparable<CompetenceDto> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -431600746060387071L;
	private Formation formation;
	private Boolean checked;
	private Boolean support;
	private String remarques;	

	public CompetenceDto(Formation formation) {
		this.formation = formation;
		checked = false;
		support = false;
		remarques = null;
	}
	public CompetenceDto(Competence competence) {
		this.formation = competence.getFormation();
		checked = true;
		support = competence.getSupport();
		remarques = competence.getRemarques();
	}
	public Formation getFormation() {
		return formation;
	}
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public Boolean getSupport() {
		return support;
	}
	public void setSupport(Boolean support) {
		this.support = support;
	}
	public String getRemarques() {
		return remarques;
	}
	public void setRemarques(String remarques) {
		this.remarques = remarques;
	}
	@Override
	public int compareTo(CompetenceDto o) {
		return this.getFormation().compareInFiliereTo(o.getFormation());
	}
	
	
}
