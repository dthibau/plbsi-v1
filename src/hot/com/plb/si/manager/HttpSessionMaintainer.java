package com.plb.si.manager;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.web.ServletContexts;

import com.plb.model.directory.Account;

/**
 * Used to maintain the HttpSession. 
 * The HttpSession is 30 minutes. 
 * The client polls the server after 29 minutes of inactivity 
 * and thus makes the session valid for another 30 minutes. This process
 * is iterative, hence the HttpSession is never invalidated by timeout.
 * 
 * @author hmushtaq
 *
 */
@Name("httpSessionMaintainer")
@Scope(ScopeType.APPLICATION)
public class HttpSessionMaintainer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1588552091346476960L;

	@Logger
	private Log log;
	
	@In(required=false)
	Account loggedUser;
	/**
	 * As soon as this method is called the session is validated for another 30 minutes
	 * @return true if its the same session
	 */
    public boolean maintainSession() {
    	log.debug("Maintaining session for " + loggedUser);
        return !(ServletContexts.instance().getRequest().getSession().isNew());
    }
    

}