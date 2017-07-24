package com.plb.si.service;


import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.plb.model.ProspectDetail;


public class ProspectDetailDao {
	
	private EntityManager entityManager;
	
	public ProspectDetailDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	//Fonction qui permet de retrouver tous les prospects
	@SuppressWarnings("unchecked")
	public List<ProspectDetail> findAll() {
		return (List<ProspectDetail>) entityManager.createQuery("from Prospect ORDER BY date DESC").getResultList();
	}
	
	
	
}