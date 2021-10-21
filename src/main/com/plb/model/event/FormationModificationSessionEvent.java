package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationModificationSession")
public class FormationModificationSessionEvent extends FormationModificationEvent {

	public FormationModificationSessionEvent() {
		super();
	}
	public FormationModificationSessionEvent(Account account, Formation formation, String message) {
		super(account, formation, message);
	}
}
