package com.plb.si.manager;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.plb.model.event.Event;
import com.plb.si.service.EventDao;

@Name("homeManager")
@Scope(ScopeType.CONVERSATION)
public class HomeManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6823897557582796385L;

	@Out
	public static int PAGE_SIZE=20;
	
	public int currentPage=0;
	
	@In EntityManager entityManager;
	
	List<Event> historique;
	EventDao eventDao;
	
	@Logger
	Log log;
	
	@Create
	public void init() {
		log.debug("Creating HomeManager");
		eventDao = new EventDao(entityManager);
	}
	@Begin(join=true)
	public List<Event> getHistorique() {
		
		if ( historique == null ) {
			log.debug("getHistorique()");
			historique = eventDao.findLast(currentPage,PAGE_SIZE);
		}
		return historique;
	}
	
	public void forward() {
		if ( currentPage > 0 ) {
			currentPage--;
		}
		historique = null;
		
	}
	public void backward() {
		currentPage++;
		historique = null;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	
}
