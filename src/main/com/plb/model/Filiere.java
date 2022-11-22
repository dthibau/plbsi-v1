package com.plb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

@Entity
@Table(name="filiere")
public class Filiere implements Comparable<Filiere> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id_filiere")
	private int id;
	
	@Column(name="fil_libelle")
	@Field
	private String libelle;
	
	@Column(name="fil_url")
	private String url;
	
	@Column(name="fil_ordre")
	private int ordre;
	
	@OneToMany(mappedBy="filiere",cascade=CascadeType.ALL)
	@ContainedIn
	List<Categorie> categories = new ArrayList<Categorie>();

	@Column(name="fil_titre")
	private String titre;
		
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Categorie> getCategories() {
		return categories;
	}

	public void setCategories(List<Categorie> categories) {
		this.categories = categories;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
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
		Filiere other = (Filiere) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return libelle ;
	}

	@Override
	public int compareTo(Filiere o) {
		
		return o.getOrdre() - getOrdre();
	}
	
	
}
