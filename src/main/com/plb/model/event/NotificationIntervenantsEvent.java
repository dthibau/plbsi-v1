package com.plb.model.event;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.plb.model.intervenant.Intervenant;
import com.plb.util.Util;

@Entity
@DiscriminatorValue("NotificationIntervenants")
public class NotificationIntervenantsEvent extends Event {

	private String subject;
	
	private boolean includeUrl = true;
	
	@ManyToMany
	List<Intervenant> intervenants = new ArrayList<Intervenant>();

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isIncludeUrl() {
		return includeUrl;
	}

	public void setIncludeUrl(boolean includeUrl) {
		this.includeUrl = includeUrl;
	}

	public List<Intervenant> getIntervenants() {
		return intervenants;
	}

	public void setIntervenants(List<Intervenant> intervenants) {
		this.intervenants = intervenants;
	}

	@Override
	public String getDestinatairesAsString() {
		
		return Util.getCollectionAsString(intervenants);
	}

	@Override
	public boolean hasDestinataires() {

		return intervenants != null && intervenants.size() > 0;

	}

	@Override
	public String[] getMessageAsArray() {
		String[] messageAsArray = new String[2];
		messageAsArray[0] = getMessage();
		messageAsArray[1] = isIncludeUrl() ? "Lien vers grille de compétence" : "Pas de lien vers grille de compétence";
		
		return messageAsArray;
	}

	
	
	
}
