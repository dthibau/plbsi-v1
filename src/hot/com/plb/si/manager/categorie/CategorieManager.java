package com.plb.si.manager.categorie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.plb.model.Categorie;
import com.plb.model.CategorieConnexe;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.RangCategorieFiliereComparator;
import com.plb.model.directory.Account;
import com.plb.si.manager.ApplicationManager;
import com.plb.si.service.CategorieDao;
import com.plb.si.service.FormationDao;
import com.plb.si.util.Labels;

@Name("categorieManager")
@Scope(ScopeType.CONVERSATION)
public class CategorieManager implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7344651181084068017L;

	@Out(required = false)
	public Categorie categorie;

	@Out
	public List<Formation> formations;

	@RequestParameter
	String categorieId;
	
	@In
	EntityManager entityManager;

	@In
	Account loggedUser;

	@In
	ApplicationManager applicationManager;
	
	@In
	FacesMessages facesMessages;
	
	@Logger
	Log log;

	List<String> selectedCategories;
	
	@In
	List<Categorie> categories;
	@Out
	List<String> categoriesAsString;
	List<Categorie> autresCategoriesFilieres;
	
	@Out(required=false)
	List<Categorie> referents;

	

	@Create
	public void init() {
		log.debug("Creating CategorieManager : loggedUser="+loggedUser);
		categoriesAsString = new ArrayList<String>(categories.size());
		for ( Categorie categorie : categories ) {
			categoriesAsString.add(categorie.getLibelle());
		}
	}

	@Begin(join = true)
	public void select(Categorie categorie) {
		log.debug("Select categorie : " + categorie);
		categorie = entityManager.find(Categorie.class,
				categorie.getId());
		selectedCategories = categorie.getCategoriesConnexesAsCategories();
		formations = new FormationDao(entityManager).findByCategorie(categorie);
		referents = new CategorieDao(entityManager).findReferents(categorie);
		

	}

	@Begin(join = true)
	public String select() {
		log.debug("Select categorie : " + categorieId);
		categorie = entityManager.find(Categorie.class,
				Integer.parseInt(categorieId));
		selectedCategories = categorie.getCategoriesConnexesAsCategories();
		formations = new FormationDao(entityManager).findByCategorie(categorie);
		referents = new CategorieDao(entityManager).findReferents(categorie);

		return "/mz/categorie/categorie.xhtml";
	}
	
	@Begin(join = true)
	public String createNew() {
		log.debug("createNew()");
		categorie = new Categorie();
		formations = new ArrayList<Formation>();
		referents = new ArrayList<Categorie>();
		selectedCategories = new ArrayList<String>();
		autresCategoriesFilieres = null;

		return "/mz/categorie/categorie.xhtml";
	}

	@RaiseEvent("categorieUpdated")
	public String save() {
		log.debug("save()");
		if (!entityManager.contains(categorie)) {
			entityManager.persist(categorie);
		}
//		applicationManager.fetchCategories();
		autresCategoriesFilieres = null;
		return "/mz/categorie/categories.xhtml";
	}


	@RaiseEvent("categorieUpdated")
	public String deleteCategorie() {
		entityManager.clear();
		categorie = entityManager.find(Categorie.class, categorie.getId());
		List<Formation> archivedFormations = new FormationDao(entityManager).findArchivedByCategorie(categorie);
		for ( Formation formation : archivedFormations ) {
			formation.setCategorie(null);
		}
		entityManager.remove(categorie);
		facesMessages.addFromResourceBundle(Severity.INFO, "Catégorie supprimée");
		return "/mz/categorie/categories.xhtml";
	}


	public boolean isDeletable() {
		return formations.isEmpty() && categorie.getCategoriesConnexes().isEmpty() && referents.isEmpty();
	}

	public List<String> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(List<String> selectedCategories) {
		this.selectedCategories = selectedCategories;
		for ( CategorieConnexe categorieConnexe : this.categorie.getCategoriesConnexes() ) {
			entityManager.remove(categorieConnexe);
		}
		List<CategorieConnexe> categoriesConnexes = new ArrayList<CategorieConnexe>();
		int i=0;
		for ( String libelle : selectedCategories ) {
			Categorie linkedCategorie = _getCategorieFromLibelle(libelle);
			CategorieConnexe categorieConnexe = new CategorieConnexe();
			categorieConnexe.setBaseCategorie(this.categorie);
			categorieConnexe.setLinkedCategorie(linkedCategorie);
			categorieConnexe.setOrder(i++);
			categoriesConnexes.add(categorieConnexe);
		}
		this.categorie.setCategoriesConnexes(categoriesConnexes);
	}

	private Categorie _getCategorieFromLibelle(String libelle) {
		for ( Categorie categorie : categories ) {
			if ( categorie.getLibelle().equals(libelle)) {
				return categorie;
			}
		}
		// Jamais, j'espère
		return null;
	}

	public List<Categorie> getAutresCategoriesFiliere() {
		
		if ( autresCategoriesFilieres == null ) {
			autresCategoriesFilieres = new ArrayList<Categorie>();
			autresCategoriesFilieres.addAll(categorie.getFiliere().getCategories());
			Collections.sort(autresCategoriesFilieres,
					new RangCategorieFiliereComparator());
		}
		return autresCategoriesFilieres;
	}
	
	public void updateMeta() {
		if ( categorie.getBaliseTitle() == null || categorie.getBaliseTitle().length() == 0 ) {
			categorie.setBaliseTitle(Labels.getString("categorie.metaTitre.default",categorie.getLibelle()));
		}
		if ( categorie.getBaliseDescription() == null || categorie.getBaliseDescription().length() == 0 ) {
			categorie.setBaliseDescription(Labels.getString("categorie.metaDescription.default",categorie.getLibelle()));
		}
	}
	public void updateAutresCategoiresFiliere() {
		autresCategoriesFilieres = null;
		// Set categorie connexe
		Filiere filiere = categorie.getFiliere();
		if ( filiere != null ) {
			for ( CategorieConnexe cc : categorie.getCategoriesConnexes() ) {
				entityManager.remove(cc);
			}
			List<CategorieConnexe> categoriesConnexes = new ArrayList<CategorieConnexe>();
			List<Categorie> categories = filiere.getCategories();
			int i=0;
			for ( Categorie categorie : categories ) {
				CategorieConnexe categorieConnexe = new CategorieConnexe();
				categorieConnexe.setBaseCategorie(this.categorie);
				categorieConnexe.setLinkedCategorie(categorie);
				categorieConnexe.setOrder(i++);
				categoriesConnexes.add(categorieConnexe);
			}
			categorie.setCategoriesConnexes(categoriesConnexes);
			selectedCategories = categorie.getCategoriesConnexesAsCategories();
			facesMessages.add(Severity.INFO,"Catégories connexes modifiées !");
		}
	}
}
