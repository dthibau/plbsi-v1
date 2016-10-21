package com.plb.si.service.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.plb.model.Filiere;

@Name("filiereRest")
@Scope(ScopeType.EVENT)
public class FiliereRest implements Serializable {

	@In
	FacesContext facesContext;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;

	@In(create=true)
	public List<Filiere> filieres;


	public void getAll() throws JsonGenerationException, JsonMappingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		ObjectMapper mapper = new ObjectMapper();
		resp.setContentType(RestUtil.JSON_TYPE);

		int base = filieres.size()/4;
		int remain= filieres.size()%4;
		List<List<FiliereJson>> ret = new ArrayList<List<FiliereJson>>(4);
		ret.add(new ArrayList<FiliereJson>(base+1));
		ret.add(new ArrayList<FiliereJson>(base+1));
		ret.add(new ArrayList<FiliereJson>(base+1));
		ret.add(new ArrayList<FiliereJson>(base+1));
		int length = base + (remain > 0 ? 1 : 0);
		int i=0;
		for ( ; i<length; i++) {
			ret.get(0).add(new FiliereJson(filieres.get(i)));
		}
		length += base + (remain > 1 ? 1 : 0);
		for ( ; i<length; i++) {
			ret.get(1).add(new FiliereJson(filieres.get(i)));
		}
		length += base + (remain > 2 ? 1 : 0);
		for ( ; i<length; i++) {
			ret.get(2).add(new FiliereJson(filieres.get(i)));
		}
		length += base;
		for ( ; i<length; i++) {
			ret.get(3).add(new FiliereJson(filieres.get(i)));
		}

		resp.getWriter().write(mapper.writeValueAsString(ret));
		facesContext.responseComplete();
	}
}