package com.plb.si.manager.intervenant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.plb.model.Fichier;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.event.Event;
import com.plb.model.event.IntervenantModificationEvent;
import com.plb.model.intervenant.Competence;
import com.plb.model.intervenant.GrilleCompetence;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.dto.CompetenceDto;
import com.plb.si.service.FormationDao;
import com.plb.si.service.IntervenantDao;
import com.plb.si.service.NotificationService;
import com.plb.si.util.PlbUtil;

@Name("grilleManager")
@Scope(ScopeType.CONVERSATION)
public class GrilleManager {

	@RequestParameter
	String grille;

	@In
	EntityManager entityManager;

	@Out
	Intervenant intervenant;

	private String activeTab;

	@Logger
	Log log;

	@In(create=true)
	List<Filiere> filieres;

	private int step = 0;

	private Intervenant oldIntervenant;

	@In(create = true)
	NotificationService notificationService;

	private Map<Filiere, List<CompetenceDto>> hshKnownCompetencesRows = new HashMap<Filiere, List<CompetenceDto>>();
	private Map<Filiere, List<CompetenceDto>> hshNewCompetencesRows = new HashMap<Filiere, List<CompetenceDto>>();

	private Map<Filiere, List<Competence>> hshCompetencesAnimees = new HashMap<Filiere, List<Competence>>();

	private List<Formation> newFormations, knownFormations;

	private Map<Filiere, List<Formation>> newFilieres = new HashMap<Filiere, List<Formation>>();
	private Map<Filiere, List<Formation>> knownFilieres = new HashMap<Filiere, List<Formation>>();

	private Map<Filiere, Boolean> expandFiliere = new HashMap<Filiere, Boolean>();

	@Create
	public void init() {
		for (Filiere f : filieres) {
			expandFiliere.put(f, false);
		}
	}

	/**
	 * Accès via intervenant et dmz
	 * 
	 * @return
	 */
	@Begin(join = true)
	public String authenticate() {

		if (grille == null || grille.length() == 0) {
			return "/error.xhtml";
		}
		try {
			intervenant = new IntervenantDao(entityManager).findByUrl(grille);
			_storeOldState();
			log.debug("Accès à la grille par " + intervenant);
		} catch (NoResultException e) {
			return "/error.xhtml";
		}

		_updateKnown(intervenant.getGrilleCompetence());
		return "/dmz/grille.xhtml";

	}

	/**
	 * Accès via IntervenantManager et mz
	 * 
	 * @param intervenant
	 */
	@Begin(join = true)
	public void selectIntervenant(Intervenant intervenant) {
		log.debug("selectIntervenant " + intervenant);
		this.intervenant = intervenant;
		_updateKnown(intervenant.getGrilleCompetence());
		for (Filiere f : newFilieres.keySet()) {
			if (expandFiliere.get(f) == null) {
				expandFiliere.put(f, false);
			}
		}
	}

	@Begin(join = true)
	public void prepareNew(Intervenant intervenant) {
		log.debug("prepareeNew " + intervenant);
		this.intervenant = intervenant;
		_updateKnown(intervenant.getGrilleCompetence());
	}

	public void next() {
		step++;
		activeTab = "tab" + step;
		log.debug("Next step " + intervenant);
	}

	public String update() {
		log.debug("update " + intervenant);
		step = 1;
		refresh();

		return "/dmz/grille.xhtml";
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getActiveTab() {
		if (activeTab == null) {
			activeTab = "tab" + step;
		}
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}

	public List<CompetenceDto> getKnownCompetencesRows(Filiere filiere) {
		if ( filiere == null ) {
			log.error("Calling getKnownCompetencesRows with filiere == null !!");
			return new ArrayList<CompetenceDto>();
		}
		log.debug("getKnownCompetencesRows cache for Filiere " + filiere
				+ " is null ? "
				+ (hshKnownCompetencesRows.get(filiere) == null));
		
		if (hshKnownCompetencesRows.get(filiere) == null) {
			hshKnownCompetencesRows.put(filiere, _getKnownCompetences(filiere));
		}
		return hshKnownCompetencesRows.get(filiere);
	}

	private List<CompetenceDto> _getKnownCompetences(Filiere filiere) {
		List<Formation> knownFormations = knownFilieres.get(filiere);
		List<CompetenceDto> ret = new ArrayList<CompetenceDto>(
				knownFormations.size());
		for (Formation formation : knownFormations) {
			Competence competence = intervenant.getGrilleCompetence()
					.getCompetence(formation);
			if (competence != null) {
				ret.add(new CompetenceDto(competence));
			} else {
				ret.add(new CompetenceDto(formation));
			}
		}

		Collections.sort(ret);
		return ret;
	}

	public List<CompetenceDto> getNewCompetencesRows(Filiere filiere) {
		if ( filiere == null ) {
			log.error("Calling getNewCompetencesRows with filiere == null !!");
			return new ArrayList<CompetenceDto>();
		}
		log.debug("getNewCompetencesRows cache for Filiere " + filiere
				+ " is null ? " + (hshNewCompetencesRows.get(filiere) == null));
		if (hshNewCompetencesRows.get(filiere) == null) {
			hshNewCompetencesRows.put(filiere, _getNewCompetences(filiere));
		}
		return hshNewCompetencesRows.get(filiere);
	}

	private List<CompetenceDto> _getNewCompetences(Filiere filiere) {
		List<Formation> newFormations = newFilieres.get(filiere);
		List<CompetenceDto> ret = new ArrayList<CompetenceDto>(
				newFormations.size());
		for (Formation formation : newFormations) {
			Competence competence = intervenant.getGrilleCompetence()
					.getCompetence(formation);
			if (competence != null) {
				ret.add(new CompetenceDto(competence));
			} else {
				ret.add(new CompetenceDto(formation));
			}
		}

		Collections.sort(ret);
		return ret;
	}

	public void refresh() {
		log.debug("refresh " + intervenant);
		hshCompetencesAnimees = new HashMap<Filiere, List<Competence>>();
		hshKnownCompetencesRows = new HashMap<Filiere, List<CompetenceDto>>();
	}

	public List<Competence> getCompetencesAnimees(Filiere filiere) {
		log.debug("getCompetencesAnimees for filiere " + filiere + " cache is "
				+ hshCompetencesAnimees.get(filiere));
		if (hshCompetencesAnimees.get(filiere) == null) {
			hshCompetencesAnimees.put(filiere, _getCompetencesAnimees(filiere));
		}
		return hshCompetencesAnimees.get(filiere);
	}

	private List<Competence> _getCompetencesAnimees(Filiere filiere) {
		List<Competence> ret = new ArrayList<Competence>();
		if (filiere != null) {
			List<Competence> competences = intervenant.getGrilleCompetence().getCompetences();
			for (Competence competence : competences) {
				Formation formation = entityManager.find(Formation.class,
						competence.getFormation().getIdFormation());
				if (formation.getFilierePrincipale().equals(filiere)) {
					ret.add(competence);
				}
			}
		}
		Collections.sort(ret);
		return ret;
	}

	// public List<Competence> getCompetencesAnimees(int part) {
	//
	// if (competencesAnimees == null) {
	// competencesAnimees = getCompetencesAnimees();
	// }
	// int toIndex = competencesAnimees.size() % 2 == 0 ? competencesAnimees
	// .size() / 2 : (competencesAnimees.size() + 1) / 2;
	//
	// if (part % 2 == 0) {
	// return competencesAnimees.subList(0, toIndex - 1);
	// } else {
	// return competencesAnimees.subList(toIndex,
	// competencesAnimees.size() - 1);
	// }
	// }

	public String terminer() {

		intervenant.getGrilleCompetence().setFilledDate(new Date());
		String modif = "";

		modif += PlbUtil.diffIntervenant(oldIntervenant, intervenant);
		if (modif.length() > 0) {
			Event event = new IntervenantModificationEvent(null, intervenant,
					modif);
			entityManager.persist(event);
			intervenant.setDateMisAJour(new Date());
			if (intervenant.getTarif() != oldIntervenant.getTarif()) {
				notificationService.sendUpdateTarif(intervenant,
						oldIntervenant.getTarif());
			}
			_storeOldState();
		}
		refresh();
		log.debug("Terminer saisie grille par " + intervenant);

		return "/dmz/merci";
	}

	public void listener(FileUploadEvent event) throws Exception {
		log.debug("Uploading CV " + intervenant);
		UploadedFile item = event.getUploadedFile();
		Fichier fichier = new Fichier();
		fichier.setLength(item.getData().length);
		fichier.setName(item.getName());
		fichier.setData(item.getData());
		fichier.setContentType(item.getContentType());
		intervenant.setCv(fichier);
	}

	private void _updateKnown(GrilleCompetence grilleCompetence) {
		FormationDao formationDao = new FormationDao(entityManager);
		if (grilleCompetence.getFilledDate() != null) { // Update newFilieres
														// and Formations
			newFormations = formationDao.findAllFrom(grilleCompetence
					.getFilledDate());
			_fillMap(newFormations, newFilieres);
			knownFormations = formationDao.findAllBefore(grilleCompetence
					.getFilledDate());
			_fillMap(knownFormations, knownFilieres);

		} else {
			newFormations = new ArrayList<Formation>();
			knownFormations = formationDao.findAll();
			_fillMap(knownFormations, knownFilieres);
		}

	}

	private void _fillMap(List<Formation> formations,
			Map<Filiere, List<Formation>> map) {
		for (Formation formation : formations) {
			if (map.get(formation.getFilierePrincipale()) == null) {
				map.put(formation.getFilierePrincipale(),
						new ArrayList<Formation>());
			}
			map.get(formation.getFilierePrincipale()).add(formation);
		}
	}

	private void _storeOldState() {
		oldIntervenant = intervenant.getCopy();
	}

	public List<Filiere> getNewFilieres() {
		log.debug("getNewFilieres " + intervenant);
		List<Filiere> ret = new ArrayList<Filiere>();
		for (Filiere f : newFilieres.keySet()) {
			ret.add(f);
		}
		return ret;
	}

	public void toggleFilierre(CompetenceDto c) {
		if ( c.getChecked() ) {
			intervenant.getGrilleCompetence().addCompetence(c.getFormation());		
		} else {
			intervenant.getGrilleCompetence().removeCompetence(c.getFormation());	
		}
		Filiere filiere = c.getFormation().getFilierePrincipale();
//		refresh();

		expandFiliere.put(filiere, true);
		log.debug("Toggle " + filiere + "/" + c.getFormation() + " par "
				+ intervenant);

	}
	
	public void updateSupport(CompetenceDto c) {
		intervenant.getGrilleCompetence().getCompetence(c.getFormation()).setSupport(c.getSupport());;		
		log.debug("UpdateSupport /" + c.getFormation() + " / "
				+ intervenant + " / " + c.getSupport());

	}
	public void updateRemarques(CompetenceDto c) {
		intervenant.getGrilleCompetence().getCompetence(c.getFormation()).setRemarques(c.getRemarques());		
		log.debug("UpdateRemarques /" + c.getFormation() + " / "
				+ intervenant + " / " + c.getRemarques());

	}

	public boolean isExpanded(Filiere filiere) {
		// return false;
		return filiere != null && expandFiliere.get(filiere) != null ? expandFiliere
				.get(filiere) : false;

	}

	public void expandAllFilieres() {
		for (Filiere f : expandFiliere.keySet()) {
			expandFiliere.put(f, true);
		}

	}

	public void collapseAllFilieres() {
		for (Filiere f : expandFiliere.keySet()) {
			expandFiliere.put(f, false);
		}

	}

	public boolean isAllExpanded() {
		for (Filiere f : expandFiliere.keySet()) {
			if (!expandFiliere.get(f)) {
				return false;
			}
		}
		return true;
	}

	public void maintainConversation() {
		log.debug("Maintaining conversation for intervenant " + intervenant);
	}
}
