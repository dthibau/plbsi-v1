package com.plb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="tarif_inter")
public class TarifInter {

	@Id
	@Column(name="tar1_code_inter", columnDefinition="char")
	private String code;
	@Column(name="tar1_tarif", columnDefinition="decimal")
	private Float tarif;


	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Transient
	public String getRootCode() {
		return code.substring(0,2);
	}
	@Transient
	public int getDuree() {
		return Integer.parseInt(code.substring(2));
	}
	public Float getTarif() {
		return tarif;
	}
	public void setTarif(Float tarif) {
		this.tarif = tarif;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		TarifInter other = (TarifInter) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	
	
}
