package com.plb.si.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.plb.model.Formation;

@Name("updateManager")
@Scope(ScopeType.CONVERSATION)
public class UpdateManager {
	
	@In
	EntityManager entityManager;
	
	List<Formation> formations;
	Long updateCount;

	@Begin(join = true, flushMode=FlushModeType.MANUAL)
	public List<Formation> getResults() {
		
		if ( formations == null ) {
			_fetch();
		}		
		return formations;
		
	}
	
	
	public void save() {
		entityManager.flush();
		_fetch();
	}


	public Long getUpdateCount() {
		return updateCount;
	}
	
	private void _fetch() {
		Query q =  entityManager.createQuery(
				"from Formation f where f.archivedDate is null and f.descriptif is null" );
		
		formations = q.setMaxResults(20).getResultList();
		
		Query c =  entityManager.createQuery(
				"select count(f) from Formation f where f.archivedDate is null and f.descriptif is null" );
		
		updateCount = (Long)c.getSingleResult();
	}
}
