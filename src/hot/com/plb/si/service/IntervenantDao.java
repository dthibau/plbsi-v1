package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.IntervenantIntra;
import com.plb.model.event.IntervenantModificationEvent;
import com.plb.model.event.NotificationIntervenantsEvent;
import com.plb.model.intervenant.Intervenant;

public class IntervenantDao {
	
	private EntityManager entityManager;

	public IntervenantDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public List<Intervenant> findAll() {
		return (List<Intervenant>) entityManager.createQuery("from Intervenant i order by i.nom, i.prenom ").getResultList();
	}
	
	public Intervenant findByUrl(String url) {
		Query q = entityManager.createQuery("from Intervenant i where i.grilleCompetence.url = :url ");
		q.setParameter("url", url);
		
		return (Intervenant)q.getSingleResult();
	}

//	@SuppressWarnings("unchecked")
//	public List<Intervenant> findByFormation(Formation formation) {
//		Query q = entityManager.createQuery("select competence.grilleCompetence.intervenant from Competence competence where competence.anime = :anime and competence.formation = :formation");
//		q.setParameter("formation", formation);
//		q.setParameter("anime", true);
//		
//		return (List<Intervenant>) q.getResultList();
//	}
	
	@SuppressWarnings("unchecked")
	public void remove(Intervenant intervenant) {
		// Remove from Historique
		Query q = entityManager.createQuery("from IntervenantModificationEvent ime where ime.intervenant = :intervenant");
		q.setParameter("intervenant", intervenant);
		List<IntervenantModificationEvent> imEvents = (List<IntervenantModificationEvent>)q.getResultList();
		for ( IntervenantModificationEvent ime : imEvents ) {
			entityManager.remove(ime);
		}
		q = entityManager.createQuery("from NotificationIntervenantsEvent nie where :intervenant MEMBER OF nie.intervenants");
		q.setParameter("intervenant", intervenant);
		List<NotificationIntervenantsEvent> niEvents = (List<NotificationIntervenantsEvent>)q.getResultList();
		for ( NotificationIntervenantsEvent nie : niEvents ) {
			nie.getIntervenants().remove(intervenant);
			if ( nie.getIntervenants().isEmpty() ) {
				entityManager.remove(nie);
			}
		}
		// Intervenant Intra
		q = entityManager.createQuery("from IntervenantIntra i where i.intervenant = :intervenant");
		q.setParameter("intervenant", intervenant);
		List<IntervenantIntra> iIntra = (List<IntervenantIntra>)q.getResultList();
		for ( IntervenantIntra iintra : iIntra ) {
			entityManager.remove(iintra);
		}
		//Suppression Intervenant
		entityManager.remove(intervenant);
	}
}
