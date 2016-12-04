package com.plb.si.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Prospect;
import com.plb.model.message.Message;

public class MessageDao {
	
	private EntityManager entityManager;
	
	public MessageDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public List<Message> findAll() {
		Query query = entityManager.createQuery("from Message m order by m.date desc");
		return query.getResultList();
	}

	//Fonction permettant de recuperer l'ensemble des messages associes a un prospect (note perso)
	@SuppressWarnings("unchecked")
	public List<Message> findAllOrderedByDate(Prospect prospect) {
		Query query = entityManager.createQuery("from Message m where m.prospect = :prospect order by m.date ASC");
		query.setParameter("prospect", prospect);
		return query.getResultList();
	}
	
	public void deleteMessages(Prospect prospect) {
		List<Message> msgs = findAllOrderedByDate(prospect);
		for ( Message message : msgs ) {
			entityManager.remove(message);
		}
	}
}
