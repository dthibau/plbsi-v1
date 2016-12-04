package com.plb.dto;

public class TarifInterDto implements Comparable<TarifInterDto>{

	private String codeTarif;
	private Float[] prix = new Float[5];
	
	public TarifInterDto(String codeTarif) {
		this.codeTarif = codeTarif;
	}
	public String getCodeTarif() {
		return codeTarif;
	}
	public void setCodeTarif(String codeTarif) {
		this.codeTarif = codeTarif;
	}
	public Float[] getPrix() {
		return prix;
	}
	public void setPrix(Float[] prix) {
		this.prix = prix;
	}
	public void setPrix(int index, Float prix) {
		this.prix[index] = prix;
	}
	@Override
	public int compareTo(TarifInterDto o) {
		
		return codeTarif.compareTo(o.getCodeTarif());
	}
	@Override
	public String toString() {
		return codeTarif;
	}
	
	
	
}
