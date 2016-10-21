package com.plb.si.manager.categorie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.SortOrder;

import com.plb.model.Categorie;
import com.plb.si.service.CategorieDao;

@Name("categoriesManager")
@Scope(ScopeType.CONVERSATION)
public class CategoriesManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4165382860129257623L;

	List<Categorie> results;
	
	List<Categorie> filteredResults;
	
	private Map<String, SortOrder> orders = new HashMap<String, SortOrder>();

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	private String filter;
	
	@Create
	public void init() {
		orders.put("libelle", SortOrder.ascending);
		orders.put("filiere", SortOrder.unsorted);
	}
	@Begin(join = true)
	public List<Categorie> getResults() {

		if ( results == null ) {
			entityManager.clear();
			results = new CategorieDao(entityManager).findAll();
		}
		if ( filteredResults == null ) {
			if ( filter != null ) {
				filteredResults=_filter(results);
			} else {
				filteredResults = results;
			}
		}
		return filteredResults;
	}

	@Observer(value="categorieUpdated")
	public void refresh() {
		results = null;
		filteredResults = null;
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

	
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
		filteredResults=null;
	}
	public List<Categorie> _filter(List<Categorie> results) {
		List<Categorie> ret = new ArrayList<Categorie>();
		for ( Categorie categorie : results ) {
			if ( categorie.getLibelle().toLowerCase().contains(filter.toLowerCase()) || 
				 categorie.getFiliere().getLibelle().toLowerCase().contains(filter.toLowerCase()) ) {
				ret.add(categorie);
			}
		}
		return ret;
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

}
