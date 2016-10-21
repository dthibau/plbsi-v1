package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.plb.model.Partenaire;

public class PartenaireDao {

	private EntityManager entityManager;
	
	public PartenaireDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public List<Partenaire> findAll( ) {
		return entityManager.createQuery("from Partenaire p order by p.nom").getResultList();
	}
}
