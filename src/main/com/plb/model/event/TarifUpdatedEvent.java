package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("TarifUpdated")
public class TarifUpdatedEvent extends Event {

	public TarifUpdatedEvent() {
		super();
	}
	public TarifUpdatedEvent(Account account, Formation formation, String message) {
		super(account,formation, message);
	}
}
