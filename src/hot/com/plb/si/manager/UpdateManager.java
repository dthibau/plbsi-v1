package com.plb.si.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.plb.model.Formation;
import com.plb.model.StashedFormation;

@Name("updateManager")
@Scope(ScopeType.CONVERSATION)
public class UpdateManager {
	
	@In
	EntityManager entityManager;
	
	List<Formation> formations;
	int count;
	
	boolean filterChanged;
	
	@Out
	public static int DONE =0;
	@Out
	public static int NOT_STASHED =1;
	@Out
	public static int STASHED =2;
	
	private int filterMenu = NOT_STASHED;
	
	@Logger
	Log log;
	

	@Begin(join = true, flushMode=FlushModeType.MANUAL)
	public List<Formation> getResults() {
		
		if ( formations == null || filterChanged ) { 
			_fetch();
			filterChanged = false;
		}
		return formations;
		
	}
	
	
	public void save() {
		entityManager.flush();
		formations = null;
		_fetch();
	}
	
	public void stash(Formation formation) {
		StashedFormation s = new StashedFormation();
		s.setFormation(formation);
		entityManager.persist(s);
		entityManager.flush();
		_fetch();
	}
	
	public void unstash(Formation formation) {
		StashedFormation s = entityManager.find(StashedFormation.class, formation.getIdFormation());
		entityManager.remove(s);
		entityManager.flush();
		_fetch();
	}




	@SuppressWarnings("unchecked")
	private void _fetch() {
		log.debug("Fetching ");
		Query q =  entityManager.createQuery(_getFetchQuery());		
		formations = q.setMaxResults(20).getResultList();
		
		Query c =  
		entityManager.createQuery(_getCountQuery());
		
		count = ((Long)c.getSingleResult()).intValue();
	}
	
	public String _getFetchQuery() {
		String ret="";

		if ( filterMenu == DONE ) {
			ret =  "from Formation f where f.archivedDate is null and f.descriptif is not null and f.objectifs_operationnels is not null";
		} else if ( filterMenu == NOT_STASHED ) {
			ret =  "from Formation f where f.archivedDate is null and (f.descriptif is null or f.objectifs_operationnels is null) and f not in (select s.formation from StashedFormation s)"; 
		} else if ( filterMenu == STASHED ) {
			ret =  "select s.formation from StashedFormation s"; 
		}
		log.debug("Fetch query is "+ret);

		return ret;
	}
	public String _getCountQuery() {
		String ret="";
		if ( filterMenu == DONE ) {
			ret = "select count(f) from Formation f where f.archivedDate is null and f.descriptif is not null and f.objectifs_operationnels is not null";
		} else if ( filterMenu == NOT_STASHED ) {
			ret = "select count(f) from Formation f where f.archivedDate is null and (f.descriptif is null or f.objectifs_operationnels is null) and f not in (select s.formation from StashedFormation s)"; 
		} else if ( filterMenu == STASHED ) {
			ret = "select count(s) from StashedFormation s"; 
		}
		log.debug("Count query is "+ret);
		return ret;
	}
	
	public int getCount() {
		log.debug("getCount :"+count);
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public int getFilterMenu() {
		log.debug("getFilterMenu :"+filterMenu);

		return filterMenu;
	}


	public void setFilterMenu(int filter) {
		if ( filter != this.filterMenu ) {
			log.debug("FilterMenu Changed ... fetching");
			this.filterMenu = filter;
			_fetch();
		}
	}


	
	
}
