package com.plb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="categorie_cat")
public class CategorieConnexe {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id_categorie_cat")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_categorie")
	private Categorie baseCategorie;
	
	@ManyToOne
	@JoinColumn(name="id_categorie_assoc")
	private Categorie linkedCategorie;
	
	@Column(name="`order`")
	private int order;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Categorie getBaseCategorie() {
		return baseCategorie;
	}

	public void setBaseCategorie(Categorie baseCategorie) {
		this.baseCategorie = baseCategorie;
	}

	public Categorie getLinkedCategorie() {
		return linkedCategorie;
	}

	public void setLinkedCategorie(Categorie linkedCategorie) {
		this.linkedCategorie = linkedCategorie;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	
}
