package com.plb.si.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Session;

public class SessionDao {

private EntityManager entityManager;
	
	public SessionDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public Session getOldestSession() {
		Query q = entityManager.createQuery("from Session s order by s.debut").setMaxResults(1);
		
		return (Session)q.getSingleResult();
	}
	public Session getNewestSession() {
		Query q = entityManager.createQuery("from Session s order by s.debut desc").setMaxResults(1);
		
		return (Session)q.getSingleResult();
	}
}
