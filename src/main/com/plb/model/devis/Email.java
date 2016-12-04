package com.plb.model.devis;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.plb.model.Fichier;

@Entity
@Table(name="email")
public class Email {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String recipient,subject,attention,cc;
	
	@Lob
	private String body;
	
	@ManyToOne
	private EmailTemplate emailTemplate;
	
	@OneToMany(cascade=CascadeType.ALL,orphanRemoval=true)
	private List<Fichier> attachments = new ArrayList<Fichier>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String to) {
		this.recipient = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	@Transient
	public String[] getCcAsArray() {
		return cc.split(",");
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
