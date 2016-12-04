package com.plb.si.dto;

import java.util.Comparator;

import com.plb.model.intervenant.Competence;

public class CompetenceRangComparator implements Comparator<Competence> {

	@Override
	public int compare(Competence c1, Competence c2) {
		
		return c1.getIntervenant().getRang() - c2.getIntervenant().getRang();
	}

}
