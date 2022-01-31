package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationModificationStatut")
public class FormationModificationStatutEvent extends FormationModificationEvent {

	public FormationModificationStatutEvent() {
		super();
	}

	public FormationModificationStatutEvent(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
