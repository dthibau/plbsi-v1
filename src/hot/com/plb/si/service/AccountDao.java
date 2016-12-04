package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.directory.Account;
import com.plb.model.directory.Role;

public class AccountDao {

	private EntityManager entityManager;
	
	public AccountDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> findAll() {
		return entityManager.createQuery("from Account a order by a.prenom,a.nom").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> findByRole(Role role) {
		Query q  = entityManager.createQuery("from Account a where a.roleString like :role and a.deletedDate Is Null order by a.prenom,a.nom");
		q.setParameter("role", "%" + role + "%");
		return q.getResultList();
	}
	
	public Account findNames(String nom, String prenom) {
		Query q  = entityManager.createQuery("from Account a where a.nom = :nom and a.prenom = :prenom").setParameter("nom", nom).setParameter("prenom", prenom);
		return (Account) q.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> findAllCommercialeActive() {
		List<Account> results  = entityManager.createQuery("from Account a where (((a.deletedDate) Is Null) and lower(a.roleString) like '%commercial%') order by a.prenom,a.nom").getResultList();

		return results;
	}
}
