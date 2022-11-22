package com.plb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="categorie")
public class Categorie {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "categorieGenerator")
	@SequenceGenerator(name = "categorieGenerator", sequenceName = "categorie_id", initialValue = 1500000, allocationSize = 1)
	@Column(name="id_categorie")
	private int id;
	
	@Column(name="cat_libelle")
	private String libelle;
	
	@ManyToOne
	@JoinColumn(name="id_filiere_principale")
	Filiere filiere;
	
	@Column(name="cat_url")
	private String url;

	@Column(name="cat_rang")
	private int rang;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRang() {
		return rang;
	}

	public void setRang(int rang) {
		this.rang = rang;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Filiere getFiliere() {
		return filiere;
	}

	public void setFiliere(Filiere filiere) {
		this.filiere = filiere;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
		Categorie other = (Categorie) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
