package com.plb.si.manager.lucene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.event.ValueChangeEvent;

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

@Name("searchManager")
@Scope(ScopeType.SESSION)
public class SearchManager implements Serializable {

	// @Out(required=false, scope=ScopeType.SESSION)
	// String cidSearch;

	@Out
	public static int CATALOGUE_VIEW = 0;
	@Out
	public static int SESSION_VIEW = 1;
	private int currentView = CATALOGUE_VIEW;
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
	private boolean obsoleteTarifs = false, obsoleteSessions = false,
			moreResults = false;
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
	Map<String, Float> tarifsInter;
	@In(create = true)
	Date lastUpdateTarifs;

	List<FormationDto> adWordsResult;

	int currentYear, nextYear;

	private boolean needPerformQuery = true, needFilter = true;
	private boolean showNextYear = false;
	boolean firstAccess = true;
	
	@In(create=true)
	ApplicationManager applicationManager;
	
	

	@Logger
	Log log;
	
	@Create
	@Begin(join = true)
	public void init() {
		log.debug("Creating searchManager ");
		results = new ArrayList<Formation>();

		currentYear = Calendar.getInstance().get(Calendar.YEAR);
		nextYear = currentYear + 1;
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
				queryResults = _performQuery(false);
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
			results = _filterResults(queryResults, false);
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
			dtos = FormationDto.buildDtos(results, tarifsInter);
		}
//		realSize = dtos.size();
		log.debug("getResults found " + dtos.size());
//		if ( realSize > MAX_SIZE )
//			return dtos.subList(0, 100);
		log.info("getResults 4 formations took "+(System.currentTimeMillis()-start)+ "ms");
		return dtos;
	}

	public List<FormationDto> getVisibleResults() {
		List<FormationDto> results = getResults();
		
		 List<FormationDto> ret = results.stream().filter(dto -> dto.getFormation().getVisible().equals("oui")).collect(Collectors.toList());
		 
		return ret;
	}
	
	public int getRealSize() {
		if ( needPerformQuery || needFilter )
			getResults();
		return realSize;
	}

	public List<FormationDto> getAdWordResults() {
		// cidSearch = Conversation.instance().getId();
		if (adWordsResult == null) {
			List<Formation> results;
			try {
				results = _performQuery(true);
				results = _filterResults(results, true);
				Collections.sort(results, new FilierePrincipaleComparator());
				adWordsResult = FormationDto.buildDtos(results, tarifsInter);
			} catch (ParseException e) {
				e.printStackTrace();
				facesMessages.add(Severity.ERROR,
						"Impossible de comprendre la requête");
			}

		}

		return adWordsResult;
	}

	public String exportAdWords() {
		adWordsResult = null;
		return "/mz/search/export/adwords.xhtml";
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
		obsoleteTarifs = false;
		obsoleteSessions = false;
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

	public int getCurrentView() {
		return currentView;
	}

	public void setCurrentView(int currentView) {
		this.currentView = currentView;
	}

	public boolean isObsoleteTarifs() {
		return obsoleteTarifs;
	}

	public void setObsoleteTarifs(boolean obsoleteTarifs) {
		this.obsoleteTarifs = obsoleteTarifs;
	}

	public boolean isObsoleteSessions() {
		return obsoleteSessions;
	}

	public void setObsoleteSessions(boolean obsoleteSessions) {
		this.obsoleteSessions = obsoleteSessions;
	}

	public boolean isMoreResults() {
		return moreResults;
	}

	public void setMoreResults(boolean moreResults) {
		this.moreResults = moreResults;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public int getNextYear() {
		return nextYear;
	}

	public void setNextYear(int nextYear) {
		this.nextYear = nextYear;
	}
	

	public boolean isPagination() {
		return pagination;
	}

	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}

	private List<Formation> _performQuery(boolean includeArchived)
			throws ParseException {
		if (searchString != null && !"".equals(searchString)) {
			unsetOrder(); // Tri naturel par pertinence
			SearchFormation searchFormation = new SearchFormation(entityManager);
			return searchFormation.search(searchString, moreResults);
		} else {
			sortBy("statut");
			sortBy("statut");
			return includeArchived ? applicationManager.getAllFormationsArchived() : applicationManager.getAllFormations();
		}
	}

	private List<Formation> _filterResults(List<Formation> formations,
			boolean includeArchived) {

		List<Formation> ret = new ArrayList<Formation>();
		for (Formation f : formations) {
			if (!includeArchived && f.getArchivedDate() != null) {
				continue;
			}
			if (filiere != null && !filiere.equals(f.getFilierePrincipale())) {
				continue;
			}
			if (categorie != null && !f.getCategorie().equals(categorie)) {
				continue;
			}
			if (partenaire != null && !plbOnly && !_containsPartenaire(f, partenaire)) {
				continue;
			}
			if (plbOnly && !applicationManager.getFormationsPartenaire(f).isEmpty()) {
				continue;
			}
			if (currentView == SESSION_VIEW && f.isintra()) {
				continue;
			}
			if (currentView == CATALOGUE_VIEW && obsoleteTarifs
					&& !f.hasObsoleteTarif(lastUpdateTarifs)) {
				continue;
			}
			if (currentView == SESSION_VIEW && !plbHidden && obsoleteSessions
					&& !f.hasObsoleteSession(nextYear)) {
				continue;
			}
			if (currentView == SESSION_VIEW && plbHidden && obsoleteSessions
					&& !f.hasObsoletePartenaireSession(nextYear)) {
				continue;
			}
			if (currentView == SESSION_VIEW && plbHidden
					&& applicationManager.getFormationsPartenaire(f).isEmpty()) {
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



	public List<Date> getAllMonthSession() {
		if (allMonthSession == null) {
			allMonthSession = new ArrayList<Date>();
			SessionDao sDao = new SessionDao(entityManager);
			Calendar start = Calendar.getInstance();
			start.setTime(sDao.getOldestSession().getDebut());
			PlbUtil.normalizeMonth(start);
			Calendar end = Calendar.getInstance();
			end.setTime(sDao.getNewestSession().getDebut());

			for (Calendar cal = start; cal.before(end); cal.add(Calendar.MONTH,
					1)) {
				allMonthSession.add(cal.getTime());
			}
		}
		log.debug("getAllMonthSessions found "+allMonthSession.size());
		return allMonthSession;
	}

	public int getDisplayYear() {
		return showNextYear ? nextYear : currentYear;
	}

	public boolean isShowNextYear() {
		return showNextYear;
	}

	public void setShowNextYear(boolean showNextYear) {
		this.showNextYear = showNextYear;
	}

}
