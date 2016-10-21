package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.plb.model.Filiere;

public class FiliereDao {

	private EntityManager entityManager;
	
	public FiliereDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public List<Filiere> findAll() {
		return entityManager.createQuery("from Filiere f order by f.libelle").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Filiere> findAllOrdered() {
		return entityManager.createQuery("from Filiere f order by f.ordre").getResultList();
	}
}
