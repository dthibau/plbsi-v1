package com.plb.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.plb.model.Session;


public class SessionOrganismesDto implements Comparable<SessionOrganismesDto> {

	private List<String> organismes = new ArrayList<String>();
	private Session session;
	
	
	public SessionOrganismesDto(Session session) {
		super();
		this.session = session;
	}
	

	public List<String> getOrganismes() {
		return organismes;
	}


	public void setOrganismes(List<String> organismes) {
		this.organismes = organismes;
	}
	
	public void addOrganisme(String organisme) {
		this.organismes.add(organisme);
	}


	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	@Override
	public int compareTo(SessionOrganismesDto o) {
		return getSession().getDebut().compareTo(o.getSession().getDebut()) ;
	}
	
	

}
