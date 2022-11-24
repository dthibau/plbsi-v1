package com.plb.si.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.record.formula.functions.T;

import com.plb.dto.SessionDto;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.FormationPartenaire;
import com.plb.model.Session;
import com.plb.model.intervenant.Intervenant;

public class PlbUtil {

	public static String evalAsString(FacesContext context, String p_expression)
	{

	    ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
	    ELContext elContext = context.getELContext();
	    ValueExpression vex = expressionFactory.createValueExpression(elContext, p_expression, String.class);
	    String result = (String) vex.getValue(elContext);
	    return result;
	}
	

	
	public static String diffIntervenant(Intervenant oldIntervenant,
			Intervenant newIntervenant) {
		StringBuffer sbf = new StringBuffer();
		if (!PlbUtil.equalsWithNull(oldIntervenant.getAdresse(),
				newIntervenant.getAdresse())) {
			sbf.append(Labels.getString("intervenant.adresse") + " : "
					+ oldIntervenant.getAdresse() + "->"
					+ newIntervenant.getAdresse() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getCentres(),
				newIntervenant.getCentres())) {
			sbf.append(Labels.getString("intervenant.centres") + " : "
					+ oldIntervenant.getCentres() + "->"
					+ newIntervenant.getCentres() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getCertifications(),
				newIntervenant.getCertifications())) {
			sbf.append(Labels.getString("intervenant.certifications") + " : "
					+ oldIntervenant.getCertifications() + "->"
					+ newIntervenant.getCertifications() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getConditionGenerale(),
				newIntervenant.getConditionGenerale())) {
			sbf.append(Labels.getString("intervenant.conditionGenerale")
					+ " : " + oldIntervenant.getConditionGenerale() + "->"
					+ newIntervenant.getConditionGenerale() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getCv(),
				newIntervenant.getCv())) {
			sbf.append(Labels.getString("intervenant.cv") + " : "
					+ oldIntervenant.getCv() + "->" + newIntervenant.getCv()
					+ "<br/>");
		}
		// if ( !PlbUtil.equalsWithNull(oldIntervenant.getDelegation(),
		// newIntervenant.getDelegation()) ) {
		// sbf.append(Labels.getString("intervenant.delegation") +
		// " : "+oldIntervenant.getDelegation()+"->"+newIntervenant.getDelegation()+"<br/>");
		// }
		if (!PlbUtil.equalsWithNull(oldIntervenant.getEmail(),
				newIntervenant.getEmail())) {
			sbf.append(Labels.getString("intervenant.email") + " : "
					+ oldIntervenant.getEmail() + "->"
					+ newIntervenant.getEmail() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getInfoTarifIntervenant(),
				newIntervenant.getInfoTarifIntervenant())) {
			sbf.append(Labels.getString("intervenant.infoTarifIntervenant")
					+ " : " + oldIntervenant.getInfoTarifIntervenant() + "->"
					+ newIntervenant.getInfoTarifIntervenant() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getNom(),
				newIntervenant.getNom())) {
			sbf.append(Labels.getString("intervenant.nom") + " : "
					+ oldIntervenant.getNom() + "->" + newIntervenant.getNom()
					+ "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getNumPortable(),
				newIntervenant.getNumPortable())) {
			sbf.append(Labels.getString("intervenant.numPortable") + " : "
					+ oldIntervenant.getNumPortable() + "->"
					+ newIntervenant.getNumPortable() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getNumTel(),
				newIntervenant.getNumTel())) {
			sbf.append(Labels.getString("intervenant.numTel") + " : "
					+ oldIntervenant.getNumTel() + "->"
					+ newIntervenant.getNumTel() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getObservations(),
				newIntervenant.getObservations())) {
			sbf.append(Labels.getString("intervenant.observations") + " : "
					+ oldIntervenant.getObservations() + "->"
					+ newIntervenant.getObservations() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getOrigine(),
				newIntervenant.getOrigine())) {
			sbf.append(Labels.getString("intervenant.origine") + " : "
					+ oldIntervenant.getOrigine() + "->"
					+ newIntervenant.getOrigine() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getPrenom(),
				newIntervenant.getPrenom())) {
			sbf.append(Labels.getString("intervenant.prenom") + " : "
					+ oldIntervenant.getPrenom() + "->"
					+ newIntervenant.getPrenom() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getRang(),
				newIntervenant.getRang())) {
			sbf.append(Labels.getString("intervenant.rang") + " : "
					+ oldIntervenant.getRang() + "->"
					+ newIntervenant.getRang() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getStatut(),
				newIntervenant.getStatut())) {
			sbf.append(Labels.getString("intervenant.statut") + " : "
					+ oldIntervenant.getStatut() + "->"
					+ newIntervenant.getStatut() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getStatutAutre(),
				newIntervenant.getStatutAutre())) {
			sbf.append(Labels.getString("intervenant.statutAutre") + " : "
					+ oldIntervenant.getStatutAutre() + "->"
					+ newIntervenant.getStatutAutre() + "<br/>");
		}
		if (!PlbUtil.equalsWithNull(oldIntervenant.getTarif(),
				newIntervenant.getTarif())) {
			sbf.append(Labels.getString("intervenant.tarif") + " : "
					+ oldIntervenant.getTarif() + "->"
					+ newIntervenant.getTarif() + "<br/>");
		}
		return sbf.toString();
	}

	public static void normalizeMonth(Calendar cal) {
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

	}

	public static boolean _dayEqual(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		cal1.setTime(d1);
		cal2.setTime(d2);

		return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		} else if (o1 == null && o2 != null) {
			return false;
		} else if (o1 != null && o2 == null) {
			return false;
		} else {
			return o1.equals(o2);
		}
	}

	@SuppressWarnings("rawtypes")
	public static String getCollectionAsString(Collection c) {
		boolean bFirst = true;
		StringBuffer sbf = new StringBuffer();
		for (Object o : c) {
			if (bFirst) {
				sbf.append(o.toString());
				bFirst = false;
			} else {
				sbf.append(" ," + o);
			}
		}
		return sbf.toString();
	}

	public static Date add(Date date, int field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);
		return cal.getTime();
	}

	public static int getMinutes(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR_OF_DAY) * 60;
	}

	public static boolean equalsWithNull(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		} else if (o1 == null && o2 != null) {
			return false;
		} else if (o1 != null && o2 == null) {
			return false;
		} else {
			return o1.equals(o2);
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean dateEqualsWithNull(Date o1, Date o2) {
		if (o1 == null && o2 == null) {
			return true;
		} else if (o1 == null && o2 != null) {
			return false;
		} else if (o1 != null && o2 == null) {
			return false;
		} else {
			return o1.getYear() == o2.getYear()
					&& o1.getMonth() == o2.getMonth()
					&& o1.getDay() == o2.getDay();
		}
	}

	public static boolean collectionEqualsWithNull(Collection<T> col1,
			Collection<T> col2) {
		if (col1 == null && col2 == null) {
			return true;
		} else if (col1 == null && col2 != null) {
			return false;
		} else if (col1 != null && col2 == null) {
			return false;
		} else {
			if (col1.size() != col2.size()) {
				return false;
			} else {
				for (T t : col1) {
					boolean bFound = false;
					for (T t2 : col2) {
						if (equalsWithNull(t, t2)) {
							bFound = true;
							break;
						}
					}
					if (!bFound)
						return false;
				}
				return true;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static boolean identicalList(List l1, List l2) {
		if (l1 == null && l2 == null) {
			return true;
		} else if (l1 == null && l2 != null) {
			return false;
		} else if (l1 != null && l2 == null) {
			return false;
		}
		if (l1.size() != l2.size()) {
			return false;
		}
		for (Object o : l1) {
			if (!l2.contains(o)) {
				return false;
			}
		}
		return true;
	}
	
	public static byte[] sendGetAsBytes(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", "PLBSI");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedInputStream in = new BufferedInputStream(
		        con.getInputStream());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		int c;
		while ( (c=in.read()) != -1 ) {
			bos.write(c);
		}
		in.close();
		bos.close();

		return baos.toByteArray()	;
	}

}
