package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.InformationIntra;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("IntraCreation")
public class IntraCreationEvent extends Event {
			
	public IntraCreationEvent() {
		super();
	}
	public IntraCreationEvent(Account account, InformationIntra intra) {
		super(account, intra, "Création de la demande d'intra "+intra.getIdInforamtionIntra(), intra.getStatutIntra());
	}

}
