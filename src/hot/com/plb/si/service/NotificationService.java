package com.plb.si.service;

import java.util.List;

import org.jboss.seam.annotations.async.Duration;

import com.plb.model.Formation;
import com.plb.model.InformationIntra;
import com.plb.model.Prospect;
import com.plb.model.devis.Email;
import com.plb.model.directory.Account;
import com.plb.model.directory.Role;
import com.plb.model.event.Event;
import com.plb.model.event.NotificationIntervenantsEvent;
import com.plb.model.intervenant.Intervenant;

public interface NotificationService {

	public List<Account> resolveDestinataires(Account actor, Event event);
	
	public List<Account> resolveDestinatairesProspect(Account newActor, Account lastActor, Event event);
	
	public List<Account> resolveDestinataires(Role role, Event event);
	
	public void sendMail( @Duration long waitingTime, Formation formation, Event event);
	
	public void sendMailProspect( @Duration long waitingTime, Prospect prospect, Event event);
	
	public void sendMailIntra(long waitingTime, InformationIntra intra, Event event);
	
	public void sendMailIntra( @Duration long waitingTime, InformationIntra intra, Event event, boolean confirmation);
	
//	public void sendMailIntraValidation( @Duration long waitingTime, InformationIntra intra, Event event);
//	
//	public void sendMailIntraCreation( @Duration long waitingTime, InformationIntra intra, Event event);

	public void sendMailIntervenant( @Duration long waitingTime, NotificationIntervenantsEvent event);
	
	public void sendUpdateTarif(Intervenant intervenant, int oldTarif);

	public List<Account> resolveDestinataireUnique(Account acteur, Event event);
	
	public void sendDevis(Email email, Account sender);

}
