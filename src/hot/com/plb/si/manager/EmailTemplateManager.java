package com.plb.si.manager;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.plb.model.devis.EmailTemplate;
import com.plb.model.directory.Account;

@Name("emailTemplateManager")
@Scope(ScopeType.CONVERSATION)
public class EmailTemplateManager {

	@In
	FacesMessages facesMessages;

	@In
	EntityManager entityManager;

	@In
	Account loggedUser;

	@Logger
	Log log;
	
	@RequestParameter
	private String templateId;

	private List<EmailTemplate> templates;
	private EmailTemplate template;

	
	/**
	 * Crée un devis à partir d'une formation
	 * @param formation
	 */
	@Begin(join = true)
	public List<EmailTemplate> getTemplates() {
		if ( templates == null ) {
			templates = entityManager.createQuery("from EmailTemplate").getResultList();
		}
		return templates;
	}

	@Begin(join = true)
	public String prepareNew() {
		template = new EmailTemplate();
		return "/mz/emailTemplates/template.xhtml";
	}

	public void edit() {
		this.template = entityManager.find(EmailTemplate.class, Integer.parseInt(templateId));
	}


	@RaiseEvent(value="emailTemplateUpdated")
	public String save() {
		if ( !entityManager.contains(template) ) {
			entityManager.persist(template);
		}
		facesMessages.addFromResourceBundle(Severity.INFO,
				"ack.save");
		templates = null;
		return "/mz/emailTemplates/templates.xhtml";
	}

	public EmailTemplate getTemplate() {
		return template;
	}

	public void setTemplate(EmailTemplate template) {
		this.template = template;
	}

	
	
}
