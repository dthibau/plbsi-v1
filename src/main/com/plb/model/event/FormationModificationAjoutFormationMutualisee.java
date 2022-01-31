package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationAjoutMutualisee")
public class FormationModificationAjoutFormationMutualisee extends FormationModificationEvent {

	public FormationModificationAjoutFormationMutualisee() {
		super();
	}

	public FormationModificationAjoutFormationMutualisee(Account account, Formation formation, String message) {
		super(account, formation, message);
	}


}
