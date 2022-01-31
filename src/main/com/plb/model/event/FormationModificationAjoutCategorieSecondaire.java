package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationAjoutCatSecondaire")
public class FormationModificationAjoutCategorieSecondaire extends FormationModificationEvent {

	public FormationModificationAjoutCategorieSecondaire() {
		super();
	}

	public FormationModificationAjoutCategorieSecondaire(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
