package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationModification")
public class FormationModificationEvent extends Event {

	public FormationModificationEvent() {
		super();
	}
	public FormationModificationEvent(Account account, Formation formation, String message) {
		super(account,formation, message);
	}

	
}
