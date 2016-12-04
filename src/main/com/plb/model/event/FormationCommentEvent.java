package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationComment")
public class FormationCommentEvent extends Event {

	public FormationCommentEvent() {
		super();
	}
	public FormationCommentEvent(Account account, Formation formation, String message) {
		super(account, formation, message);
	}

	
}
