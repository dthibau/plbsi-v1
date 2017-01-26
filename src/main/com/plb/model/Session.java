package com.plb.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "formation_session")
public class Session implements Comparable<Session> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_session")
	private int id;
	@Column(name = "forsess_date_debut", columnDefinition="timestamp")
	@Temporal(TemporalType.DATE)
	private Date debut;
	@Column(name = "forsess_date_fin", columnDefinition="timestamp")
	@Temporal(TemporalType.DATE)
	private Date fin;
	@Column(name = "forsess_promotion", columnDefinition="smallint")
	private int promotion;

	@ManyToOne
	@JoinColumn(name = "id_formation")
	private Formation formation;
	
//	@ManyToOne(optional=true)
//	FormationPartenaire formationPartenaire;

	public Session() {

	}

	public Session(Formation formation) {
		this.formation = formation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDebut() {
		return debut;
	}

	public void setDebut(Date debut) {
		this.debut = debut;
	}

	@Transient
	public int getYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(debut);
		return cal.get(Calendar.YEAR);
	}
	@Transient
	public void setYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(debut);
		cal.set(Calendar.YEAR,year);
		debut = cal.getTime();
		cal.setTime(fin);
		cal.set(Calendar.YEAR,year);
		fin = cal.getTime();
		
	}
	@Transient
	public int getMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(debut);
		return cal.get(Calendar.MONTH);
	}
	@Transient
	public int getDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(debut);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	@Transient
	public int getDayFin() {
		Calendar cal = Calendar.getInstance();
		if ( fin != null ) {
			cal.setTime(fin);
			return cal.get(Calendar.DAY_OF_MONTH);
		}
		return -1;
	}
	public Date getFin() {
		return fin;
	}

	public void setFin(Date fin) {
		this.fin = fin;
	}

	public int getPromotion() {
		return promotion;
	}

	public void setPromotion(int promotion) {
		this.promotion = promotion;
	}

	public Formation getFormation() {
		return formation;
	}

	public void setFormation(Formation formation) {
		this.formation = formation;
	}

//	public FormationPartenaire getFormationPartenaire() {
//		return formationPartenaire;
//	}
//
//	public void setFormationPartenaire(FormationPartenaire formationPartenaire) {
//		this.formationPartenaire = formationPartenaire;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		if ( id == 0 && other.id == 00 ) {
			return getFormation().equals(other.getFormation()) && getDebut().equals(other.getDebut());
		}
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return "Du " + df.format(debut) + " au " + df.format(fin);
	}

	@Override
	public int compareTo(Session o) {
		return getDebut().compareTo(o.getDebut()) ;
	}

}
