package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationCreation")
public class FormationCreationEvent extends Event {

	public FormationCreationEvent() {
		super();
	}
	public FormationCreationEvent(Account account, Formation formation) {
		super(account,formation, "Création de la formation "+formation.getLibelle());
	}

	
}
