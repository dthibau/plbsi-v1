package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationArchive")
public class FormationArchiveEvent extends Event {

	public FormationArchiveEvent() {
		super();
	}
	public FormationArchiveEvent(Account account, Formation formation) {
		super(account,formation, "Archivage de la formation " + formation.getLibelle());
	}

	
}
