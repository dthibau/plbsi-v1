package com.plb.si.service.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

import com.plb.model.Formation;
import com.plb.model.intervenant.Competence;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.service.CompetenceDao;
import com.plb.si.service.FormationDao;
import com.plb.si.util.Labels;

@Name("competenceRest")
@Scope(ScopeType.EVENT)
public class CompetenceRest implements Serializable {

	@In
	FacesContext facesContext;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;

	@In
	EntityManager entityManager;

	@RequestParameter
	String idIntervenant;
	@RequestParameter
	String idFormation;
	@RequestParameter
	String selectDate = RestUtil.ALL;

	@RequestParameter
	String selectedFormations;

	@RequestParameter
	String idCompetence;

	

	public void getCompetences() throws JsonGenerationException,
			JsonMappingException, IOException {

		Intervenant intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(idIntervenant));

		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		ObjectMapper mapper = new ObjectMapper();
		resp.setContentType(RestUtil.JSON_TYPE);
		List<Competence> competences = _getNewCompetences(intervenant,
				selectDate);
		List<CompetenceJson> ret = new ArrayList<CompetenceJson>(
				competences.size());
		for (Competence competence : competences) {
			ret.add(new CompetenceJson(competence, intervenant.getId()));
		}
		resp.getWriter().write(mapper.writeValueAsString(ret));
		facesContext.responseComplete();
	}

	public void addCompetences() throws IOException {
		Intervenant intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(idIntervenant));

		boolean updated = true;
		String[] sFormations = selectedFormations.split(",");
		for (String sFormation : sFormations) {
			Formation formation = entityManager.find(Formation.class,
					Integer.parseInt(sFormation));
			if (intervenant.getGrilleCompetence().getCompetence(formation) == null) {
				intervenant.getGrilleCompetence().addCompetence(formation);
				updated = true;
			}
		}
		if ( updated ) {
			intervenant.getGrilleCompetence().setFilledDate(new Date());
		}
		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		resp.setContentType("application/text");
		resp.getWriter().write(Labels.getString("competences.updated"));
		facesContext.responseComplete();
	}

	public void saveOrUpdate() throws IOException {
		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();

		CompetenceJson competenceJson = RestUtil.parsePost(request,
				CompetenceJson.class);
		Competence competence = null;
		if (competenceJson.getId() > 0) {
			competence = entityManager.find(Competence.class,
					competenceJson.getId());
			if (competence != null) { // Mise à jour
				competence.setSupport(competenceJson.isSupport());
				competence.setRemarques(competenceJson.getRemarques());
				competenceJson.setMessage(Labels
						.getString("competences.updated"));
				Intervenant intervenant = entityManager.find(Intervenant.class,
						competence.getIntervenant().getId());
				intervenant.getGrilleCompetence().setFilledDate(new Date());
			}
		} else if (competenceJson.getIdFormation() > 0
				&& competenceJson.getIdIntervenant() > 0) {
			Formation formation = entityManager.find(Formation.class,
					competenceJson.getIdFormation());
			Intervenant intervenant = entityManager.find(Intervenant.class,
					competenceJson.getIdIntervenant());
			try {
				competence = new CompetenceDao(entityManager)
						.findByFormationAndIntervenant(formation, intervenant);
				competence.setSupport(competenceJson.isSupport());
				competence.setRemarques(competenceJson.getRemarques());
				competenceJson.setMessage(Labels
						.getString("competences.updated"));
			} catch (NoResultException e) { // insertion
				competence = new Competence();
				competence.setGrilleCompetence(intervenant
						.getGrilleCompetence());
				competence.setFormation(formation);
				competence.setSupport(competenceJson.isSupport());
				competence.setRemarques(competenceJson.getRemarques());
				entityManager.persist(competence);
				competenceJson
						.setMessage(Labels.getString("competences.added"));
			}
		}
		competenceJson.setId(competence.getId());

		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();

		ObjectMapper mapper = new ObjectMapper();
		resp.setContentType(RestUtil.JSON_TYPE);
		resp.getWriter().write(mapper.writeValueAsString(competenceJson));
		facesContext.responseComplete();
	}

	public void removeCompetence() throws IOException {
		Competence competence = entityManager.find(Competence.class,
				Long.parseLong(idCompetence));
		Intervenant intervenant = entityManager.find(Intervenant.class,
				competence.getIntervenant().getId());
		intervenant.getGrilleCompetence().setFilledDate(new Date());
		entityManager.remove(competence);

		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		resp.setContentType("application/text");
		resp.getWriter().write(Labels.getString("competences.removed"));
		facesContext.responseComplete();
	}

	public void removeCompetenceByFormation() throws IOException {
		Intervenant intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(idIntervenant));
		Formation formation = entityManager.find(Formation.class,
				Integer.parseInt(idFormation));
		CompetenceDao cDao = new CompetenceDao(entityManager);
		Competence competence = cDao.findByFormationAndIntervenant(formation,
				intervenant);

		entityManager.remove(competence);

		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		resp.setContentType("application/text");
		resp.getWriter().write(Labels.getString("competences.removed"));
		facesContext.responseComplete();
	}

	private List<Competence> _getNewCompetences(Intervenant intervenant,
			String selectDate) {
		FormationDao formationDao = new FormationDao(entityManager);
		Date fromDate = RestUtil.getDate(selectDate);
		List<Formation> formations = null;
		List<Competence> ret = null;
		if (fromDate != null) {
			formations = formationDao.findAllFrom(fromDate);
			ret = new ArrayList<Competence>(formations.size());
			for (Formation formation : formations) {
				Competence competence = intervenant.getGrilleCompetence()
						.getCompetence(formation);
				if (competence != null) {
					ret.add(competence);
				}
			}
		} else {
			ret = intervenant.getGrilleCompetence()
					.getCompetences();	
		}
		
		return ret;
	}
}