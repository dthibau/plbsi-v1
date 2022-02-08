package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationAjoutPartenaire")
public class FormationModificationAjoutFormationPartenaire extends FormationModificationEvent {

	public FormationModificationAjoutFormationPartenaire() {
		super();
	}

	public FormationModificationAjoutFormationPartenaire(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
