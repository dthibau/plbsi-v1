package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.jboss.seam.core.SeamResourceBundle;

import com.plb.model.InformationIntra;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("IntraCreation")
public class IntraCreationEvent extends Event {
			
	public IntraCreationEvent() {
		super();
	}
	public IntraCreationEvent(Account account, InformationIntra intra) {
		super(account, intra, SeamResourceBundle.getBundle().getString("intra.eventCreate"), intra.getStatutIntra());
	}

}
