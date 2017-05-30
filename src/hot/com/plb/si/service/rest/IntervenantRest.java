package com.plb.si.service.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

import com.plb.model.Fichier;
import com.plb.model.intervenant.Intervenant;
import com.plb.si.manager.FichierManager;
import com.plb.si.util.Labels;

@Name("intervenantRest")
@Scope(ScopeType.EVENT)
public class IntervenantRest implements Serializable {

	@In
	FacesContext facesContext;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;

	@In
	EntityManager entityManager;
	
	@In(create=true)
	FichierManager fichierManager;

	@RequestParameter
	String idIntervenant;

	@RequestParameter
	String selectDate;



	public void get() throws JsonGenerationException, JsonMappingException,
			IOException {

		Intervenant intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(idIntervenant));
		IntervenantJson intervenantJson = new IntervenantJson(intervenant);
		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		ObjectMapper mapper = new ObjectMapper();
		resp.setContentType(RestUtil.JSON_TYPE);

		resp.getWriter().write(mapper.writeValueAsString(intervenantJson));
		facesContext.responseComplete();
	}

	public void update() throws JsonGenerationException, JsonMappingException,
			IOException {
		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		InputStreamReader isr = new InputStreamReader(request.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		String line = in.readLine();

		ObjectMapper mapper = new ObjectMapper();
		IntervenantJson intervenantJson = mapper.readValue(line,
				IntervenantJson.class);
		try {
			Intervenant intervenant = entityManager.find(Intervenant.class,
					intervenantJson.getId());
			_updateIntervenant(intervenant, intervenantJson);
			HttpServletResponse resp = (HttpServletResponse) facesContext
					.getExternalContext().getResponse();
			resp.setContentType("application/text");
			resp.getWriter().write(Labels.getString("infos.updated"));
			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateDate() throws JsonGenerationException,
			JsonMappingException, IOException {

		try {
			Intervenant intervenant = entityManager.find(Intervenant.class,
					Integer.parseInt(idIntervenant));
			Calendar cal = Calendar.getInstance();

			intervenant.getGrilleCompetence().setFilledDate(cal.getTime());
			HttpServletResponse resp = (HttpServletResponse) facesContext
					.getExternalContext().getResponse();
			resp.setContentType("application/text");
			resp.getWriter().write("" + cal.getTime().getTime());
			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void _updateIntervenant(Intervenant intervenant,
			IntervenantJson intervenantJson) {
		intervenant.setNom(intervenantJson.getNom());
		intervenant.setPrenom(intervenantJson.getPrenom());
		intervenant.setEmail(intervenantJson.getEmail());
		intervenant.setAdresse(intervenantJson.getAdresse());
		intervenant.setCodePostal(intervenantJson.getCodePostal());
		intervenant.setVille(intervenantJson.getVille());
		intervenant.setNumTel(intervenantJson.getNumTel());
		intervenant.setNumPortable(intervenantJson.getNumPortable());

		intervenant.setTarif(intervenantJson.getTarif());
		intervenant.setInfoTarifIntervenant(intervenantJson
				.getInfoTarifIntervenant());
		intervenant.setCentres(intervenantJson.getCentres());
		intervenant.setStatut(intervenantJson.getStatut());
		intervenant.setStatutAutre(intervenantJson.getStatutAutre());
		intervenant.setCertifications(intervenantJson.getCertifications());
		intervenant.setAnglais(intervenantJson.isAnglais());

		intervenant
				.setConditionGenerale(intervenantJson.getConditionGenerale());
		intervenant.setChampLibre(intervenantJson.getChampLibre());
	}

	public void fileUpload() {
		HttpServletRequest request = (HttpServletRequest) facesContext
				.getExternalContext().getRequest();
		Intervenant intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(idIntervenant));
		
		
		
		try {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Configure a repository (to ensure a secure temp location is used)
		
			factory.setRepository(new File("../data"));
			System.out.println("Path used for Upload : " + factory.getRepository().getAbsolutePath());
			ServletFileUpload upload = new ServletFileUpload(factory);
			@SuppressWarnings("unchecked")
			List<FileItem> items = upload.parseRequest(request);
			for (FileItem item : items ) {
				if ( !item.isFormField() ) {
					Fichier cv = new Fichier();
					cv.setData(item.get());
					cv.setLength(cv.getData().length);
					cv.setName(item.getName());
					cv.setContentType(item.getContentType());
					intervenant.setCv(cv);
					
				}
			}

			HttpServletResponse resp = (HttpServletResponse) facesContext
					.getExternalContext().getResponse();
			resp.setContentType("application/text");
			resp.getWriter().write(intervenant.getCv().getName());
			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	public void fileDownload() throws IOException {

		Intervenant intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(idIntervenant));
		fichierManager.download(intervenant.getCv().getId());
	}

	public void removeCv() throws IOException {

		Intervenant intervenant = entityManager.find(Intervenant.class,
				Integer.parseInt(idIntervenant));
		intervenant.setCv(null);
	}
}