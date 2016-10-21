package com.plb.si.manager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import com.plb.model.TarifInter;
import com.plb.model.directory.Role;

@Name("sessionDataManager")
@Scope(ScopeType.SESSION)
public class SessionDataManager implements Serializable {


	@In
	EntityManager entityManager;
	
	  @Out(required=false, scope=ScopeType.SESSION)
	   Map<String, Float> tarifsInter;
	  
	/**
	 * 
	 */
	private static final long serialVersionUID = -1502426156582681077L;
	private Role currentRole;

	public boolean isManager() {
		return currentRole != null ? currentRole.equals(Role.MANAGER) : false;
	}
	public boolean isIntervenantsManager() {
		return currentRole != null ? currentRole.equals(Role.INTERVENANTS_MANAGER) : false;
	}
	public boolean isManagerIntervenantsManager() {
		return currentRole != null ? currentRole.equals(Role.MANAGER) || currentRole.equals(Role.INTERVENANTS_MANAGER) : false;
	}
	public boolean isCommercial() {
		return currentRole != null ? currentRole.equals(Role.COMMERCIAL) : false;
	}
	public boolean isDispatcher() {
		return currentRole != null ? currentRole.equals(Role.DISPATCHER) : false;
	}
	public Role getCurrentRole() {
		return currentRole;
	}
	public boolean isManagerDispatcher() {
		return currentRole != null ? currentRole.equals(Role.MANAGER) || currentRole.equals(Role.INTERVENANTS_MANAGER) || currentRole.equals(Role.DISPATCHER) : false;
	}
	public void setCurrentRole(Role currentRole) {
		this.currentRole = currentRole;
	}
	
	@SuppressWarnings("unchecked")
	@Factory("tarifsInter")
	public void fetchTarifsInter() {
		Query q = entityManager.createQuery("from TarifInter");
		List<TarifInter> tarifs = q.getResultList(); 
		 tarifsInter = new HashMap<String, Float>();
		 for ( TarifInter tarif : tarifs) {
			 tarifsInter.put(tarif.getCode(), tarif.getTarif());
		 }

	}
	@Observer(value="tarifsUpdated")
	public void refreshTarisInter() {
		tarifsInter = null;
	}
	
}
