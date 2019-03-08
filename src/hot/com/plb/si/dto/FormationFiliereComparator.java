package com.plb.si.dto;

import java.util.Comparator;

import com.plb.dto.FormationDto;

public class FormationFiliereComparator implements Comparator<FormationDto> {

	@Override
	public int compare(FormationDto f1, FormationDto f2) {
		
		return f1.getRangFiliere() -  f2.getRangFiliere() ;
	}

}
