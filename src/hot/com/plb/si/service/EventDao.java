package com.plb.si.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Formation;
import com.plb.model.InformationIntra;
import com.plb.model.Prospect;
import com.plb.model.event.Event;
import com.plb.model.intervenant.Intervenant;

public class EventDao {

	private EntityManager entityManager;

	public EventDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public List<Event> findAll() {
		Query query = entityManager
				.createQuery("from Event e order by e.date desc");

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Event> findLast(int max) {
		Query query = entityManager.createQuery(
				"from Event e order by e.date desc").setMaxResults(max);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Event> findLast(int page, int max) {
		int first = page * max;
		Query query = entityManager
				.createQuery("from Event e order by e.date desc")
				.setFirstResult(first).setMaxResults(max);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Event> findAll(Formation formation) {
		Query query = entityManager
				.createQuery("from Event e where e.formation = :formation order by e.date desc");
		query.setParameter("formation", formation);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Event> findAll(Prospect prospect, InformationIntra intra) {
		Query query = entityManager
				.createQuery("from Event e where e.intra = :intra or e.prospect = :prospect order by e.date desc");
		query.setParameter("prospect", prospect);
		query.setParameter("intra", intra);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Event> findAll(InformationIntra intra) {
		Query query = entityManager
				.createQuery("from Event e where e.intra = :intra order by e.date asc");
		query.setParameter("intra", intra);

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Event> findAll(Intervenant intervenant) {
		Query query = entityManager
				.createQuery("from Event e where e.intervenant = :intervenant order by e.date asc");
		query.setParameter("intervenant", intervenant);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public void deletEventProspect(Prospect p) {
		try {
			List<Event> events = entityManager
					.createQuery("from Event e where e.prospect=:pro")
					.setParameter("pro", p).getResultList();
			for (Event event : events) {
				entityManager.remove(event);
			}
			if (p.getInformationIntra() != null) {
				events = entityManager
						.createQuery("from Event e where e.intra=:intra")
						.setParameter("intra", p.getInformationIntra())
						.getResultList();
				for (Event event : events) {
					entityManager.remove(event);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
