package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationSuppression")
public class FormationSuppressionEvent extends Event {

	public FormationSuppressionEvent() {
		super();
	}
	public FormationSuppressionEvent(Account account, String libelle) {
		super(account, "Suppression de la formation " + libelle);
	}

	
}
