package com.plb.si.manager.filiere;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.component.SortOrder;

import com.plb.model.Filiere;
import com.plb.si.service.FiliereDao;

@Name("filieresManager")
@Scope(ScopeType.CONVERSATION)
public class FilieresManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4165382860129257623L;

	List<Filiere> results;
	
	private Map<String, SortOrder> orders = new HashMap<String, SortOrder>();

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	@Create
	public void init() {
		orders.put("libelle", SortOrder.unsorted);
		orders.put("ordre", SortOrder.ascending);
	}
	@Begin(join = true)
	public List<Filiere> getResults() {

		if ( results == null ) {
			results = new FiliereDao(entityManager).findAllOrdered();
		}
		return results;
	}

	public void refresh() {
		results = null;
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

}
