package com.plb.si.manager;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.plb.model.directory.Role;

@Name("sessionDataManager")
@Scope(ScopeType.SESSION)
public class SessionDataManager implements Serializable {


	@In
	EntityManager entityManager;
	
	  
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
	
	
}
