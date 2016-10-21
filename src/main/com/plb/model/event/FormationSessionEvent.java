package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationSession")
public class FormationSessionEvent extends Event {

	public FormationSessionEvent() {
		super();
	}
	public FormationSessionEvent(Account account, Formation formation, String message) {
		super(account,formation, message);
	}

	
}
