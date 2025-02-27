package com.plb.model.directory;

public enum Role {
	MANAGER("manager"),DISPATCHER("dispatcher"),COMMERCIAL("commercial"),INTERVENANTS_MANAGER("intervenantsManager"),TB_MANAGER("tableauBordManager"),AC_MANAGER("AccountsManager"),FA_MANAGER("FactureManager"),OFFRE_MANAGER("OffreManager");
	
	private final String libelle; 
    Role(String libelle) {
        this.libelle = libelle;
    }
	public String getLibelle() {
		return libelle;
	}
    
}
