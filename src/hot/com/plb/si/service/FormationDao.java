package com.plb.si.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Categorie;
import com.plb.model.Filiere;
import com.plb.model.Formation;
import com.plb.model.FormationFiliere;
import com.plb.model.FormationMutualisees;
import com.plb.model.ProspectFormation;
import com.plb.model.devis.Devis;
import com.plb.model.event.Event;
import com.plb.model.intervenant.Competence;

public class FormationDao {

	public static String ACTIF_CLAUSE="f.archivedDate is null";
	public static String ARCHIVE_CLAUSE="f.archivedDate is not null";
	private EntityManager entityManager;

	public FormationDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public List<Formation> findAll() {
		return (List<Formation>) entityManager.createQuery(
				"from Formation f JOIN FETCH f.formationFilieres where "+ACTIF_CLAUSE).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findAllWithPartenaires() {
		return (List<Formation>) entityManager.createQuery(
				"from Formation f JOIN FETCH f.formationsPartenaire where "+ACTIF_CLAUSE).getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Formation> findAllFrom(Date from ) {
		Query q =  entityManager.createQuery(
				"from Formation f where "+ACTIF_CLAUSE + " and f.dateCreation > :from" );
		q.setParameter("from", from);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findAllBefore(Date before ) {
		Query q =  entityManager.createQuery(
				"from Formation f where "+ACTIF_CLAUSE + " and f.dateCreation <= :before" );
		q.setParameter("before", before);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findAllOrder() {
		return (List<Formation>) entityManager.createQuery(
				"from Formation f where "+ACTIF_CLAUSE+" order by reference ASC").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findAllWithArchived() {
		return (List<Formation>) entityManager.createQuery(
				"from Formation f JOIN FETCH f.formationFilieres ").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findOnlyArchived() {
		return (List<Formation>) entityManager.createQuery(
				"from Formation f JOIN FETCH f.formationFilieres where " + ARCHIVE_CLAUSE).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findAllWithArchivedAndPartenaires() {
		return (List<Formation>) entityManager.createQuery(
				"from Formation").getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Formation> findByCategorie(Categorie categorie) {
		Query q = entityManager
				.createQuery("from Formation f where " + ACTIF_CLAUSE + " and f.categorie=:categorie order by f.rangCategorie");
		q.setParameter("categorie", categorie);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findArchivedByCategorie(Categorie categorie) {
		Query q = entityManager
				.createQuery("from Formation f where " + ARCHIVE_CLAUSE + " and f.categorie=:categorie order by f.rangCategorie");
		q.setParameter("categorie", categorie);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<FormationFiliere> findByCategorieSecondaire(Categorie categorie) {
		Query q = entityManager
				.createQuery("from FormationFiliere ff where ff.formation.archivedDate is null and ff.formation.visible='oui' and ff.isPrincipale='non' and ff.categorie=:categorie order by ff.rang");
		q.setParameter("categorie", categorie);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<FormationFiliere> findByFiliere(Filiere filiere) {
		Query q = entityManager
				.createQuery("from FormationFiliere ff where ff.formation.archivedDate is null and ff.formation.visible='oui' and ff.filiere=:filiere order by ff.rang");
		q.setParameter("filiere", filiere);
		return q.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<FormationFiliere> findByFiliereFrom(Filiere filiere, Date fromDate) {
		Query q = entityManager
				.createQuery("from FormationFiliere ff where ff.formation.archivedDate is null and ff.formation.visible='oui' and ff.filiere=:filiere and ff.formation.dateCreation > :from order by ff.rang");
		q.setParameter("filiere", filiere);
		q.setParameter("from", fromDate);
		return q.getResultList();
	}
	public List<Formation> findByFilierePrincipale(Filiere filiere) {
		List<FormationFiliere> formationFiliere = findByFiliere(filiere);
		List<Formation> formations = new ArrayList<Formation>();
		for ( FormationFiliere ff : formationFiliere ) {
			if ( ff.getFormation().getFilierePrincipale().equals(filiere) ) {
				formations.add(ff.getFormation());
			}
		}
		return formations;
	}
	
	
	public Formation findByReference(String reference) {
		Query q = entityManager
				.createQuery("from Formation f where " + ACTIF_CLAUSE + " and f.reference=:reference");
		q.setParameter("reference", reference);
		return (Formation)q.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Formation> findSuivantes(Formation formation) {
		Query q = entityManager
				.createQuery("from Formation f where f.categorie=:categorie and f.rangCategorie > :rang and f.archivedDate is null order by rangCategorie");
		q.setParameter("categorie", formation.getCategorie());
		q.setParameter("rang", formation.getRangCategorie());
		return q.getResultList();
	}
	
	public void deleteFormation(Formation formation) {
		_unLinkFormation(formation);
		entityManager.remove(formation);
	}

	public void archiveFormation(Formation formation) {
		_unLinkFormation(formation);
		formation.setVisible("non");
		formation.setArchivedDate(new Date());
	}
	public void unarchiveFormation(Formation formation) {
		formation.setArchivedDate(null);
	}

	private void _unLinkFormation(Formation formation) {
		if (formation.getFormationMutualisees() != null) {
			removeFormationMutualise(formation);
		}
		if (formation.getFormationAssociees() != null) {
			removeFormationAssocie(formation);
		}
		formation.setFormationAssociees(new ArrayList<Formation>());
//		EventDao eDao = new EventDao(entityManager);
//		List<Event> events = eDao.findAll(formation);
//		for ( Event e : events ) {
//			entityManager.remove(e);
//		}
		CompetenceDao cDao = new CompetenceDao(entityManager);
		List<Competence> competences = cDao.findByFormation(formation);
		for ( Competence c : competences ) {
			entityManager.remove(c);
		}
		DevisDao dDao = new DevisDao(entityManager);
		List<Devis> devis = dDao.findByFormation(formation);
		for ( Devis d : devis ) {
			d.setSessions(null); // Les sessions ont pu être supprimées par le processus d'archive
			entityManager.remove(d);
		}
		ProspectFormationDao pDao = new ProspectFormationDao(entityManager);
		List<ProspectFormation> pfs = pDao.findByFormation(formation);
		for ( ProspectFormation pf : pfs ) {
			entityManager.remove(pf);
		}
		
	}
	public void removeFormationMutualise(Formation formationMutualisee) {
		FormationMutualisees formationMutualisees = formationMutualisee
				.getFormationMutualisees();
		formationMutualisees.removeFormation(formationMutualisee);
		if (formationMutualisees != null
				&& formationMutualisees.getFormations().size() == 1) {
			formationMutualisees.getFormations().get(0)
					.setFormationMutualisees(null);
			entityManager.remove(formationMutualisees);
		}
	}
	@SuppressWarnings("unchecked")
	public void removeFormationAssocie(Formation formation) {
		List<Formation> formations = (List<Formation>)entityManager.createQuery("select f from Formation f where :formation MEMBER OF f.formationAssociees").setParameter("formation", formation).getResultList();
		for ( Formation f : formations ) {
			f.getFormationAssociees().remove(formation);
		}
	}

}
