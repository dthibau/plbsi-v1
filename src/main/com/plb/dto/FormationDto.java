package com.plb.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.plb.model.Formation;

public class FormationDto implements Serializable, Comparable<FormationDto>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Formation formation;
	private boolean intra;
	private float tarifInter;
	
	public static List<FormationDto> buildDtos(List<Formation> formations, Map<String, Float> tarifsInter) {
		List<FormationDto> ret = new ArrayList<FormationDto>();
		for (Formation f : formations) {
			ret.add(new FormationDto(f, tarifsInter));
		}
		return ret;

	}
	public FormationDto(Formation formation, Map<String, Float> tarifsInter) {
		this.formation = formation;
		
		if ( formation.getCodeTarifInter() == null || formation.getCodeTarifInter().length() == 0 || formation.getCodeTarifInter().startsWith("E0") ) {
			intra = formation.getPrix() != 0 ? false : true;
			tarifInter = formation.getPrix();
		} else {
			if ( formation.getDuree() < 7 )
				tarifInter = tarifsInter.get(formation.getCodeTarifInter()+formation.getDuree());
		}
	}
	
	public Formation getFormation() {
		return formation;
	}
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	public boolean isIntra() {
		return intra;
	}
	public void setIntra(boolean intra) {
		this.intra = intra;
	}
	public float getTarifInter() {
		return tarifInter;
	}
	public int getTarifInterAsInt() {
		return (int)tarifInter;
	}
	public void setTarifInter(float tarifInter) {
		this.tarifInter = tarifInter;
	}
	public int getHeures() {
		return formation.getDuree()*7;
	}
	public String getGescoffCode() {
		if ( formation.getFilierePrincipale().getLibelle().equals("Développement Personnel") ) {
			return "413";
		} else if ( formation.getFilierePrincipale().getLibelle().equals("Management") ) {
			return "414";
		} else if ( formation.getFilierePrincipale().getLibelle().equals("Relation client") ) {
			return "312";
		} 
		// Pour toutes les autres ....
		return "326";
	}
	
	public int getRangFiliere() {
		return getFormation().getCategorie().getRang()*100000 + getFormation().getRangCategorie();
	}
	@Override
	public int compareTo(FormationDto o) {
		// TODO Auto-generated method stub
		return getFormation().getReference().compareTo(o.getFormation().getReference());
	}
	
}
