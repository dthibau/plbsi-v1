package com.plb.si.converter;

import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("com.plb.URLConverter")
public class URLConverter implements Converter{
	 
		@Override
		public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	 
			if ( value == null || value.length() == 0 ) {
				return null;
			}
			String HTTP = "http://";
			StringBuilder sUrl = new StringBuilder();
	 
			//if not start with http://, then add it
			if(!value.startsWith(HTTP, 0)  ){
				sUrl.append(HTTP);
			}
			sUrl.append(value);
			
			URL ret = null;
			try {
				ret = new URL(sUrl.toString());
			} catch (MalformedURLException e) {
	 				FacesMessage msg = 
					new FacesMessage("URL Conversion error.", 
							"Invalid URL format.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(msg);
			}
	 
		
	 
			return ret;
		}
	 
		@Override
		public String getAsString(FacesContext context, UIComponent component,
				Object value) {
	 
			return value.toString();
	 
		}	

}
