package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationModificationPartenaire")
public class FormationModificationModificationFormationPartenaire extends FormationModificationEvent {

	public FormationModificationModificationFormationPartenaire() {
		super();
	}

	public FormationModificationModificationFormationPartenaire(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
