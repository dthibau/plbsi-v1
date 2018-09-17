package com.plb.si.manager.intervenant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.plb.model.event.Event;
import com.plb.model.intervenant.GrilleCompetence;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.service.EventDao;
import com.plb.si.service.IntervenantDao;

@Name("intervenantManager")
@Scope(ScopeType.CONVERSATION)
public class IntervenantManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2112349276429025279L;

	@Out(required = false)
	private Intervenant intervenant;

	@RequestParameter
	String intervenantId;

	@In
	EntityManager entityManager;

	@Logger
	Log log;

	@In
	FacesMessages facesMessages;

	@In(create = true)
	GrilleManager grilleManager;

	@In(create = true)
	SearchIntervenantManager searchIntervenantManager;
	

	private static int VISU_MODE = 0;
	private static int EDIT_MODE = 1;
	private static int NEW_MODE = 2;
	int mode = EDIT_MODE;

	List<Event> historique;

	@Begin(join = true)
	public String prepareNew() {
		intervenant = new Intervenant();
		grilleManager.prepareNew(intervenant);

		return "/mz/intervenant/intervenant.xhtml";
	}

	@Begin(join = true)
	public String selectIntervenant() {
		long start = System.currentTimeMillis();
		intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(intervenantId));
		

		if (intervenant.getGrilleCompetence() == null) { // Backward
															// compatibility,
															// dans la base les
															// intervenants
															// n'ont pas tous
															// une grille de
															// compétences
			GrilleCompetence grilleCompetence = new GrilleCompetence(
					intervenant);
			grilleCompetence.setIntervenant(intervenant);
			intervenant.setGrilleCompetence(grilleCompetence);
			entityManager.persist(grilleCompetence);
		}
		log.info(entityManager.contains(intervenant));
		grilleManager.selectIntervenant(intervenant);
		log.info("Loading one Intervenants took "+(System.currentTimeMillis()-start)+ "ms");
		mode = VISU_MODE;
		return "/mz/intervenant/intervenant.xhtml";
	}

	public List<Event> getHistorique() {
 		if (historique == null) {
			if (entityManager.contains(intervenant))
				try  {
				historique = new EventDao(entityManager).findAll(intervenant);
				} catch (Exception e) {
					log.error("Error retriving historique for " + intervenant + " - " + e);
				}
			else {
				historique = new ArrayList<Event>();
			}
		}
		return historique;
	}

	public String saveIntervenant() {
		if (intervenant.getId() != 0) {
			intervenant = entityManager.merge(intervenant);
			facesMessages.addFromResourceBundle(Severity.INFO,
					"intervenant.modif", intervenant.toString());
			intervenant.setDateMisAJour(new Date());
		} else {
			entityManager.persist(intervenant);
			facesMessages.addFromResourceBundle(Severity.INFO,
					"intervenant.save", intervenant.toString());
			intervenant.setDateMisAJour(new Date());
		}
		mode = VISU_MODE;
		grilleManager.refresh();
		searchIntervenantManager.search();
		return "/mz/intervenant/searchIntervenant.xhtml";
	}

	public void setEditMode() {
		mode = EDIT_MODE;
	}

	public void setVisuMode() {
		mode = VISU_MODE;
	}

	public boolean isVisuMode() {
		return mode == VISU_MODE;
	}

	public boolean isEditMode() {
		return mode == EDIT_MODE;
	}

	public boolean isCreationMode() {
		return mode == NEW_MODE;
	}

	@End
	public String suppIntervenant() {
		intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(intervenantId));
		if (intervenant != null) {
			new IntervenantDao(entityManager).remove(intervenant);

			facesMessages.addFromResourceBundle(Severity.INFO,
					"intervenant.supp", intervenant.toString());
		}
		return "/mz/intervenant/searchIntervenant.xhtml";
	}

	public void maintainConversation() {
		log.debug("Maintaining conversation for intervenant " + intervenant);
	}
}
