package com.plb.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.Formation;
import com.plb.model.directory.Account;

@Entity
@DiscriminatorValue("FormationCatPrincipale")
public class FormationModificationCategoriePrincipale extends FormationModificationEvent {

	public FormationModificationCategoriePrincipale() {
		super();
	}

	public FormationModificationCategoriePrincipale(Account account, Formation formation, String message) {
		super(account, formation, message);
	}

}
