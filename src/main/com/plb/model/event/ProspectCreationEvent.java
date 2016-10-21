package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.jboss.seam.core.SeamResourceBundle;

import com.plb.model.Prospect;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("ProspectCreation")
public class ProspectCreationEvent extends Event {
			
	public ProspectCreationEvent() {
		super();
	}
	public ProspectCreationEvent(Account account, Prospect prospect) {
		super(account,prospect, SeamResourceBundle.getBundle().getString("prospect.eventModifMessage"));
	}

}
