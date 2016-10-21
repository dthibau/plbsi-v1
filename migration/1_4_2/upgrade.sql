alter table grille_competence add unique unique_intervenant (intervenant_id);

alter table formulaire_contact_detail add unique unique_detail (id_formulaire_contact);