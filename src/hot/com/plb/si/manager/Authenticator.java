package com.plb.si.manager;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import com.plb.model.directory.Account;

@Name("authenticator")
public class Authenticator
{
    @Logger private Log log;

    @In Identity identity;
    @In Credentials credentials;
    
   @In EntityManager entityManager;
   
   @Out(required=false, scope=ScopeType.SESSION)
   Account loggedUser;
   
 
   
   @In(create=true)
   SessionDataManager sessionDataManager;
   
    public boolean authenticate()
    {
        log.info("authenticating {0}", credentials.getUsername());
        try {
        	loggedUser = checkPassword(credentials.getUsername(),credentials.getPassword());
        	identity.addRole(loggedUser.getPreferredRole().getLibelle());
        	sessionDataManager.setCurrentRole(loggedUser.getPreferredRole());
        	return true;
        } catch (Exception e) {
        	return false;
        }
    }

   private Account checkPassword(String login, String password) {
	   Query q = entityManager.createQuery("from Account where login=:login and password = :password and deletedDate is null");
	   q.setParameter("login", login);
	   q.setParameter("password", password);
	   return (Account)q.getSingleResult();
   }
   

	
}
