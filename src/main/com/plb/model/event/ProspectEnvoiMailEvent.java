package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.plb.model.Prospect;
import com.plb.model.devis.Email;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("ProspectEnvoiMail")
public class ProspectEnvoiMailEvent extends Event {
	
	@OneToOne
	private Email email;
	
	public ProspectEnvoiMailEvent() {
		super();
	}
	public ProspectEnvoiMailEvent(Account account, Prospect prospect, Email email) {
		super(account,prospect, email.getBody());
		this.email = email;
	}
	public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}

	
}
