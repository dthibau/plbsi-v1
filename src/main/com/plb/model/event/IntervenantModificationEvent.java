package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.directory.Account;
import com.plb.model.intervenant.Intervenant;

@Entity
@DiscriminatorValue("IntervenantModification")
public class IntervenantModificationEvent extends Event {

	public IntervenantModificationEvent() {
		super();
	}
	public IntervenantModificationEvent(Account account, Intervenant intervenant, String message) {
		super(account,intervenant, message);
	}

	
}
