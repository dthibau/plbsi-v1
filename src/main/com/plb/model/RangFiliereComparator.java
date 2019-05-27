package com.plb.model;

import java.util.Comparator;




public class RangFiliereComparator implements Comparator<Formation> {

	private Filiere filiere;
	
	public RangFiliereComparator(Filiere filiere) {
		this.filiere = filiere;
	}
	@Override
	public int compare(Formation o1, Formation o2) {
		FormationFiliere ff1=null,ff2=null;
		for (FormationFiliere ff : o1.getFormationFilieres() ) {
			if ( ff.getFiliere().equals(filiere) ) {
				ff1 = ff;
				break;
			}
		}
		for (FormationFiliere ff : o2.getFormationFilieres() ) {
			if ( ff.getFiliere().equals(filiere) ) {
				ff2 = ff;
				break;
			}
		}
		int rang1 = -99999; int rang2 = -99999;
		if ( ff1 != null ) {
			rang1 = ff1.getRang();
		}
		if ( ff2 != null ) {
			rang2 = ff2.getRang();
		}
		return  rang1 - rang2;
			
	}


}
