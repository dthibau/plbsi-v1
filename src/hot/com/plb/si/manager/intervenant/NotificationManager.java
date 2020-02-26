package com.plb.si.manager.intervenant;

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
import com.plb.model.event.NotificationIntervenantsEvent;
import com.plb.si.service.EventDao;

@Name("notificationManager")
@Scope(ScopeType.CONVERSATION)
public class NotificationManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6823897557582796385L;

	@Out
	public static int PAGE_SIZE=20;
	
	public int currentPage=0;
	
	@In EntityManager entityManager;
	
	List<NotificationIntervenantsEvent> notifications;
	EventDao eventDao;
	
	@Logger
	Log log;
	
	@Create
	public void init() {
		log.debug("Creating NotificationManager");
		eventDao = new EventDao(entityManager);
	}
	@Begin(join=true)
	public List<NotificationIntervenantsEvent> getNotifications() {
		
		if ( notifications == null ) {
			log.debug("getNotifications");
			notifications = eventDao.findLastNotifications(currentPage,PAGE_SIZE);
		}
		return notifications;
	}
	
	public List<String> getAttachmentNames(NotificationIntervenantsEvent event) {
		
		return eventDao.findAttachmentNames(event);
	}

	public void forward() {
		if ( currentPage > 0 ) {
			currentPage--;
		}
		notifications = null;
		
	}
	public void backward() {
		currentPage++;
		notifications = null;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	
}
