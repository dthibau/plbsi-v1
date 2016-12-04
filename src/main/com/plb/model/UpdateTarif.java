package com.plb.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.plb.model.directory.Account;

@Entity
@Table(name="updatetarif")
public class UpdateTarif {

	@Id @GeneratedValue
	private long id;
	@Temporal(TemporalType.DATE)
	private Date date;
	@ManyToOne
	private Account updater;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Account getUpdater() {
		return updater;
	}
	public void setUpdater(Account updater) {
		this.updater = updater;
	}
	
	
}
