package com.plb.si.manager.lucene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryParser.ParseException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.richfaces.component.SortOrder;

import com.plb.dto.FormationDto;
import com.plb.model.Categorie;
import com.plb.model.Filiere;
import com.plb.model.FilierePrincipaleComparator;
import com.plb.model.Formation;
import com.plb.model.FormationPartenaire;
import com.plb.model.Partenaire;
import com.plb.model.RangCategorieComparator;
import com.plb.model.RangFiliereComparator;
import com.plb.si.manager.ApplicationManager;
import com.plb.si.service.FormationDao;
import com.plb.si.service.SearchFormation;
import com.plb.si.service.SessionDao;
import com.plb.si.util.PlbUtil;

@Name("archivedManager")
@Scope(ScopeType.SESSION)
public class ArchivedManager implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -209923931683152793L;
//	public static int pageSize = 20;

	private String searchString;
	private Filiere filiere;
	private Categorie categorie;
	private Partenaire partenaire;
	private boolean plbOnly = false;
	private boolean plbHidden = false;
	private boolean moreResults = false;
	private boolean pagination = true;

	List<Formation> results;
	List<FormationDto> dtos;
	int realSize;
	@Out
	private static int MAX_SIZE=100;

	List<Formation> queryResults;

	List<Date> allMonthSession;

	private Map<String, SortOrder> orders = new HashMap<String, SortOrder>();

	@In(create = true)
	private List<Categorie> categories;


	@In
	FullTextEntityManager entityManager;
	@In
	FacesMessages facesMessages;

	@In(create = true)
	Date lastUpdateTarifs;

	private boolean needPerformQuery = true, needFilter = true;
	boolean firstAccess = true;
	
	@In(create=true)
	ApplicationManager applicationManager;
	

	@Logger
	Log log;
	
	@Create
	@Begin(join = true)
	public void init() {
		log.debug("Creating archivedManager ");
		results = new ArrayList<Formation>();

		unsetOrder();	

	}

	@Begin(join = true)
	public List<FormationDto> getResults() {
		long start = System.currentTimeMillis();
		// cidSearch = Conversation.instance().getId();
		if (needPerformQuery) {
			log.debug("Executing new Query !!" );
			entityManager.clear();
			try {
				queryResults = _performQuery();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				queryResults = new ArrayList<Formation>();
				facesMessages.add(Severity.ERROR,
						"Impossible de comprendre la requête");
			}
			needPerformQuery = false;
			needFilter = true;
			dtos = null;
		}
		if (needFilter) {
			log.debug("Executing new Filtering !!" );
			results = _filterResults(queryResults);
			if (categorie != null) {
				Collections.sort(results, new RangCategorieComparator());
			} else if (filiere != null) {
				Collections.sort(results, new RangFiliereComparator(filiere));
			}
			needFilter = false;
			dtos = null;
		}
		if (dtos == null) {
			log.debug("Building DTOS !!" );
			dtos = FormationDto.buildDtos(results);
		}
		log.debug("getResults found " + dtos.size());
		log.debug("getResults 4 formations took "+(System.currentTimeMillis()-start)+ "ms");
		return dtos;
	}

	public List<FormationDto> getVisibleResults() {
		List<FormationDto> results = getResults();
		
		 List<FormationDto> ret = results.stream().filter(dto -> dto.getFormation().getVisible().equals("oui")).collect(Collectors.toList());
		 
		 Collections.sort(ret);
		 
		 HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		 response.setContentType("application/text");
		
		return ret;
	}


	public int getRealSize() {
		if ( needPerformQuery || needFilter )
			getResults();
		return realSize;
	}


	public void search() {
		needPerformQuery = true;
	}

	public void reset() {
		searchString = null;
		filiere = null;
		categorie = null;
		partenaire = null;
		plbOnly = false;
		needPerformQuery = true;
	}

	@Observer("formationUpdated")
	public void refresh() {
		needPerformQuery = true;
	}

	public void changeCriteria() {
		searchString = null;
		needPerformQuery = true;
		filter();
	}
	public void filter() {
		needFilter = true;
		if ( pagination ) 
			needPerformQuery = true;
	}

	public void filter(ValueChangeEvent evt) {
		needFilter = true;
	}

	public void unsetOrder() {
		orders.put("reference", SortOrder.unsorted);
		orders.put("libelle", SortOrder.unsorted);
		orders.put("duree", SortOrder.unsorted);
		orders.put("tarif", SortOrder.unsorted);
		orders.put("tarifIntra", SortOrder.unsorted);
		orders.put("categorie", SortOrder.unsorted);
		orders.put("statut", SortOrder.unsorted);
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
				orders.put(k, SortOrder.unsorted);
			} else {
				if (orders.get(key).equals(SortOrder.ascending)) {
					orders.put(key, SortOrder.descending);
				} else {
					orders.put(key, SortOrder.ascending);
				}
			}
		}
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		// if ( !searchString.equals(this.searchString) ) {
		// results = null;
		// }
		this.searchString = searchString;
	}

	public Filiere getFiliere() {
		return filiere;
	}

	public void setFiliere(Filiere filiere) {
		this.filiere = filiere;
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

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public Partenaire getPartenaire() {
		return partenaire;
	}

	public void setPartenaire(Partenaire partenaire) {
		this.partenaire = partenaire;
	}

	public boolean isPlbOnly() {
		return plbOnly;
	}

	public void setPlbOnly(boolean plbOnly) {
		this.plbOnly = plbOnly;
	}

	public boolean isPlbHidden() {
		return plbHidden;
	}

	public void setPlbHidden(boolean plbHidden) {
		this.plbHidden = plbHidden;
	}

	public boolean isMoreResults() {
		return moreResults;
	}

	public void setMoreResults(boolean moreResults) {
		this.moreResults = moreResults;
	}	

	public boolean isPagination() {
		return pagination;
	}

	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}

	private List<Formation> _performQuery()
			throws ParseException {
		if (searchString != null && !"".equals(searchString)) {
			unsetOrder(); // Tri naturel par pertinence
			SearchFormation searchFormation = new SearchFormation(entityManager);
			List<Formation> found = searchFormation.search(searchString, moreResults);
			return found.stream().filter(f -> f.getArchivedDate() != null ).collect(Collectors.toList());
		} else {
			sortBy("statut");
			sortBy("statut");
			return applicationManager.getOnlyFormationsArchived();
		}
	}

	private List<Formation> _filterResults(List<Formation> formations) {

		List<Formation> ret = new ArrayList<Formation>();
		for (Formation f : formations) {
			if (filiere != null && !filiere.equals(f.getFilierePrincipale()) && !f.contains(filiere)) {
				continue;
			}
			if (categorie != null && !f.getCategorie().equals(categorie) && !f.contains(categorie) ) {
				continue;
			}
			if (partenaire != null && !plbOnly && !_containsPartenaire(f, partenaire)) {
				continue;
			}
			if (plbOnly && !applicationManager.getFormationsPartenaire(f).isEmpty()) {
				continue;
			}
			
			ret.add(f);
		}
		return ret;
	}
	
	private boolean _containsPartenaire(Formation formation, Partenaire partenaire) {
		
		List<FormationPartenaire> fps = applicationManager.getFormationsPartenaire(formation);
		
		for (FormationPartenaire fp : fps ) {
			if (fp.getPartenaire().equals(partenaire)) {
				return true;
			}
		}
		return false;
	}




}
