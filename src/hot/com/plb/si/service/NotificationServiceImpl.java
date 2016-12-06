package com.plb.si.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Duration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import com.plb.model.Contact;
import com.plb.model.Formation;
import com.plb.model.InformationIntra;
import com.plb.model.Prospect;
import com.plb.model.devis.Email;
import com.plb.model.directory.Account;
import com.plb.model.directory.Role;
import com.plb.model.event.Event;
import com.plb.model.event.FormationCommentEvent;
import com.plb.model.event.FormationModificationEvent;
import com.plb.model.event.FormationSessionEvent;
import com.plb.model.event.IntraCreationEvent;
import com.plb.model.event.IntraModificationEvent;
import com.plb.model.event.IntraNoteEvent;
import com.plb.model.event.IntraValidationEvent;
import com.plb.model.event.NotificationIntervenantsEvent;
import com.plb.model.event.ProspectEnvoiMailEvent;
import com.plb.model.event.ProspectModificationEvent;
import com.plb.model.event.TarifUpdatedEvent;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.manager.ApplicationManager;

@Name("notificationService")
@Scope(ScopeType.CONVERSATION)
public class NotificationServiceImpl implements NotificationService,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2285506329089580634L;

	@In
	EntityManager entityManager;

	@In
	ApplicationManager applicationManager;

	@In(create = true)
	private Renderer renderer;

	@Logger
	Log log;

	@Override
	public List<Account> resolveDestinataires(Account acteur, Event event) {
		List<Account> destinataires = new ArrayList<Account>();
		if (event instanceof FormationCommentEvent) {
			AccountDao accountDao = new AccountDao(entityManager);
			destinataires = accountDao.findByRole(Role.MANAGER);
		}
		if (event instanceof FormationModificationEvent
				&& acteur.isOnlyCommercial() ) {
			AccountDao accountDao = new AccountDao(entityManager);
			destinataires = accountDao.findByRole(Role.MANAGER);
		}
		if (event instanceof FormationSessionEvent && acteur.isOnlyCommercial()) {
			AccountDao accountDao = new AccountDao(entityManager);
			destinataires = accountDao.findByRole(Role.MANAGER);
		}
		if (event instanceof TarifUpdatedEvent) {
			AccountDao accountDao = new AccountDao(entityManager);
			destinataires = accountDao.findByRole(Role.MANAGER);
		}
		if (event instanceof IntraNoteEvent) {
			AccountDao accountDao = new AccountDao(entityManager);
			destinataires = accountDao.findByRole(Role.INTERVENANTS_MANAGER);
			if ( ((IntraNoteEvent)event).getIntra().getCommercial() != null ) {
				destinataires.add(((IntraNoteEvent)event).getIntra().getCommercial());
			}
		}
		if (acteur != null && destinataires.contains(acteur)) {
			destinataires.remove(acteur);
		}
		event.setDestinataires(destinataires);

		return destinataires;

	}
	
	//Permet l'envoi de mail a un unique destinataire (Fonction Intra)
	@Override
	public List<Account> resolveDestinataireUnique(Account acteur, Event event) {
		List<Account> destinataires = new ArrayList<Account>();
		if ( acteur != null ) {
			destinataires.add(acteur);
		}
		event.setDestinataires(destinataires);
		return destinataires;
	}
	
	//Permet la notification au role Intervenant_Manager
	@Override
	public List<Account> sendToIntervenantManager(Event event) {
		List<Account> destinataires = new ArrayList<Account>();
		AccountDao accountDao = new AccountDao(entityManager);
		//Ne fonctionne plus car plus de role intervenant manager en bdd
		//destinataires = accountDao.findByRole(Role.INTERVENANTS_MANAGER);
		//Parametrage manuel des concernés Laurent Aurore
		destinataires.add(accountDao.findNames("DERUY", "Aurore"));
		destinataires.add(accountDao.findNames("BOURQUARD", "Laurent"));
		event.setDestinataires(destinataires);
		return destinataires;
	}

	@Asynchronous
	@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
	public void sendMail(@Duration long waitingTime, Formation formation,
			Event event) {

		if (ApplicationManager.MAIL_ENABLED && Renderer.instance() != null) {
			Contexts.getEventContext()
					.set("server", ApplicationManager.SERVEUR);
			Contexts.getEventContext().set("event", event);
			Contexts.getEventContext().set("formation", formation);

			for (Account account : event.getDestinataires()) {
				if (!account.equals(event.getAccount())) {
					_sendContact(account, event, "/mail/notification.xhtml");
				}
			}

		}

	}

	private void _sendContact(Contact contact, Event event, String template) {
		Contexts.getEventContext().set("receiver", contact);

		try {
			Renderer.instance().render(template);
			log.info("Une notification a ete envoye a " + contact + "("
					+ contact.getEmail() + ") event :"
					+ event);
			Thread.sleep(1000);
		} catch (Exception e) {
			log.error("Unable to send mail to " + contact + "(" + contact.getEmail() + ") event " + event);
		}
	}

	@Override
	public List<Account> resolveDestinatairesProspect(Account newActeur,
			Account lastActeur, Event event) {
		List<Account> destinataires = new ArrayList<Account>();
		if (lastActeur == null) {
			destinataires.add(newActeur);
		} else {
			destinataires.add(newActeur);
			destinataires.add(lastActeur);
		}
		event.setDestinataires(destinataires);
		return destinataires;
	}

	@Asynchronous
	@Transactional(TransactionPropagationType.REQUIRED)
	public void sendMailIntervenant(long waitingTime,
			NotificationIntervenantsEvent event) {
		if (ApplicationManager.MAIL_ENABLED && Renderer.instance() != null) {
			Contexts.getEventContext()
					.set("server", ApplicationManager.SERVEUR);
			Contexts.getEventContext().set("event", event);

			List<String> alreadySent = new ArrayList<String>();
			for (Intervenant intervenant : event.getIntervenants()) {
				if (intervenant.getEmail() != null
						&& intervenant.getEmail().length() > 0) {
					// Gare aux doublons dans la base
					if (!alreadySent.contains(intervenant.getEmail())) {
						_sendContact(intervenant, event,
								"/mail/notificationIntervenant.xhtml");
					}
				}
			}
			applicationManager.setGrillesUpdateDate(new Date());

		}

	}

	@Asynchronous
	@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
	public void sendMailProspect(@Duration long waitingTime, Prospect prospect,
			Event event) {
		if (ApplicationManager.MAIL_ENABLED && Renderer.instance() != null) {
			Contexts.getEventContext()
					.set("server", ApplicationManager.SERVEUR);
			Contexts.getEventContext().set("event", event);
			Contexts.getEventContext().set("prospect", prospect);
			// On gere le bonne acheminemnt des mails
			if (event.getDestinataires().size() == 1) {
				_sendAccount(event.getDestinataires().get(0), event,"/mail/notificationProspect.xhtml");
			} else if (event.getDestinataires().size() == 2) {
				_sendAccount(event.getDestinataires().get(0), event,"/mail/notificationProspect.xhtml");
				_sendAccount(event.getDestinataires().get(1),
						event,"/mail/notificationProspectNonAssocie.xhtml");
			}

		}

	}
	
	public void sendMailIntra(long waitingTime, InformationIntra intra, Event event) {
		
		if (ApplicationManager.MAIL_ENABLED && Renderer.instance() != null) {
			Contexts.getEventContext()
					.set("server", ApplicationManager.SERVEUR);
			Contexts.getEventContext().set("event", event);
			Contexts.getEventContext().set("intra", intra);
			String template = _resolveTemplate(event);
				for (Account account : event.getDestinataires()) {		
					_sendAccount(account, event,template);
					log.debug("*********************************************************---"+account.getNomComplet());
				}
		}		
	}
	
	private String _resolveTemplate(Event event) {
		if ( event instanceof IntraNoteEvent ) {
			return "/mail/notificationIntraNote.xhtml";
		} else if ( event instanceof IntraCreationEvent ) {
			return "/mail/notificationIntraCreation.xhtml";
		} else if ( event instanceof IntraModificationEvent ) {
			return "/mail/notificationIntraModification.xhtml";
		} else if ( event instanceof IntraValidationEvent ) {
			return "/mail/notificationIntraValidation.xhtml";
		}
		return null;
	} 
	@Override
	public void sendMailIntra(long waitingTime, InformationIntra intra,
			Event event, boolean confirmation) {
		if (ApplicationManager.MAIL_ENABLED && Renderer.instance() != null) {
			Contexts.getEventContext()
					.set("server", ApplicationManager.SERVEUR);
			Contexts.getEventContext().set("event", event);
			Contexts.getEventContext().set("intra", intra);
			// On gere le bonne acheminemnt des mails
			if(confirmation){
				for (Account account : event.getDestinataires()) {		
					_sendAccount(account, event,"/mail/notificationIntraConfirmation.xhtml");
					log.debug("*********************************************************---"+account.getNomComplet()+" "+confirmation);
				}
			}
			else{
				for (Account account : event.getDestinataires()) {		
					_sendAccount(account, event,"/mail/notificationIntraModification.xhtml");
					log.debug("*********************************************************---"+account.getNomComplet()+" "+confirmation);
				}
			}
		}
	}
	
	@Asynchronous
	@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
	public void sendUpdateTarif(Intervenant intervenant, int oldTarif) {
		if (ApplicationManager.MAIL_ENABLED && Renderer.instance() != null) {
			Contexts.getEventContext()
					.set("server", ApplicationManager.SERVEUR);
			Contexts.getEventContext().set("intervenant", intervenant);
			Contexts.getEventContext().set("oldTarif", oldTarif);
			List<Account> managers = new AccountDao(entityManager)
					.findByRole(Role.INTERVENANTS_MANAGER);
			for (Account a : managers) {
				Contexts.getEventContext().set("receiver", a);
				try {
					Renderer.instance().render(
							"/mail/notificationUpdateTarif.xhtml");
					log.info("Une notification a ete envoye a " + a + "("
							+ a.getEmail() + ")");
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	private void _sendAccount(Account account, Event event, String template){
		Contexts.getEventContext().set("receiver", account);
		try {
			Renderer.instance().render(template);
			log.info("Une notification a ete envoye a " + account + "("
					+ account.getEmail() + ") event : "
					+ event);
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Asynchronous
	@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
	public void sendDevis(Email email, Account sender) {
		if (ApplicationManager.MAIL_ENABLED && Renderer.instance() != null) {
			Contexts.getEventContext()
					.set("server", ApplicationManager.SERVEUR);
			Contexts.getEventContext()
			.set("email", email);
			Contexts.getEventContext()
			.set("sender", sender);

				try {
					Renderer.instance().render(
							"/mail/envoiDevis.xhtml");
					log.info("Un devis a été envoyé à " + email.getRecipient());
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

}
