package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Formation;
import com.plb.model.devis.Devis;
import com.plb.model.directory.Account;

public class DevisDao {

	private EntityManager entityManager;
	
	public DevisDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public List<Devis> findByFormation(Formation formation) {
		Query query = entityManager.createQuery("from Devis d where d.formation=:formation order by d.date desc");
		query.setParameter("formation", formation);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Devis> findLast(Account account, Formation formation) {
		Query query = entityManager.createQuery("from Devis d where d.auteur=:auteur and d.formation=:formation order by d.date desc");
		query.setParameter("auteur", account);
		query.setParameter("formation", formation);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Devis> findLast(Account account) {
		Query query = entityManager.createQuery("from Devis d where d.auteur=:auteur order by d.date desc");
		query.setParameter("auteur", account);
		
		return query.getResultList();
	}
	
	
	
}
