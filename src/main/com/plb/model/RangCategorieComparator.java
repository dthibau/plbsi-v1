package com.plb.model;

import java.util.Comparator;




public class RangCategorieComparator implements Comparator<Formation> {


	@Override
	public int compare(Formation o1, Formation o2) {
		int rang1 = o1.getRangCategorie() != null ? o1.getRangCategorie() : -1;
		int rang2 = o2.getRangCategorie() != null ? o2.getRangCategorie() : -1;
		
		return rang1 - rang2;
			
	}


}
