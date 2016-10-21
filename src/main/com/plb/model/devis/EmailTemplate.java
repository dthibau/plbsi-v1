package com.plb.model.devis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="email_template")
public class EmailTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String subject;
	
	@Lob
	private String body;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	@Transient
	public String getSummary() {
		return body != null ? (body.length() < 400 ? body : body.substring(0,400) + "...") : "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		EmailTemplate other = (EmailTemplate) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
