update formulaire_contact_detail set potentiel=3 where asuivre=true;

alter table formulaire_contact_detail drop column asuivre;
