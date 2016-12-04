package com.plb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity 
@IdClass(FormationFilierePK.class)
@Table(name="formation_filiere")
public class FormationFiliere implements Comparable<FormationFiliere>{

	@Id
	@ManyToOne
	@JoinColumn(name="id_formation")
	private Formation formation;
	@Id
	@ManyToOne
	@JoinColumn(name="id_filiere")
//	@IndexedEmbedded
	private Filiere filiere;
	
	@Column(name="forfil_rang")
	private Integer rangFiliere;

	@Column(name="forfil_filiere_principale")
	private String isPrincipale = "non";
	
	public FormationFiliere() {
		super();
	}
	public FormationFiliere(Formation formation) {
		super();
		this.formation = formation;
	}
	

	public FormationFiliere getCopy() {
		FormationFiliere newFF = new FormationFiliere(this.formation);
		newFF.setFiliere(this.filiere);
		newFF.setRangFiliere(this.rangFiliere);
		
		return newFF;
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

	public Integer getRangFiliere() {
		return rangFiliere;
	}

	public void setRangFiliere(Integer rangFiliere) {
		this.rangFiliere = rangFiliere;
	}
	
	public String getIsPrincipale() {
		return isPrincipale;
	}
	public void setIsPrincipale(String isPrincipale) {
		this.isPrincipale = isPrincipale;
	}
	@Transient 
	public boolean isPrincipale() {
		return getIsPrincipale().equals("oui");
	}
	@Transient 
	public String getLibelle() {
		return filiere.getLibelle();
	}
	@Override
	public int compareTo(FormationFiliere o) {
		// TODO Auto-generated method stub
		return getFormation().compareTo(o.getFormation());
	}
}
