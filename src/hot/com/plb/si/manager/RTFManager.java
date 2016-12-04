package com.plb.si.manager;

import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.plb.model.Formation;
import com.plb.model.Prospect;
import com.plb.model.ProspectDetail;
import com.plb.model.Session;
import com.plb.model.devis.Devis;
import com.plb.model.devis.DevisSession;
import com.plb.model.directory.Account;
import com.plb.si.service.DevisDao;
import com.plb.util.HTMLUtils;

@Name("rtfManager")
@Scope(ScopeType.CONVERSATION)
public class RTFManager implements Serializable {

	@In
	FacesContext facesContext;

	@In
	EntityManager entityManager;

	@In(required = false)
	Formation formation;

	@In
	Account loggedUser;

	private JasperReport formationReport;

	@Out(value = ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, scope = ScopeType.SESSION, required = false)
	JasperPrint print;

	

	@Logger
	Log log;
	

	public void generate() throws ServletException {
		log.debug(loggedUser + " generate RTF() ");


		if (formationReport == null) {
			formationReport = _loadReport("/formation.jasper");
		}
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		parametersMap.put("REPORT_LOCALE", Locale.FRANCE);
		parametersMap.put("idFormation", formation.getIdFormation());
//		parametersMap.put("simpleContent", HTMLUtils.getSimpleHTML(formation.getContenu()));


		_serveDOC(formationReport, parametersMap);
		facesContext.responseComplete();
		
	}


	private JasperReport _loadReport(String jasperPath) throws ServletException {
		URL jasperResURL = this.getClass().getResource(jasperPath);
		JasperReport report = null;

		try {
			report = (JasperReport) JRLoader.loadObject(jasperResURL);
		} catch (JRException e) {
			throw new ServletException(e.getMessage());
		}
		return report;

	}

	private void _serveDOC(JasperReport jasperReport,
			Map<String, Object> parametersMap) throws ServletException {
		HttpServletResponse resp = (HttpServletResponse) facesContext
				.getExternalContext().getResponse();
		resp.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		resp.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + formation.getReference() + ".rtf\"");
		try {
			Connection c = getConnection();
			print = JasperFillManager.fillReport(jasperReport, parametersMap,
					c);

			JRRtfExporter exporter = new JRRtfExporter();
			// AWDocExporter exporter = new AWDocExporter();

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,
					resp.getOutputStream());
			exporter.exportReport();
			c.close();

			facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private Connection getConnection() throws SQLException {
		org.hibernate.Session session = (org.hibernate.Session) entityManager
				.getDelegate();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session
				.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();

		return cp.getConnection();

	}


	
}
