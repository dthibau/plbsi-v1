package com.plb.model.directory;

public enum Role {
	MANAGER("manager"),DISPATCHER("dispatcher"),COMMERCIAL("commercial"),INTERVENANTS_MANAGER("intervenantsManager"),TB_MANAGER("tableauBordManager");
	
	private final String libelle; 
    Role(String libelle) {
        this.libelle = libelle;
    }
	public String getLibelle() {
		return libelle;
	}
    
}
