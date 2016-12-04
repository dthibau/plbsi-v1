package com.plb.model;

import java.util.List;

import org.hibernate.search.bridge.StringBridge;

public class FormationFiliereBridge implements StringBridge {

	@SuppressWarnings("unchecked")
	@Override
	public String objectToString(Object arg0) {
		List<FormationFiliere> ffs = (List<FormationFiliere>)arg0;
		StringBuilder sb = new StringBuilder();
		for ( FormationFiliere ff : ffs ) {
			sb.append(ff.getLibelle()+" ");
		}
		return sb.toString();
	}

}
