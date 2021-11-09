package com.plb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="categorie")
public class Categorie {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "categorieGenerator")
	@SequenceGenerator(name = "categorieGenerator", sequenceName = "CATEGORIE_ID", initialValue = 1500000, allocationSize = 1)
	@Column(name="id_categorie")
	private int id;
	
	@Column(name="cat_libelle")
	private String libelle;
	
	@ManyToOne
	@JoinColumn(name="id_filiere_principale")
	Filiere filiere;
	
	// Web
	@Lob
	@Column(name="cat_description", columnDefinition="text")
	private String description;
	
	@Column(name="cat_balise_title")
	private String baliseTitle;
	
	@Column(name="cat_balise_description", columnDefinition="text")
	private String baliseDescription;
	
	@Column(name="cat_balise_keywords", columnDefinition="text")
	private String baliseKeywords;
	
	@Column(name="cat_url")
	private String url;

	@Column(name="cat_rang")
	private int rang;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="baseCategorie")
	@OrderBy("order")
	List<CategorieConnexe> categoriesConnexes = new ArrayList<CategorieConnexe>();
	
	@Column(name="cat_afficher_haut", columnDefinition = "TINYINT")
	Integer afficherHaut=1;

	@Column(name="data_version")
	private Integer dataVersion=3;
	
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


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBaliseTitle() {
		return baliseTitle;
	}

	public void setBaliseTitle(String baliseTitle) {
		this.baliseTitle = baliseTitle;
	}

	public String getBaliseDescription() {
		return baliseDescription;
	}

	public void setBaliseDescription(String baliseDescription) {
		this.baliseDescription = baliseDescription;
	}

	public String getBaliseKeywords() {
		return baliseKeywords;
	}

	public void setBaliseKeywords(String baliseKeywords) {
		this.baliseKeywords = baliseKeywords;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	public List<CategorieConnexe> getCategoriesConnexes() {
		return categoriesConnexes;
	}

	public void setCategoriesConnexes(List<CategorieConnexe> categoriesConnexes) {
		this.categoriesConnexes = categoriesConnexes;
	}

	public List<String> getCategoriesConnexesAsCategories() {
		List<String> ret = new ArrayList<String>(getCategoriesConnexes().size());
		
		for ( CategorieConnexe catConnexe : categoriesConnexes ) {
			ret.add(catConnexe.getLinkedCategorie().getLibelle().replaceAll(",", "|"));
		}
		
		return ret;
	}

	public Integer getAfficherHaut() {
		return afficherHaut;
	}

	public void setAfficherHaut(Integer afficherHaut) {
		this.afficherHaut = afficherHaut;
	}

	public Integer getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(Integer dataVersion) {
		this.dataVersion = dataVersion;
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
