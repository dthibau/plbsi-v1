package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.devis.Devis;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("DevisCreate")
public class DevisCreationEvent extends Event {

	public DevisCreationEvent() {
		super();
	}
	public DevisCreationEvent(Account account, Devis devis) {
		super(account,devis.getFormation(), "Création de devis " + devis + " pour " + devis.getFormation().getLibelle());
	}

	
}
