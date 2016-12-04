package com.plb.si.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.plb.model.Session;

@FacesConverter("com.plb.SessionOrganismeConverter")
@Name("sessionOrganismeConverter")
@Scope(ScopeType.CONVERSATION)
public class SessionOrganismeConverter implements Converter {

	EntityManager entityManager;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		
		return entityManager.find(Session.class, Integer.parseInt(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}

}
