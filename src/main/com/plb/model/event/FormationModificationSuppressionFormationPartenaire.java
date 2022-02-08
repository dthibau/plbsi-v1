package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationSuppressionPartenaire")
public class FormationModificationSuppressionFormationPartenaire extends FormationModificationEvent {

	public FormationModificationSuppressionFormationPartenaire() {
		super();
	}

	public FormationModificationSuppressionFormationPartenaire(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
