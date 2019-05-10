package com.plb.si.dto;

import com.plb.model.Categorie;
import com.plb.model.Formation;
import com.plb.model.FormationFiliere;

public class FormationCategorieDto implements Comparable<FormationCategorieDto>{

	private Formation formation;
	private Categorie categorie;
	private int rang;
	
	public FormationCategorieDto(Formation formation) {
		this.formation = formation;
		this.categorie = formation.getCategorie();
		this.rang = formation.getRangCategorie();
	}
	
	public FormationCategorieDto(FormationFiliere formationFiliere) {
		this.formation = formationFiliere.getFormation();
		this.categorie = formationFiliere.getCategorie();
		this.rang = formationFiliere.getRangFiliere();
	}
	
	public Formation getFormation() {
		return formation;
	}
	public void setFormation(Formation formation) {
		this.formation = formation;
	}
	public Categorie getCategorie() {
		return categorie;
	}
	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}
	public int getRang() {
		return rang;
	}
	public void setRang(int rang) {
		this.rang = rang;
	}

	@Override
	public int compareTo(FormationCategorieDto o) {
		
		return getRang() - o.getRang();
	}
	
	
}
