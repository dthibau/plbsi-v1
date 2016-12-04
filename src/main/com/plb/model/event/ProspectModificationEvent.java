package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Prospect;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("ProspectModification")
public class ProspectModificationEvent extends Event {
	
	public ProspectModificationEvent() {
		super();
	}
	public ProspectModificationEvent(Account account, Prospect prospect) {
		super(account,prospect, "Modification d'un prospect");
	}

}
