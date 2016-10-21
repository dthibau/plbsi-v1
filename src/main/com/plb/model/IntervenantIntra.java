package com.plb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.plb.model.intervenant.Intervenant;

@Entity
@Table(name="intervenant_intra")

public class IntervenantIntra implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1822755249534344360L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne
	private Intervenant intervenant;
	
	@ManyToOne
	private ProspectFormation prospectFormation;
	
	@ManyToOne
	private IntraReferenceSpe intraReferenceSpe;
	
	@Column(name = "note")
	private String note;
	
	@Column(name = "tarif")
	private String tarif;
	
	@Column(name = "favori")
	private Integer favori;
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Intervenant getIntervenant() {
		return intervenant;
	}

	public void setIntervenant(Intervenant intervenant) {
		this.intervenant = intervenant;
	}

	public ProspectFormation getProspectFormation() {
		return prospectFormation;
	}

	public void setProspectFormation(ProspectFormation prospectFormation) {
		this.prospectFormation = prospectFormation;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTarif() {
		return tarif;
	}

	public void setTarif(String tarif) {
		this.tarif = tarif;
	}

	public Integer getFavori() {
		return favori;
	}

	public void setFavori(Integer favori) {
		this.favori = favori;
	}

	public IntraReferenceSpe getIntraReferenceSpe() {
		return intraReferenceSpe;
	}

	public void setIntraReferenceSpe(IntraReferenceSpe intraReferenceSpe) {
		this.intraReferenceSpe = intraReferenceSpe;
	}

}
