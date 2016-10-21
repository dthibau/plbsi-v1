package com.plb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="formation_partenaire")
public class FormationPartenaire {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne
	private Formation formation;
	@ManyToOne
	private Partenaire partenaire;
//	@OneToMany(cascade=CascadeType.ALL,orphanRemoval=true,mappedBy="formationPartenaire")
	@OneToMany(cascade=CascadeType.ALL,orphanRemoval=true)
	private List<Session> sessions=new ArrayList<Session>();
	
	private float prix;
	private String code;
	private String url;
	
	public FormationPartenaire() {
		
	}
	public FormationPartenaire(Formation formation) {
		this.formation = formation;
	}
	public FormationPartenaire getCopy() {
		FormationPartenaire newFP = new FormationPartenaire();
		newFP.setId(this.id);
		newFP.setFormation(this.formation);
		newFP.setPartenaire(this.partenaire);
		newFP.setCode(this.code);
		newFP.setPrix(this.prix);
		newFP.setUrl(this.url);
		
		return newFP;
	}
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
	public Partenaire getPartenaire() {
		return partenaire;
	}
	public void setPartenaire(Partenaire partenaire) {
		this.partenaire = partenaire;
	}
	public float getPrix() {
		return prix;
	}
	public void setPrix(float prix) {
		this.prix = prix;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<Session> getSessions() {
		return sessions;
	}
	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}
	
	public void addSessions(List<Session> sessions) {
//		for ( Session session : sessions ) {
//			session.setFormationPartenaire(this);
//		}
		this.sessions.addAll(sessions);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		FormationPartenaire other = (FormationPartenaire) obj;
		if ( id == 0 ) { // En cours d'édition
			if ( partenaire != null && code != null ) {
				return partenaire.equals(other.getPartenaire()) && code.equals(other.getCode());
			} else 
				return false;
		} else if (id != other.id) // Persité en base
			return false;
		return true;
	}
	@Override
	public String toString() {
		return  partenaire + "/"+ code;
	}
	
	
	
}
