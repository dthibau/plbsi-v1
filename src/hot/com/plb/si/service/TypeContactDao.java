package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.plb.model.TypeContact;

public class TypeContactDao {
	
	private EntityManager entityManager;
	
	public TypeContactDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public List<TypeContact> findAll(){
		return (List<TypeContact>) entityManager.createQuery("from TypeContact ORDER BY libelle ASC").getResultList();
	}

}
