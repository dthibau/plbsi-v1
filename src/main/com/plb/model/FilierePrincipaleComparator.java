package com.plb.model;

import java.util.Comparator;




public class FilierePrincipaleComparator implements Comparator<Formation> {


	@Override
	public int compare(Formation o1, Formation o2) {
		
		return o1.getFilierePrincipale().compareTo(o2.getFilierePrincipale());
			
	}


}
