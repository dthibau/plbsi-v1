package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationSuppMutualisee")
public class FormationModificationSuppressionFormationMutualisee extends FormationModificationEvent {

	public FormationModificationSuppressionFormationMutualisee() {
		super();
	}

	public FormationModificationSuppressionFormationMutualisee(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
