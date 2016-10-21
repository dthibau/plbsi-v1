package com.plb.si.service.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.FormationFiliere;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.service.FormationDao;

@Name("formationRest")
@Scope(ScopeType.EVENT)
public class FormationRest implements Serializable {

	@In
	FacesContext facesContext;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;


	@In
	EntityManager entityManager;
	
	@RequestParameter
	String idFiliere;
	
	@RequestParameter
	String idIntervenant;
	
	@RequestParameter
	String query;
	
	@RequestParameter
	String selectDate;

	public void getByFiliere() throws JsonGenerationException, JsonMappingException, IOException {
		FormationDao formationDao = new FormationDao(entityManager);
		Filiere selectedFilere = new Filiere();
		selectedFilere.setId(Integer.parseInt(idFiliere));
		Date fromDate = RestUtil.getDate(selectDate);
		List<FormationFiliere> formationsFiliere = null;
		if ( fromDate != null ) {
			formationsFiliere = formationDao.findByFiliereFrom(selectedFilere,fromDate);
		} else {
			formationsFiliere = formationDao.findByFiliere(selectedFilere);
		}
		Collections.sort(formationsFiliere);
		int base = formationsFiliere.size()/4;
		int remain= formationsFiliere.size()%4;
		List<List<FormationJson>> ret = new ArrayList<List<FormationJson>>(4);
		ret.add(new ArrayList<FormationJson>(base+1));
		ret.add(new ArrayList<FormationJson>(base+1));
		ret.add(new ArrayList<FormationJson>(base+1));
		ret.add(new ArrayList<FormationJson>(base+1));
		int length = base + (remain > 0 ? 1 : 0);
		int i=0;
		for ( ; i<length; i++) {
			ret.get(0).add(new FormationJson(formationsFiliere.get(i).getFormation()));
		}
		length += base + (remain > 1 ? 1 : 0);
		for ( ; i<length; i++) {
			ret.get(1).add(new FormationJson(formationsFiliere.get(i).getFormation()));
		}
		length += base + (remain > 2 ? 1 : 0);
		for ( ; i<length; i++) {
			ret.get(2).add(new FormationJson(formationsFiliere.get(i).getFormation()));
		}
		length += base;
		for ( ; i<length; i++) {
			ret.get(3).add(new FormationJson(formationsFiliere.get(i).getFormation()));
		}
		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		ObjectMapper mapper = new ObjectMapper();
		resp.setContentType(RestUtil.JSON_TYPE);

		resp.getWriter().write(mapper.writeValueAsString(ret));
		facesContext.responseComplete();
	}
//	public void getNewFormationsFiliere() throws JsonGenerationException, JsonMappingException, IOException {
//		Intervenant intervenant = entityManager.find(Intervenant.class, Integer.parseInt(idIntervenant));
//		List<Formation> formations = _getNewFormations(intervenant);
//		// Filtre par filière
//		if ( idFiliere != null ) {
//			int id = Integer.parseInt(idFiliere);
//			List<Formation> filteredFormations = new ArrayList<Formation>();
//			for ( Formation formation : formations ) {
//				if ( formation.getFilierePrincipale().getId() == id ) {
//					filteredFormations.add(formation);
//				}
//			}
//			formations = filteredFormations;
//		}
//
//		Collections.sort(formations);
//		int base = formations.size()/4;
//		int remain= formations.size()%4;
//		List<List<FormationJson>> ret = new ArrayList<List<FormationJson>>(4);
//		ret.add(new ArrayList<FormationJson>(base+1));
//		ret.add(new ArrayList<FormationJson>(base+1));
//		ret.add(new ArrayList<FormationJson>(base+1));
//		ret.add(new ArrayList<FormationJson>(base+1));
//		int length = base + (remain > 0 ? 1 : 0);
//		int i=0;
//		for ( ; i<length; i++) {
//			ret.get(0).add(new FormationJson(formations.get(i)));
//		}
//		length += base + (remain > 1 ? 1 : 0);
//		for ( ; i<length; i++) {
//			ret.get(1).add(new FormationJson(formations.get(i)));
//		}
//		length += base + (remain > 2 ? 1 : 0);
//		for ( ; i<length; i++) {
//			ret.get(2).add(new FormationJson(formations.get(i)));
//		}
//		length += base;
//		for ( ; i<length; i++) {
//			ret.get(3).add(new FormationJson(formations.get(i)));
//		}
//		HttpServletResponse resp = (HttpServletResponse) facesContext
//				.getExternalContext().getResponse();
//		ObjectMapper mapper = new ObjectMapper();
//		resp.setContentType("application/json");
//
//		resp.getWriter().write(mapper.writeValueAsString(ret));
//		facesContext.responseComplete();
//	}

	public void getByKeyword() throws JsonGenerationException, JsonMappingException, IOException {
		FormationDao formationDao = new FormationDao(entityManager);
		Date fromDate = RestUtil.getDate(selectDate);
		List<Formation> formations ;
		if ( fromDate != null ) {
			formations = formationDao.findAllFrom(fromDate);
		} else {
			formations = formationDao.findAll();
		}
		
		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		ObjectMapper mapper = new ObjectMapper();
		resp.setContentType(RestUtil.JSON_TYPE);
		List<FormationJson> temp = new ArrayList<FormationJson>();
		for ( Formation formation : formations ) {
			if ( formation.getLibelle().toLowerCase().contains(query.toLowerCase()) ) {
				temp.add(new FormationJson(formation));
			}
		}
		int base = temp.size()/4;
		int remain= temp.size()%4;
		List<List<FormationJson>> ret = new ArrayList<List<FormationJson>>(4);
		ret.add(new ArrayList<FormationJson>(base+1));
		ret.add(new ArrayList<FormationJson>(base+1));
		ret.add(new ArrayList<FormationJson>(base+1));
		ret.add(new ArrayList<FormationJson>(base+1));
		int length = base + (remain > 0 ? 1 : 0);
		int i=0;
		for ( ; i<length; i++) {
			ret.get(0).add(temp.get(i));
		}
		length += base + (remain > 1 ? 1 : 0);
		for ( ; i<length; i++) {
			ret.get(1).add(temp.get(i));
		}
		length += base + (remain > 2 ? 1 : 0);
		for ( ; i<length; i++) {
			ret.get(2).add(temp.get(i));
		}
		length += base;
		for ( ; i<length; i++) {
			ret.get(3).add(temp.get(i));
		}
		resp.getWriter().write(mapper.writeValueAsString(ret));
		facesContext.responseComplete();
	}
	
//	private List<Formation> _getNewFormations(Intervenant intervenant) {
//		FormationDao formationDao = new FormationDao(entityManager);
//		List<Formation> newFormations = null;
//		if (intervenant.getGrilleCompetence().getFilledDate() != null) { // Update newFilieres
//														// and Formations
//			newFormations = formationDao.findAllFrom(intervenant.getGrilleCompetence().getFilledDate());
//
//		} else {
//			newFormations = new ArrayList<Formation>();
//		
//		}
//		return newFormations;
//	}
}