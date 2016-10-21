package com.plb.si.manager.intervenant;

import java.io.Serializable;
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
import java.util.HashMap;
//import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
//import org.apache.jackrabbit.spi.commons.query.sql.QueryBuilder;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.SortOrder;

import com.plb.model.Formation;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.service.IntervenantDao;

@Name("searchIntervenantManager")
@Scope(ScopeType.SESSION)
public class SearchIntervenantManager implements Serializable {

	private static final long serialVersionUID = -209923931683152793L;

	public static int pageSize = 20;

	private String searchString;

	List<Intervenant> intervenantResults;
	List<Intervenant> queryResults;

	private Map<String, SortOrder> orders = new HashMap<String, SortOrder>();


	@SuppressWarnings("rawtypes")
	public static final Class[] INDEXED_CLASSES = new Class[] { Intervenant.class, };

	String[] fields = { "nom", "prenom", "rang", "email", "observations", "tarif" };

	@In
	FullTextEntityManager entityManager;

	IntervenantDao intervenantDao;

	private boolean needPerformQuery = true;


	@In
	FacesMessages facesMessages;

	@Out(required = false)
	private Intervenant intervenant;

	@RequestParameter
	String intervenantId;

	@Create
	@Begin(join = true)
	public void init() {

		// intervenantDao = new IntervenantDao(entityManager);
		orders.put("nomComplet", SortOrder.unsorted);
		orders.put("email", SortOrder.unsorted);
		orders.put("rang", SortOrder.unsorted);
		orders.put("tarif", SortOrder.unsorted);
		orders.put("dateMisAJour", SortOrder.descending);

		// Résultat vide par défaut
		// needPerformQuery = false;
		// needFilter = false;
		// dtos = new ArrayList<FormationCompetencesDto>();
	}

	@Begin(join = true)
	public List<Intervenant> getResults() throws ParseException {
		if (needPerformQuery || intervenantResults == null) {
			intervenantResults = _performIntervenantQuery();
			needPerformQuery = false;
		}
		return intervenantResults;
	}

	@Begin(join = true)
	public int getTotal() throws ParseException {
		getResults();
		return intervenantResults.size();
	}

	public void search() {
		needPerformQuery = true;
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
					orders.put(k, SortOrder.descending);
				} else {
					orders.put(k, SortOrder.ascending);
				}
			}
		}
	}

	public void reset() {
		searchString = null;
		needPerformQuery = true;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@SuppressWarnings("unchecked")
	private List<Intervenant> _performIntervenantQuery() throws ParseException {
		if (searchString != null && !"".equals(searchString)) {
			org.apache.lucene.search.Query luceneQuery = getIntervenantLuceneQuery(searchString);
			javax.persistence.Query query = entityManager.createFullTextQuery(
					luceneQuery, Intervenant.class);
			List<Intervenant> results = query.getResultList();
			return results;
		} else {
			return new IntervenantDao(entityManager).findAll();
		}
	}

	private org.apache.lucene.search.Query getIntervenantLuceneQuery(
			String searchWord) throws ParseException {
		Query luceneQuery = null;

		SearchFactory searchFactory = entityManager.getSearchFactory();
		QueryParser parser = new QueryParser(Version.LUCENE_35, "reference",
				searchFactory.getAnalyzer(Formation.class));
		StringBuffer sbf = new StringBuffer();
		String[] tokens = searchWord.split(" ");
		boolean bFirst = true;
		for (String token : tokens) {
			// In this case, we use AND
			if (bFirst) {
				bFirst = false;
			} else {
				sbf.append(" AND ");
			}
			sbf.append("(");

			for (String field : fields) {
				sbf.append(field).append(":").append(token).append(" ");
			}
			sbf.append(")");

		}
		luceneQuery = parser.parse(sbf.toString());

//		System.out.println(luceneQuery);
		return luceneQuery;
	}
}
