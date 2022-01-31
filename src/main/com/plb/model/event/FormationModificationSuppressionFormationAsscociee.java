package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationSuppAssociee")
public class FormationModificationSuppressionFormationAsscociee extends FormationModificationEvent {

	public FormationModificationSuppressionFormationAsscociee() {
		super();
	}

	public FormationModificationSuppressionFormationAsscociee(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
