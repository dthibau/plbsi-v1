package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationUnarchive")
public class FormationUnArchiveEvent extends Event {

	public FormationUnArchiveEvent() {
		super();
	}
	public FormationUnArchiveEvent(Account account, Formation formation) {
		super(account,formation, "Dés-Archivage de la formation " + formation.getLibelle());
	}

	
}
