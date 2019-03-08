package com.plb.si.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.plb.dto.FormationDto;
import com.plb.model.Filiere;
import com.plb.si.dto.FormationFiliereComparator;
import com.plb.si.manager.lucene.SearchManager;
import com.plb.si.service.FiliereDao;
import com.plb.si.service.FormationDao;

@Name("calendarManager")
@Scope(ScopeType.CONVERSATION)
public class CalendarManager {

	private int month,year=Calendar.getInstance().get(Calendar.YEAR);
	boolean selection = false;
	

	@In
	SearchManager searchManager;
	
	FiliereDao filiereDao;
	FormationDao formationDao;
	
	@In(create = true)
	Map<String, Float> tarifsInter;
	
	@In
	EntityManager entityManager;
	
	@Logger
	Log log;
	
	private List<Filiere> filieres;
	private List<FormationDto> selectedFormations;
	private Map<Filiere,List<FormationDto>> formationsByFiliere;
	
	@Create
	public void init() {
		log.debug("Creating CalendarManager");
		formationDao = new FormationDao(entityManager);
		filiereDao = new FiliereDao(entityManager);
	}
	
	public List<Filiere> getFilieres() {
		
		
		if ( filieres == null ) {
			log.debug("getFilieres() : Fetch filieres");
			if ( selection ) {
				selectedFormations = searchManager.getResults();
				filieres = _extractFilieres(selectedFormations);
			} else {
				filieres = filiereDao.findAllOrdered();
			}
		}
		return filieres;
	}
	
	public List<FormationDto> getFormations(Filiere filiere) {
		log.debug("Getting Formations for : " + filiere);
		if ( selection ) {
			return formationsByFiliere.get(filiere);
		}
		List<FormationDto> dtos = FormationDto.buildDtos(formationDao.findByFilierePrincipale(filiere), tarifsInter);
		Collections.sort(dtos, new FormationFiliereComparator());
		return dtos;
	}
	
	public String generate() {
		log.debug("Generating calendrier");
		return "/mz/calendrier/calendrier.xhtml";
	}

	
	public List<Date> getMonths() {
		log.debug("Get months ");
		List<Date> months = new ArrayList<Date>();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		for ( int i=0; i<6; i++) {
			months.add(cal.getTime());
			cal.add(Calendar.MONTH,1);
		}
		
		
		return months;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
		filieres = null;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
		filieres = null;
	}

	public boolean isSelection() {
		return selection;
	}

	public void setSelection(boolean selection) {
		this.selection = selection;
		filieres = null;
	}

	private List<Filiere> _extractFilieres(List<FormationDto> formations) {
		List<Filiere> ret = new ArrayList<Filiere>();
		formationsByFiliere = new HashMap<Filiere, List<FormationDto>>();
		for ( FormationDto dto : formations ) {
			if ( !ret.contains(dto.getFormation().getFilierePrincipale()) ) {
				ret.add(dto.getFormation().getFilierePrincipale());
			}
			if ( formationsByFiliere.get(dto.getFormation().getFilierePrincipale()) == null ) {
				formationsByFiliere.put(dto.getFormation().getFilierePrincipale(), new ArrayList<FormationDto>());
			}
			formationsByFiliere.get(dto.getFormation().getFilierePrincipale()).add(dto);
		}
		Collections.sort(ret);
		return ret;
	}
	
	
}
