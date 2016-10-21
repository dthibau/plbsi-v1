package com.plb.model;

import java.util.Calendar;
import java.util.Date;

import com.plb.model.directory.Account;

public class ProspectCritere {
	public static int ALL = 0;
	public static int TODO = 1; // A faire
	public static int ENCOURS = 2; // Les affaires en cours tout statuts
									// confondus

	private String statut;

	private Account commercial;

	private String reference;

	private int state;

	private String typo;

	private TypeContact typeContact;

	private Date dateDebut, dateFin;

	// GETTEURS AND SETTEURS

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Account getCommercial() {
		return commercial;
	}

	public void setCommercial(Account commercial) {
		this.commercial = commercial;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isAll() {
		return state == ALL;
	}

	public String getTypo() {
		return typo;
	}

	public void setTypo(String typo) {
		this.typo = typo;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		if (dateDebut != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateDebut);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			this.dateDebut = cal.getTime();
		} else {
			this.dateDebut = null;
		}
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		if (dateFin != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFin);
			cal.set(Calendar.HOUR, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			this.dateFin = dateFin;
		} else {
			this.dateFin = null;
		}
	}

	public TypeContact getTypeContact() {
		return typeContact;
	}

	public void setTypeContact(TypeContact typeContact) {
		this.typeContact = typeContact;
	}

}
