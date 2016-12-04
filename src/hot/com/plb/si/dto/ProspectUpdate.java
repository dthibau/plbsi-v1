package com.plb.si.dto;

import java.util.Date;

import com.plb.model.Prospect;

public class ProspectUpdate {

	private Prospect prospect;
	private Date updateDate;
	
	public ProspectUpdate(Prospect prospect) {
		this.prospect = prospect;
		updateDate = new Date();
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	
}
