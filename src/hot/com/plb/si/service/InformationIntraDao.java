
package com.plb.si.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.plb.model.Formation;
import com.plb.model.InformationIntra;
import com.plb.model.directory.Account;
import com.plb.model.intervenant.Intervenant;


public class InformationIntraDao {
	
	private EntityManager entityManager;
	
	public InformationIntraDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	//Recupere toutes les demande d'intra car quand un intra est créer un objet InformationIntra est créé
	@SuppressWarnings("unchecked")
	public List<InformationIntra> findAll(){
		List<InformationIntra> results = (List<InformationIntra>) entityManager.createQuery("from InformationIntra i where i.valide=0 ORDER BY dateCreation DESC").getResultList();
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<InformationIntra> search(Account commercial, Intervenant intervenant, String statut, boolean all, Formation formation){
		List<InformationIntra> results = new ArrayList<InformationIntra>();
		String query = "";
		boolean precedentExiste = false;
		if(intervenant == null){
			if(formation != null){
				query = "select i from InformationIntra i join i.prospect p join p.formations f where f.formation=:formation";
				if(statut != null){
					query = query + "  and i.statutIntra=:statut";
				}
				if(all == false){
					query = query + " and i.valide=0";
				}
				if(commercial != null){
					
					query = query + " and i.commercial=:commercial";
				}
			}
			else if(formation == null){
				query = "from InformationIntra i";
				if(statut != null){
					query = query + "  where i.statutIntra=:statut";
					precedentExiste = true;
				}
				if(all == false){
					if(precedentExiste == true){
						query = query + " and i.valide=0";
					}
					else{
						query = query + "  where i.valide=0";
						precedentExiste = true;
					}
				}
				if(commercial != null){
					if(precedentExiste == true){
						query = query + " and i.commercial=:commercial";
					}
					else{
						query = query + "  where i.commercial=:commercial";
					}
				}
			}
		}
		else{
			query = "select i from InformationIntra i join i.prospect p join p.formations f join f.intervenants e where e.intervenant = :intervenant and e.favori=1";
			if(formation != null){
				query = query + " and f.formation=:formation";
			}
			if(statut != null){
				query = query + " and i.statutIntra=:statut";
			}
			if(all == false){
				query = query + " and i.valide=0";
			}
			if(commercial != null){
				query = query + " and i.commercial=:commercial";
			}
		}
		query = query + " ORDER BY i.dateCreation DESC";
		Query requete = entityManager.createQuery(query);
		//On attribut les valeur des paramètres
		if(intervenant != null){
			requete.setParameter("intervenant", intervenant);
		}
		if(formation != null){
			requete.setParameter("formation", formation);
		}
		if(commercial != null){
			requete.setParameter("commercial", commercial);
		}
		if(statut != null){
			requete.setParameter("statut", statut);
		}
		results = (List<InformationIntra>) requete.getResultList();
		return results;		
	}
	
	@SuppressWarnings("unchecked")
	public List<InformationIntra> searchModified(){
		List<InformationIntra> results = (List<InformationIntra>) entityManager.createQuery("from InformationIntra i where i.changementCom=1 and i.changementAdminIntra=0 ORDER BY i.dateModification DESC").getResultList();
		return results;
	}
	
}
