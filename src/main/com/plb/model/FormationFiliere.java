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
	
	@ManyToOne
	@JoinColumn(name="id_categorie")
	private Categorie categorie;
	
	@Column(name="forfil_rang")
	private Integer rang;

	@Column(name="forfil_filiere_principale", columnDefinition="set")
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
		newFF.setRang(this.rang);
		
		return newFF;
	}
	public Formation getFormation() {
		return formation;
	}

	public void setFormation(Formation formation) {
		this.formation = formation;
	}

	public Categorie getCategorie() {
		return categorie;
	}
	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
		this.filiere = categorie.getFiliere();
	}
	public Filiere getFiliere() {
		return filiere;
	}

	public void setFiliere(Filiere filiere) {
		this.filiere = filiere;
	}

	public Integer getRang() {
		return rang;
	}

	public void setRang(Integer rangCategorie) {
		this.rang = rangCategorie;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categorie == null) ? 0 : categorie.hashCode());
		result = prime * result + ((filiere == null) ? 0 : filiere.hashCode());
		result = prime * result + ((formation == null) ? 0 : formation.hashCode());
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
		FormationFiliere other = (FormationFiliere) obj;
		if (categorie == null) {
			if (other.categorie != null)
				return false;
		} else if (!categorie.equals(other.categorie))
			return false;
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
	
}
