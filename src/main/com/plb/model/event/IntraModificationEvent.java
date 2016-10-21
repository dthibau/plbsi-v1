package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.InformationIntra;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("IntraModification")
public class IntraModificationEvent extends Event {
	
	public IntraModificationEvent() {
		super();
	}
	public IntraModificationEvent(Account account, InformationIntra intra) {
		super(account, intra, "Modification du statut de l'intra : "+intra.getStatutIntra(), intra.getStatutIntra());
	}

}
