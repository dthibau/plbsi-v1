package com.plb.dto;

import java.util.ArrayList;
import java.util.List;

import com.plb.model.Session;

public class MonthSessionDto {

	private List<Session> sessions = new ArrayList<Session>();

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public void addSession(Session session) {
		sessions.add(session);
	}
	
}
