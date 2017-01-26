package com.plb.model.intervenant;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.plb.model.Formation;

@Entity
@Table(name="competence")
public class Competence implements Serializable,Comparable<Competence> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2564836119556079445L;
	
	
//	public static int NON=0;
//	public static int OUI=1;
//	public static int NON_RENSEIGNE=2;
	
	@Id @GeneratedValue
	private long id;
	@ManyToOne
	private Formation formation;
	@ManyToOne
	private GrilleCompetence grilleCompetence;
	
	@Column(columnDefinition="bit")
	private Boolean support=false;
	private String remarques;
	
	public Competence() {
		
	}
	public Competence(Formation formation) {
		this.formation = formation;
		this.remarques="";
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Formation getFormation() {
		return formation;
	}
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	public GrilleCompetence getGrilleCompetence() {
		return grilleCompetence;
	}
	public void setGrilleCompetence(GrilleCompetence grilleCompetence) {
		this.grilleCompetence = grilleCompetence;
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
	
	@Transient
	public Intervenant getIntervenant() {
		return getGrilleCompetence().getIntervenant();
	}
	@Override
	public int compareTo(Competence o) {
		// TODO Auto-generated method stub
		return this.getFormation().compareInFiliereTo(o.getFormation());
	}
	@Override
	public String toString() {
		return "Competence [formation=" + formation + ", support=" + support + "]";
	}
	
}
