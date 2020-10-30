package com.plb.si.manager;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIInput;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import com.plb.model.Formation;
import com.plb.model.StashedFormation;

@Name("updateManager")
@Scope(ScopeType.SESSION)
public class UpdateManager {
	
	@In
	EntityManager entityManager;
	
	List<Formation> displayedFormations;
	List<Formation> formations;
	List<StashedFormation> stashedFormations;
	int updateCount, displayedCount=-1;
	
	boolean filterChanged;
	
	@Out
	public static int ALL =0;
	@Out
	public static int NOT_STASHED =1;
	@Out
	public static int STASHED =2;
	
	private int filterMenu = NOT_STASHED;
	

	@Begin(join = true, flushMode=FlushModeType.MANUAL)
	public List<Formation> getResults() {
		
		if ( stashedFormations == null ) {
			_fetchStashed();
		}
		if ( formations == null ) {
			_fetch();
		}	
		
		if ( displayedFormations == null || filterChanged ) { 
			_filter();
			filterChanged = false;
		}
		return formations;
		
	}
	
	
	public void save() {
		entityManager.flush();
		formations = null;
		displayedFormations = null;
	}
	
	public void stash(Formation formation) {
		StashedFormation s = new StashedFormation();
		s.setFormation(formation);
		entityManager.persist(s);
		entityManager.flush();
		stashedFormations.add(s);
		formations = null;
		displayedFormations = null;
	}


	public int getDisplayedCount() {
		getResults();
		return displayedCount;
	}
	public List<Formation> getDisplayedFormations() {
		getResults();
		return displayedFormations;
	}
	

	@SuppressWarnings("unchecked")
	private void _fetch() {
		Query q =  entityManager.createQuery(
				"from Formation f where f.archivedDate is null and f.descriptif is null" );
		
		formations = q.setMaxResults(20+stashedFormations.size()).getResultList();
		
		Query c =  entityManager.createQuery(
				"select count(f) from Formation f where f.archivedDate is null and (f.descriptif is null or f.objectifs_operationnels is null)" );
		
		updateCount = ((Long)c.getSingleResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	private void _fetchStashed() {
		Query q =  entityManager.createQuery(
				"from StashedFormation" );
		
		stashedFormations = q.getResultList();
		

	}
	
//	public void updateFilter() {
//		String value = (String)htmlSelectMenu.getSubmittedValue();
//		setFilterMenu(Integer.parseInt(value));
//	}
	public int getFilterMenu() {
		return filterMenu;
	}


	public void setFilterMenu(int filter) {
		if ( filter != this.filterMenu ) {
			filterChanged = true;
		}
		this.filterMenu = filter;
	}


	private void _filter() {
		if ( filterMenu == ALL ) {
			displayedFormations = formations;
			displayedCount = updateCount;
		} else if ( filterMenu == NOT_STASHED ) {
			displayedFormations = new ArrayList<Formation>();
			for ( Formation f : formations ) {
				boolean bStashed = false;
				for ( StashedFormation s : stashedFormations ) {
					if ( s.getFormation().getIdFormation() == f.getIdFormation() ) {
						bStashed = true;
					}
				}
				if ( ! bStashed ) {
					displayedFormations.add(f);
				}
				if ( displayedFormations.size() >= 20 ) {
					break;
				}
			}
			displayedCount = updateCount - stashedFormations.size();
		} else if ( filterMenu == STASHED ) {
			displayedFormations = new ArrayList<Formation>();
			for ( StashedFormation s : stashedFormations ) {
				displayedFormations.add(s.getFormation());
			}
			displayedCount = stashedFormations.size();
		}
	}


//	public UIInput getHtmlSelectMenu() {
//		return htmlSelectMenu;
//	}
//
//
//	public void setHtmlSelectMenu(UIInput htmlSelectMenu) {
//		this.htmlSelectMenu = htmlSelectMenu;
//	}
	
	
}
