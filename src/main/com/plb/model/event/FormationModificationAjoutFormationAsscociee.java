package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationAjoutAssociee")
public class FormationModificationAjoutFormationAsscociee extends FormationModificationEvent {

	public FormationModificationAjoutFormationAsscociee() {
		super();
	}

	public FormationModificationAjoutFormationAsscociee(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
