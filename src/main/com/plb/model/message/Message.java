package com.plb.model.message;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.plb.model.Prospect;
import com.plb.model.directory.Account;

//classe permettant l'archivage de note  relatif a une affaire, une formation ...
@Entity
@Table(name="message")
public class Message implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@Column(name="libelle", columnDefinition="text")
	private String libelle;
	
	//Emetteur du message dans la box
	@ManyToOne
	private Account account;
	
	//Permet de rattacher le message au bon prospect
	@ManyToOne
	private Prospect prospect;
	
	public Message() {
	}
	
	public Message(Date date, String libelle, Account account, Prospect prospect){
		this.date = date;
		this.libelle = libelle;
		this.account = account;
		this.prospect = prospect;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Prospect getProspect() {
		return prospect;
	}

	public void setProspect(Prospect prospect) {
		this.prospect = prospect;
	}
}
