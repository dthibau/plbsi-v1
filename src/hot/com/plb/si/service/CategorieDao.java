package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Categorie;

public class CategorieDao {

	private EntityManager entityManager;
	
	public CategorieDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public List<Categorie> findAll() {
		return entityManager.createQuery("from Categorie c order by c.libelle").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categorie> findReferents(Categorie c) {
		Query q = entityManager.createQuery("select con.baseCategorie from CategorieConnexe con where con.linkedCategorie = :categorie order by con.baseCategorie.libelle");
		q.setParameter("categorie", c);
		return q.getResultList();
	}
	
	
}
