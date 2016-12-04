package com.plb.si.manager.intervenant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;

import org.apache.lucene.queryParser.ParseException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.component.SortOrder;

import com.plb.model.Categorie;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.RangCategorieComparator;
import com.plb.model.RangFiliereComparator;
import com.plb.model.intervenant.Competence;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.dto.FormationCompetencesDto;
import com.plb.si.service.CompetenceDao;
import com.plb.si.service.FormationDao;
import com.plb.si.service.IntervenantDao;
import com.plb.si.service.SearchFormation;
import com.plb.si.util.PlbUtil;


@Name("searchFormationManager")
@Scope(ScopeType.SESSION)
public class SearchFormationManager implements Serializable {

	private static final long serialVersionUID = -209923931683152793L;

	public static int pageSize = 20;

	private String searchString;

	List<Intervenant> intervenantResults;
	List<Intervenant> queryResults;

	List<FormationCompetencesDto> dtos;
	List<Formation> formationResults;
	List<Formation> formationQueryResults;
	private Filiere filiere;
	private Categorie categorie;
	private boolean moreResults = false;
	private boolean showEmptyFormation = true;
	boolean needFilter = true;

	private Map<String, SortOrder> orders = new HashMap<String, SortOrder>();

	@In(create = true)
	private List<Categorie> categories;

	@SuppressWarnings("rawtypes")
	public static final Class[] INDEXED_CLASSES = new Class[] { Intervenant.class, };

	String[] fields = { "nom", "prenom", "rang", "observations", "tarif" };

	@In
	FullTextEntityManager entityManager;

	IntervenantDao intervenantDao;

	private boolean needPerformQuery = false;
	
	

	/*
	 * @In EntityManager entityManager;
	 */

	@In
	FacesMessages facesMessages;


	@Create
	@Begin(join = true)
	public void init() {

		intervenantDao = new IntervenantDao(entityManager);
		orders.put("nomComplet", SortOrder.unsorted);
		orders.put("email", SortOrder.unsorted);
		orders.put("rang", SortOrder.unsorted);
		orders.put("tarif", SortOrder.unsorted);
		orders.put("dateMisAJour", SortOrder.unsorted);

		// Résultat vide par défaut
		// needPerformQuery = false;
		// needFilter = false;
		// dtos = new ArrayList<FormationCompetencesDto>();
	}


	@Begin(join = true)
	public List<FormationCompetencesDto> getFormationResults() {

		if (needPerformQuery) {
			entityManager.clear();
			try {
				formationQueryResults = _performFormationQuery();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				formationQueryResults = new ArrayList<Formation>();
				facesMessages.add(Severity.ERROR,
						"Impossible de comprendre la requête : "+e);
			}
			needPerformQuery = false;
			needFilter = true;
			formationResults = null;
			dtos = null;
		}
		if (needFilter) {
			formationResults = _filterResults(formationQueryResults);
			if (categorie != null) {
				Collections.sort(formationResults,
						new RangCategorieComparator());
			} else if (filiere != null) {
				Collections.sort(formationResults, new RangFiliereComparator(
						filiere));
			}
			needFilter = false;
			dtos = null;
		}
		if (dtos == null) {
			dtos = _findIntervenantsByFormations(formationResults);
			// if (firstAccess) { // Tri par statut descendant
			// sortByStatut();
			// sortByStatut();
			// firstAccess = false;
			// }
		}
		return dtos;

	}

	@Begin(join = true)
	public int getTotal() {

			getFormationResults();

			Set<Intervenant> intervenants = new HashSet<Intervenant>();
			for (FormationCompetencesDto dto : dtos) {
				for (Competence competence : dto.getCompetences()) {
					if (!intervenants.contains(competence.getIntervenant())) {
						intervenants.add(competence.getIntervenant());
					}
				}
			}
			return intervenants.size();
		

	}

	public void search() {
		 needPerformQuery = true;
	}

	public void filter() {
		needFilter = true;
		// Traitement de la première recherche
		if (formationQueryResults == null) {
			search();
		}
	}

	public void filter(ValueChangeEvent evt) {
		needFilter = true;
	}

	public SortOrder getOrder(String key) {
		return orders.get(key);
	}

	public SortOrder setOrder(String key, SortOrder order) {
		return orders.put(key, order);
	}

	public boolean isAscending(String key) {
		return orders.get(key) != null
				&& orders.get(key).equals(SortOrder.ascending);
	}

	public boolean isDescending(String key) {
		return orders.get(key) != null
				&& orders.get(key).equals(SortOrder.descending);
	}

	public void sortBy(String key) {
		for (String k : orders.keySet()) {
			if (!k.equals(key)) {
				orders.put(key, SortOrder.unsorted);
			} else {
				if (orders.get(key).equals(SortOrder.ascending)) {
					orders.put(key, SortOrder.descending);
				} else {
					orders.put(key, SortOrder.ascending);
				}
			}
		}
	}

	public void reset() {
		searchString = null;
		filiere = null;
		categorie = null;
		needPerformQuery = true;
	}
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}


	// /////////////////////////
	// Recherhe formation
	// /////////////////////////
	private List<Formation> _performFormationQuery() throws ParseException {
		if (searchString != null && !"".equals(searchString)) {
			SearchFormation searchFormation = new SearchFormation(entityManager);
			return searchFormation.search(searchString, moreResults);
		} else {
			return new FormationDao(entityManager).findAll();
		}
	}

	private List<Formation> _filterResults(List<Formation> formations) {
		List<Formation> ret = new ArrayList<Formation>();
		if (formations != null) {

			for (Formation f : formations) {

				if (filiere != null
						&& !filiere.equals(f.getFilierePrincipale())) {
					continue;
				}
				if (categorie != null && !f.getCategorie().equals(categorie)) {
					continue;
				}

				ret.add(f);
			}
		}
		return ret;
	}

	private List<FormationCompetencesDto> _findIntervenantsByFormations(
			List<Formation> formationResults) {
		List<FormationCompetencesDto> ret = new ArrayList<FormationCompetencesDto>();
		CompetenceDao competenceDao = new CompetenceDao(entityManager);
		for (Formation formation : formationResults) {
			List<Competence> competences = competenceDao
					.findByFormation(formation);
			ret.add(new FormationCompetencesDto(formation, competences));
		}

		return ret;
	}


	public Filiere getFiliere() {
		return filiere;
	}

	public void setFiliere(Filiere filiere) {
		if (!PlbUtil.equalsWithNull(this.filiere, filiere)) {
			categorie = null;
		}
		this.filiere = filiere;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public boolean isMoreResults() {
		return moreResults;
	}

	public void setMoreResults(boolean moreResults) {
		this.moreResults = moreResults;
	}

	public List<Categorie> getCategories() {
		if (filiere == null) {
			return categories;
		}
		List<Categorie> ret = new ArrayList<Categorie>();
		for (Categorie categorie : categories) {
			if (categorie.getFiliere().equals(filiere)) {
				ret.add(categorie);
			}
		}
		return ret;
	}

	public void setCategories(List<Categorie> categories) {
		this.categories = categories;
	}


	public boolean isShowEmptyFormation() {
		return showEmptyFormation;
	}


	public void setShowEmptyFormation(boolean showEmptyFormation) {
		this.showEmptyFormation = showEmptyFormation;
	}

	
}
