package com.plb.si.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.core.SeamResourceBundle;

import com.plb.model.Prospect;
import com.plb.model.ProspectCritere;
import com.plb.model.TypeContact;
import com.plb.model.directory.Account;

public class ProspectDao {

	private EntityManager entityManager;

	private String gagne;

	private String nonAffecte;

	public ProspectDao(EntityManager entityManager) {
		this.entityManager = entityManager;
		ResourceBundle bundle = SeamResourceBundle.getBundle();
		gagne = bundle.getString("prospect.gagne");
		nonAffecte = bundle.getString("prospect.nonAffecte");

	}

	// Fonction qui permet de retrouver tous les prospects "actif"
	@SuppressWarnings("unchecked")
	public List<Prospect> findActiveProspect() {
		return (List<Prospect>) entityManager
				.createQuery(
						"from Prospect p WHERE p.statut<>:gagne And p.statut<>'Perdu' And p.statut<>'Abandon' ORDER BY dateCreation DESC")
				.setParameter("gagne", gagne).getResultList();
	}

	// Fonction qui permet un affichage plus rapide
	/**
	 * Requêtage pour les managers
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Prospect> findProspectInit(ProspectCritere critere) {
		StringBuffer sbfQuery = _buildFromClause(critere);
		sbfQuery.append(" and (p.statut=:nonAffecte or p.statut='En attente')");
		sbfQuery.append(_buildWhereClause(critere));
		Query query = entityManager.createQuery(sbfQuery.toString()
				+ " ORDER BY dateCreation DESC");
		_setParameter(critere, query);
		query.setParameter("nonAffecte", nonAffecte);

		return (List<Prospect>) query.getResultList();
	}

	/**
	 * Requêtage pour un commercial
	 * 
	 * @param account
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Prospect> findActiveAssociateProspect(Account account,
			ProspectCritere critere) {
		critere.setCommercial(account);
		StringBuffer sbfQuery = _buildFromClause(critere);
		sbfQuery.append(_buildWhereClause(critere));
		Query query = entityManager.createQuery(sbfQuery.toString()
				+ " ORDER BY dateCreation DESC");
		_setParameter(critere, query);
		return (List<Prospect>) query.getResultList();

	}

	/**
	 * Recherche dans toute la base de prospects
	 * 
	 * @param critere
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Prospect> search(ProspectCritere critere) {
		StringBuffer sbfQuery = _buildFromClause(critere);
		sbfQuery.append(_buildWhereClause(critere));
		Query query = entityManager.createQuery(sbfQuery.toString()
				+ " ORDER BY dateCreation DESC");
		_setParameter(critere, query);
		return (List<Prospect>) query.getResultList();

	}

	private StringBuffer _buildFromClause(ProspectCritere critere) {
		if (critere.getCommercial() != null) {
			return new StringBuffer(
					"from Prospect p join fetch p.prospectDetail d where d.commercial=:commercial");
		} else {
			return new StringBuffer("from Prospect p where 1=1");
		}
	}

	private String _buildWhereClause(ProspectCritere critere) {
		StringBuffer sbf = new StringBuffer("");
		if (!critere.isAll()) {
			sbf.append(" and p.statut<>:gagne and p.statut<>'Perdu' and p.statut<>'Abandon'");
		}
		if (critere.getStatut() != null) {
			sbf.append(" and p.statut=:statut");
		}
		if (critere.getReference() != null) {
			sbf.append(" and p.reference=:ref");
		}
		if (critere.getDateDebut() != null) {
			sbf.append(" and p.dateCreation >= :dateDebut");
		}
		if (critere.getDateFin() != null) {
			sbf.append(" and p.dateCreation <= :dateFin");
		}
		if (critere.getTypeContact() != null) {
			sbf.append(" and p.type=:typeC");
		}
		return sbf.toString();
	}

	private void _setParameter(ProspectCritere critere, Query query) {
		if (!critere.isAll()) {
			query.setParameter("gagne", gagne);
		}
		if (critere.getReference() != null) {
			query.setParameter("ref", critere.getReference());
		}
		if (critere.getStatut() != null) {
			query.setParameter("statut", critere.getStatut());
		}
		if (critere.getCommercial() != null) {
			query.setParameter("commercial", critere.getCommercial()
					.getNomComplet());
		}
		if (critere.getDateDebut() != null) {
			query.setParameter("dateDebut", critere.getDateDebut());
		}
		if (critere.getDateFin() != null) {
			query.setParameter("dateFin", critere.getDateFin());
		}
		if (critere.getTypeContact() != null) {
			query.setParameter("typeC", critere.getTypeContact().getLibelle());
		}
	}

	// Fonction qui recupere les prospects associées a un commercial donné

	// Fonction de recherche pour un commercial donné (remplacé par la fonction
	// search() )
	@SuppressWarnings("unchecked")
	public List<Prospect> searchAssociateProspect(String statut,
			TypeContact typeContact, Account account, String reference,
			boolean all) {
		List<Prospect> results = new ArrayList<Prospect>();
		boolean estVide1 = false;
		boolean estVide2 = false;
		boolean estVide3 = false;
		if (statut == null || "".equals(statut)) {
			estVide1 = true;
		}
		if (typeContact == null) {
			estVide2 = true;
		}
		if (reference == null || "".equals(reference)) {
			estVide3 = true;
		}
		if (all) {
			if (estVide1 && estVide2 && estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.getResultList();
			} else if (estVide2 && estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial and p.statut=:statut ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.setParameter("statut", statut).getResultList();
			} else if (estVide1 && estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial and p.type=:typeC ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.setParameter("typeC", typeContact.getLibelle())
						.getResultList();
			} else if (estVide1 && estVide2) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial and p.reference=:ref ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.setParameter("ref", reference).getResultList();
			} else if (estVide1) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial and p.reference=:ref and p.type=:typeC ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.setParameter("ref", reference)
						.setParameter("typeC", typeContact.getLibelle())
						.getResultList();
			} else if (estVide2) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial and p.reference=:ref and p.statut=:statut ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.setParameter("ref", reference)
						.setParameter("statut", statut).getResultList();
			} else if (estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial and p.statut=:statut and p.type=:typeC ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.setParameter("statut", statut)
						.setParameter("typeC", typeContact.getLibelle())
						.getResultList();
			} else {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE d.commercial=:commercial and p.statut=:statut and p.type=:typeC and p.reference=:ref ORDER BY p.dateCreation DESC")
						.setParameter("commercial", account.getNomComplet())
						.setParameter("statut", statut)
						.setParameter("typeC", typeContact.getLibelle())
						.setParameter("ref", reference).getResultList();
			}
		} else if (all == false) {
			if (estVide1 && estVide2 && estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.getResultList();
			} else if (estVide2 && estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial and p.statut=:statut ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.setParameter("statut", statut).getResultList();
			} else if (estVide1 && estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial and p.type=:typeC ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.setParameter("typeC", typeContact.getLibelle())
						.getResultList();
			} else if (estVide1 && estVide2) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial and p.reference=:ref ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.setParameter("ref", reference).getResultList();
			} else if (estVide1) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial and p.reference=:ref and p.type=:typeC ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.setParameter("ref", reference)
						.setParameter("typeC", typeContact.getLibelle())
						.getResultList();
			} else if (estVide2) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial and p.reference=:ref and p.statut=:statut ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.setParameter("ref", reference)
						.setParameter("statut", statut).getResultList();
			} else if (estVide3) {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial and p.statut=:statut and p.type=:typeC ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.setParameter("statut", statut)
						.setParameter("typeC", typeContact.getLibelle())
						.getResultList();
			} else {
				results = entityManager
						.createQuery(
								"from Prospect p join fetch p.prospectDetail d WHERE p.statut<>:gagne and p.statut<>'Perdu' And p.statut<>'Abandon' and d.commercial=:commercial and p.statut=:statut and p.type=:typeC and p.reference=:ref ORDER BY p.dateCreation DESC")
						.setParameter("gagne", gagne)
						.setParameter("commercial", account.getNomComplet())
						.setParameter("statut", statut)
						.setParameter("typeC", typeContact.getLibelle())
						.setParameter("ref", reference).getResultList();
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findYears() {
		Query q = entityManager
				.createQuery("select distinct year(p.dateCreation) from Prospect p where dateCreation is not null order by year(p.dateCreation) desc");
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> countPotentiel(int potentiel) {
		String query = potentiel == 0 ? "select count(p),p.prospectDetail.commercial from Prospect p where (p.prospectDetail.potentiel = :potentiel or p.prospectDetail.potentiel is null) group by p.prospectDetail.commercial"
				: "select count(p),p.prospectDetail.commercial from Prospect p where p.prospectDetail.potentiel = :potentiel group by p.prospectDetail.commercial";
		return entityManager.createQuery(query)
				.setParameter("potentiel", potentiel).getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<Object[]> countPotentiel(int potentiel, Date from, Date to) {
		String query= 
				potentiel == 0 ? 
						"select count(p),p.prospectDetail.commercial from Prospect p where (p.prospectDetail.potentiel = :potentiel or p.prospectDetail.potentiel is null) and p.dateCreation >= :dateDebut and p.dateCreation <= :dateFin group by p.prospectDetail.commercial" 
						: "select count(p),p.prospectDetail.commercial from Prospect p where p.prospectDetail.potentiel = :potentiel and p.dateCreation >= :dateDebut and p.dateCreation <= :dateFin group by p.prospectDetail.commercial";
		return entityManager.createQuery(query).setParameter("potentiel", potentiel).setParameter("dateDebut", from).setParameter("dateFin", to).getResultList();
		
	}

	public String getGagne() {
		return gagne;
	}

	public void setGagne(String gagne) {
		this.gagne = gagne;
	}

	public String getNonAffecte() {
		return nonAffecte;
	}

	public void setNonAffecte(String nonAffecte) {
		this.nonAffecte = nonAffecte;
	}
}
