package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationSuppCatSecondaire")
public class FormationModificationSuppressionCategorieSecondaire extends FormationModificationEvent {

	public FormationModificationSuppressionCategorieSecondaire() {
		super();
	}

	public FormationModificationSuppressionCategorieSecondaire(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
