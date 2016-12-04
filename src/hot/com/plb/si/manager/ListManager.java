package com.plb.si.manager;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("listManager")
@Scope(ScopeType.CONVERSATION)
public class ListManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1922007909463133225L;
	private int pageSize=25;
	private int currentPage=1;
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getFirst() {
		return ((currentPage-1)*pageSize)+1;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
	
}
