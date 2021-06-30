package com.plb.dto;

import com.plb.model.Session;


public class SessionOrganismeDto implements Comparable<SessionOrganismeDto> {

	private String organisme;
	private Session session;
	
	
	public SessionOrganismeDto(String organisme, Session session) {
		super();
		this.organisme = organisme;
		this.session = session;
	}
	
	public String getOrganisme() {
		return organisme;
	}
	public void setOrganisme(String organisme) {
		this.organisme = organisme;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	@Override
	public int compareTo(SessionOrganismeDto o) {
		return getSession().getDebut().compareTo(o.getSession().getDebut()) ;
	}

	@Override
	public String toString() {
		return session.toString() + " (" + organisme + ")";
	}
	
	
}
