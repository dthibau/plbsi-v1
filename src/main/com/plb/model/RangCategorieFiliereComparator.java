package com.plb.model;

import java.util.Comparator;




public class RangCategorieFiliereComparator implements Comparator<Categorie> {


	@Override
	public int compare(Categorie o1, Categorie o2) {
		int rang1 = o1.getRang();
		int rang2 = o2.getRang();
		
		return rang1 - rang2;
			
	}


}
