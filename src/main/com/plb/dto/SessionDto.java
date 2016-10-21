package com.plb.dto;

import java.util.ArrayList;
import java.util.List;

import com.plb.model.FormationPartenaire;
import com.plb.model.Session;

public class SessionDto {

	private String organisme;
	
	private MonthSessionDto[] arraySessions = new MonthSessionDto[12];

	public SessionDto(List<Session> sessions, int year) {
		_initArray();
		organisme = "PLB";
		alimentArray(sessions, year);
	}
	
	public SessionDto(FormationPartenaire fp, int year) {
		_initArray();
		organisme = fp.getPartenaire().getNom();
		alimentArray(fp.getSessions(), year);
	}
	
	private void _initArray() {
		for ( int i=0; i<arraySessions.length; i++) 
			arraySessions[i] = new MonthSessionDto();
	}
	private void alimentArray(List<Session> sessions, int year) {
		for ( Session session : sessions ) {
			if ( session.getYear() == year ) {
				arraySessions[session.getMonth()].getSessions().add(session);
			}
		}
	}

	
	public String getOrganisme() {
		return organisme;
	}

	public void setOrganisme(String organisme) {
		this.organisme = organisme;
	}

	public MonthSessionDto[] getArraySessions() {
		return arraySessions;
	}

	public void setArraySessions(MonthSessionDto[] arraySessions) {
		this.arraySessions = arraySessions;
	}


	public List<Session> getAllSessions() {
		List<Session> ret = new ArrayList<Session>();
		
		for ( int i=0; i<arraySessions.length; i++) {
			ret.addAll(arraySessions[i].getSessions());
		}
		
		return ret;
	}
	
	
	
}
