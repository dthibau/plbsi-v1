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
	
	@Column(name="new_fil_color")
	private String color;
	
	@Column(name="fil_color_titre")
	private String colorTitre;

	@OneToMany(mappedBy="filiere",cascade=CascadeType.ALL)
	@ContainedIn
	List<Categorie> categories = new ArrayList<Categorie>();

	@Lob
	@Column(name="fil_description", columnDefinition="mediumtext")
	private String description;
	
	@Column(name="fil_titre")
	private String titre;
	
	@Column(name="fil_balise_title")
	private String metaTitre;
	
	@Column(name="fil_balise_description", columnDefinition="text")
	private String metaDescription;
	
	@Column(name="fil_balise_keywords", columnDefinition="text")
	private String metaKeywords;
	
	@Column(name="fil_image_petit")
	private String imagePetit;
	
	@Column(name="new_fil_image_moyen")
	private String imageMoyen;
	
	@Column(name="fil_logo")
	private String logo;
	
	@Column(name="fil_mini_description")
	private String miniDescription;
	
	@Column(name="data_version")
	private Integer dataVersion=3;
	
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getMetaTitre() {
		return metaTitre;
	}

	public void setMetaTitre(String metaTitle) {
		this.metaTitre = metaTitle;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	
	public String getImagePetit() {
		return imagePetit;
	}

	public void setImagePetit(String imagePetit) {
		this.imagePetit = imagePetit;
	}

	public String getImageMoyen() {
		return imageMoyen;
	}

	public void setImageMoyen(String imageMoyen) {
		this.imageMoyen = imageMoyen;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMiniDescription() {
		return miniDescription;
	}

	public void setMiniDescription(String miniDescription) {
		this.miniDescription = miniDescription;
	}

	public Integer getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(Integer dataVersion) {
		this.dataVersion = dataVersion;
	}

	public String getColorTitre() {
		return colorTitre;
	}

	public void setColorTitre(String colorTitre) {
		this.colorTitre = colorTitre;
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
