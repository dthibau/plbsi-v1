package com.plb.si.manager;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.plb.dto.SessionDto;
import com.plb.model.Categorie;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.FormationFiliere;
import com.plb.model.FormationMutualisees;
import com.plb.model.FormationPartenaire;
import com.plb.model.Session;
import com.plb.model.directory.Account;
import com.plb.model.event.Event;
import com.plb.model.event.FormationArchiveEvent;
import com.plb.model.event.FormationCommentEvent;
import com.plb.model.event.FormationCreationEvent;
import com.plb.model.event.FormationModificationEvent;
import com.plb.model.event.FormationSessionEvent;
import com.plb.model.event.FormationSuppressionEvent;
import com.plb.model.event.FormationUnArchiveEvent;
import com.plb.si.dto.FormationCategorieDto;
import com.plb.si.service.EventDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.NotificationService;
import com.plb.si.util.Labels;
import com.plb.si.util.PlbUtil;

@Name("formationManager")
@Scope(ScopeType.CONVERSATION)
public class FormationManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;

	@Out(required = false)
	public Formation formation;

	// new parameter
	@RequestParameter
	String ref;

	@In
	EntityManager entityManager;

	@In
	Account loggedUser;

	@In
	FacesMessages facesMessages;
	
	@In
	FacesContext facesContext;


	@Logger
	Log log;

	FormationDao formationDao;



	@Create
	public void init() {
		log.debug("Creating FormationManager : loggedUser=" + loggedUser);
		formationDao = new FormationDao(entityManager);
	}


	// Fonction qui recupere une formation en fonction de sa référence
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void selectByReference() throws IOException {
		long ts = System.currentTimeMillis();
		log.debug("Select formation : " + ref);
		formation = formationDao.findByReference(ref);
		log.debug("SelectByReference take : " + (ts-System.currentTimeMillis()));
		
		HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
		response.sendRedirect(ApplicationManager.PLBSI_V2 + "offre/formation/"+formation.getIdFormation());
		facesContext.responseComplete();
		// return "/mz/formation/formation.xhtml";
	}

	

}
