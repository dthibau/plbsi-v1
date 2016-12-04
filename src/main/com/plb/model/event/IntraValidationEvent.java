package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.InformationIntra;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("IntraValidation")
public class IntraValidationEvent extends Event {
	
	public IntraValidationEvent() {
		super();
	}
	public IntraValidationEvent(Account account, InformationIntra intra) {
		super(account, intra, "Validation de l'intra", intra.getStatutIntra());
	}

}
