package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationModificationRemarque")
public class FormationModificationRemarqueEvent extends FormationModificationEvent {

	public FormationModificationRemarqueEvent() {
		super();
	}

	public FormationModificationRemarqueEvent(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
