package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Prospect;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("ProspectSuppression")
public class ProspectSupressionEvent extends Event{

	public ProspectSupressionEvent() {
		super();
	}
	public ProspectSupressionEvent(Account account, Prospect prospect) {
		super(account,prospect, "Suppression d'un prospect");
	}

	
}
