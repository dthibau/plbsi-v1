package com.plb.si.converter;

import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.plb.dto.MonthSessionDto;
import com.plb.model.Session;

@FacesConverter("com.plb.SessionsConverter")
public class SessionsConverter implements Converter{
	 
		@Override
		public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
			MonthSessionDto monthSessionDto = new MonthSessionDto();
			
			String [] sSessions = value.split(";");
			
			for ( String s : sSessions ) {
				
				if ( s.length() > 0 ) {
					try {
					String [] sDay = s.split("-");
					Session session = new Session();
					Calendar debut = Calendar.getInstance();
					debut.set(Calendar.YEAR, _getYear(component));
					debut.set(Calendar.MONTH, _getMonth(component));
					debut.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDay[0]));
					session.setDebut(debut.getTime());
					if ( sDay.length > 1) {
						Calendar fin = Calendar.getInstance();
						fin.set(Calendar.MONTH, _getMonth(component));
						fin.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDay[1]));
						if ( debut.get(Calendar.DAY_OF_MONTH) > fin.get(Calendar.DAY_OF_MONTH) ) {
							fin.add(Calendar.MONTH, 1);
							if ( fin.get(Calendar.MONTH) == Calendar.JANUARY ) {
								fin.add(Calendar.YEAR, 1);
							}
						}
						session.setFin(fin.getTime());
					} else {
						Calendar fin = Calendar.getInstance();
						fin.setTime(debut.getTime());
						fin.add(Calendar.DAY_OF_YEAR, _getDuree(component)-1);
						session.setFin(fin.getTime());
					}
					monthSessionDto.addSession(session);
					} catch ( NumberFormatException e) {
						FacesMessage msg = 
								new FacesMessage("Erreur de conversion de nombre.", 
										"Jour invalide.");
							msg.setSeverity(FacesMessage.SEVERITY_ERROR);
							throw new ConverterException(msg);
					}
				}
			}
			 
	 
			return monthSessionDto;
		}
	 
		@Override
		public String getAsString(FacesContext context, UIComponent component,
				Object value) {
	 
			StringBuilder sb = new StringBuilder();
			List<Session> sessions = ((MonthSessionDto)value).getSessions();
			boolean bFirst = true;
			for ( Session session : sessions) {
				if ( bFirst ) {
					bFirst=false;
				} else {
					sb.append(";");
				}
				sb.append(""+session.getDay());
				if ( session.getDayFin() != -1) {
					sb.append("-"+session.getDayFin());
				}
			}
			return sb.toString();
	 
		}	
		
		private int _getYear(UIComponent component) {
			return Integer.parseInt(component.getId().substring(1,5));
		}
		private int _getMonth(UIComponent component) {
			if ( component.getId().contains("janvier") ) {
				return Calendar.JANUARY;
			} else if ( component.getId().contains("fevrier") ) {
				return Calendar.FEBRUARY;
			}else if ( component.getId().contains("mars") ) {
				return Calendar.MARCH;
			} else if ( component.getId().contains("avril") ) {
				return Calendar.APRIL;
			} else if ( component.getId().contains("mai") ) {
				return Calendar.MAY;
			} else if ( component.getId().contains("juin") ) {
				return Calendar.JUNE;
			} else if ( component.getId().contains("juillet") ) {
				return Calendar.JULY;
			} else if ( component.getId().contains("aout") ) {
				return Calendar.AUGUST;
			} else if ( component.getId().contains("septembre") ) {
				return Calendar.SEPTEMBER;
			} else if ( component.getId().contains("octobre") ) {
				return Calendar.OCTOBER;
			} else if ( component.getId().contains("novembre") ) {
				return Calendar.NOVEMBER;
			} 
				return Calendar.DECEMBER;
		}
		
		private int _getDuree(UIComponent component) {
			return Integer.parseInt(component.getId().substring(component.getId().lastIndexOf("_")+1));
		}

}
