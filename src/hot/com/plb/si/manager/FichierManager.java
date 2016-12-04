package com.plb.si.manager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.plb.model.Fichier;

@Name("fichierManager")
public class FichierManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8795863557917459542L;

	@In
	EntityManager entityManager;
	
	@In
	FacesContext facesContext;
	
	public void download(long id) throws IOException {
		
		Fichier fichier = entityManager.find(Fichier.class, id);
		HttpServletResponse resp = (HttpServletResponse)facesContext.getExternalContext().getResponse();
	
		if ( fichier != null ) {
			
			resp.setContentType(fichier.getContentType());
			resp.setContentLength(fichier.getLength());
			OutputStream os = resp.getOutputStream();
			os.write(fichier.getData());
			os.close();
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			
		}
		
		facesContext.responseComplete();
	}
}
