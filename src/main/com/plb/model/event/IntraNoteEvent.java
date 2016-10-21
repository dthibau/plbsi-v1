package com.plb.model.event;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.plb.model.InformationIntra;
import com.plb.model.directory.Account;
import com.plb.model.message.Message;

@Entity
@DiscriminatorValue("IntraNote")
public class IntraNoteEvent extends Event {
	
	public IntraNoteEvent() {
		super();
	}
	public IntraNoteEvent(Account account, InformationIntra intra, List<Message> messages) {
		super(account, intra, "", intra.getStatutIntra());
		StringBuffer sbf = new StringBuffer("");
		for ( Message message : messages ) {
			sbf.append(message.getLibelle());
			sbf.append("\n\n");
			
		}
		this.setMessage(sbf.toString());
	}

}
