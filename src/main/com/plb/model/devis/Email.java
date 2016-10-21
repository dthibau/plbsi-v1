package com.plb.model.devis;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.plb.model.Fichier;

@Entity
@Table(name="email")
public class Email {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String to,subject,mail,attention,cc;
	
	private String body;
	
	@ManyToOne
	private EmailTemplate emailTemplate;
	
	@OneToMany
	private List<Fichier> attachments = new ArrayList<Fichier>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<Fichier> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Fichier> attachments) {
		this.attachments = attachments;
	}

	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
	
	
}
