package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.plb.model.Formation;
import com.plb.model.ProspectFormation;

public class ProspectFormationDao {
	
private EntityManager entityManager;

	
		public ProspectFormationDao(EntityManager entityManager) {
			this.entityManager = entityManager;			
		}
	
		//Fonction qui permet de retrouver tous les prospects "actif"
		@SuppressWarnings("unchecked")
		public List<ProspectFormation> findByFormation(Formation formation) {
			return (List<ProspectFormation>) entityManager.createQuery("from ProspectFormation p WHERE p.formation=:formation").setParameter("formation", formation).getResultList();
		}
		
}
