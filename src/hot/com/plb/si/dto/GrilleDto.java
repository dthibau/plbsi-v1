package com.plb.si.dto;

import javax.persistence.Transient;

import com.plb.model.intervenant.Intervenant;
import com.plb.si.util.Labels;

public class GrilleDto {

	private Intervenant intervenant;
	private boolean selected = false;
	
	public GrilleDto(Intervenant intervenant) {
		this.intervenant = intervenant;
	}
	public Intervenant getIntervenant() {
		return intervenant;
	}
	public void setIntervenant(Intervenant intervenant) {
		this.intervenant = intervenant;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	@Transient
	public String getDateNotify() {
		return intervenant != null && intervenant.getGrilleCompetence() != null && intervenant.getGrilleCompetence().getNotifiedDate() != null ? Labels.formatDate(intervenant.getGrilleCompetence().getNotifiedDate()) : Labels.getString("never");
	}
	@Transient
	public long getDateNotifyAsLong() {
		return intervenant != null && intervenant.getGrilleCompetence() != null && intervenant.getGrilleCompetence().getNotifiedDate() != null ? intervenant.getGrilleCompetence().getNotifiedDate().getTime() : 0l;
	}
	@Transient
	public String getDateGrille() {
		return intervenant != null && intervenant.getGrilleCompetence() != null && intervenant.getGrilleCompetence().getFilledDate() != null ? Labels.formatDate(intervenant.getGrilleCompetence().getFilledDate()) : Labels.getString("never");
	}
	@Transient
	public long getDateGrilleAsLong() {
		return intervenant != null && intervenant.getGrilleCompetence() != null && intervenant.getGrilleCompetence().getFilledDate() != null ? intervenant.getGrilleCompetence().getFilledDate().getTime() : 0l;
	}
	public boolean isOutdated() { 
		if ( intervenant.getGrilleCompetence() == null || intervenant.getGrilleCompetence().getNotifiedDate() == null || intervenant.getGrilleCompetence().getFilledDate() == null || intervenant.getGrilleCompetence().getFilledDate().before(intervenant.getGrilleCompetence().getNotifiedDate() ) ) {
			return true;
		}
		return false;
	}
	
	
}
