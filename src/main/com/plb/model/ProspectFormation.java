package com.plb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="prospect_formation")
public class ProspectFormation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne
	private Formation formation;
	
	@ManyToOne
	private Prospect prospect;
	
	@Column(name = "duree_voulu")
	private String dureeVoulu;
	
	@Column(name = "session")
	private String session;
	
	@Column(name = "participant")
	private String participant;
	
	@OneToMany(mappedBy="prospectFormation",cascade=CascadeType.ALL)
	List<IntervenantIntra> intervenants = new ArrayList<IntervenantIntra>();
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Formation getFormation() {
		return formation;
	}
	
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	
	public Prospect getProspect() {
		return prospect;
	}
	
	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}

	public String getDureeVoulu() {
		return dureeVoulu;
	}

	public void setDureeVoulu(String dureeVoulu) {
		this.dureeVoulu = dureeVoulu;
	}

	public List<IntervenantIntra> getIntervenants() {
		return intervenants;
	}

	public void setIntervenants(List<IntervenantIntra> intervenants) {
		this.intervenants = intervenants;
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

	@Override
	public String toString() {
		return "ProspectFormation [id=" + id + ", formation=" + formation
				+ ", prospect=" + prospect + "]";
	}

}
