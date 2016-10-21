package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Formation;
import com.plb.model.intervenant.Competence;
import com.plb.model.intervenant.Intervenant;

public class CompetenceDao {
	
	private EntityManager entityManager;

	public CompetenceDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}


	@SuppressWarnings("unchecked")
	public List<Competence> findByFormation(Formation formation) {
		Query q = entityManager.createQuery("select competence from Competence competence where competence.formation = :formation");
		q.setParameter("formation", formation);
		
		return (List<Competence>) q.getResultList();
	}
	
	public Competence findByFormationAndIntervenant(Formation formation, Intervenant intervenant) {
		Query q = entityManager.createQuery("select competence from Competence competence where competence.formation = :formation and competence.grilleCompetence=:grilleCompetence");
		q.setParameter("formation", formation);
		q.setParameter("grilleCompetence", intervenant.getGrilleCompetence());
		
		return (Competence) q.getSingleResult();
	}
	

}
