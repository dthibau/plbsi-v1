package com.plb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="intra_reference_spe")

public class IntraReferenceSpe implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6988528373903563220L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idIntraRef;
	
	@ManyToOne
	private InformationIntra informationIntra;
	
	@ManyToOne
	private ReferenceSpe referenceSpe;
	
	@OneToMany(mappedBy="intraReferenceSpe",cascade=CascadeType.ALL)
	List<IntervenantIntra> intervenants = new ArrayList<IntervenantIntra>();
	
	//GETTEURS AND SETTEURS

	public int getIdIntraRef() {
		return idIntraRef;
	}

	public void setIdIntraRef(int idIntraRef) {
		this.idIntraRef = idIntraRef;
	}

	public InformationIntra getInformationIntra() {
		return informationIntra;
	}

	public void setInformationIntra(InformationIntra informationIntra) {
		this.informationIntra = informationIntra;
	}

	public ReferenceSpe getReferenceSpe() {
		return referenceSpe;
	}

	public void setReferenceSpe(ReferenceSpe referenceSpe) {
		this.referenceSpe = referenceSpe;
	}

	public List<IntervenantIntra> getIntervenants() {
		return intervenants;
	}

	public void setIntervenants(List<IntervenantIntra> intervenants) {
		this.intervenants = intervenants;
	}
	
}
