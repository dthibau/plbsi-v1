alter TABLE formation modify id_categorie int(10) unsigned NOT NULL;
ALTER TABLE formation ADD FOREIGN KEY fk_categorie(id_categorie) REFERENCES categorie(id_categorie);

alter TABLE categorie modify id_filiere_principale int(10) unsigned NOT NULL;
ALTER TABLE categorie ADD FOREIGN KEY fk_filiere_principale(id_filiere_principale) REFERENCES filiere(id_filiere);

alter TABLE categorie_cat modify id_categorie int(10) unsigned NOT NULL;
ALTER TABLE categorie_cat ADD FOREIGN KEY fk_categorie(id_categorie) REFERENCES categorie(id_categorie);

alter TABLE categorie_cat modify id_categorie_assoc int(10) unsigned NOT NULL;
delete from categorie_cat where id_categorie_assoc not in (select id_categorie from categorie);
ALTER TABLE categorie_cat ADD FOREIGN KEY fk_categorie_assoc(id_categorie_assoc) REFERENCES categorie(id_categorie);

alter TABLE competence modify formation_id_formation int(10) unsigned NOT NULL;
ALTER TABLE competence ADD FOREIGN KEY fk_formation(formation_id_formation) REFERENCES formation(id_formation);

alter TABLE devis modify formation_id_formation int(10) unsigned NOT NULL;
ALTER TABLE devis ADD FOREIGN KEY fk_formation(formation_id_formation) REFERENCES formation(id_formation);

alter TABLE devis_session modify session_id_session int(10) unsigned NOT NULL;
# ALTER TABLE devis_session ADD FOREIGN KEY fk_session(session_id_session) REFERENCES formation_session(id_session);

alter TABLE event modify formation_id_formation int(10) unsigned NULL;
update event set formation_id_formation=null where formation_id_formation=0;
delete from event where formation_id_formation not in (select id_formation from formation);
ALTER TABLE event ADD FOREIGN KEY fk_formation(formation_id_formation) REFERENCES formation(id_formation);

alter TABLE formation_partenaire modify formation_id_formation int(10) unsigned NOT NULL;
delete from formation_partenaire_formation_session where formation_partenaire_id in (select id from formation_partenaire where formation_id_formation not in (select id_formation from formation) );
delete from formation_partenaire where formation_id_formation not in (select id_formation from formation);
ALTER TABLE formation_partenaire ADD FOREIGN KEY fk_formation(formation_id_formation) REFERENCES formation(id_formation);

alter TABLE formation_partenaire_formation_session modify sessions_id_session int(10) unsigned NOT NULL;
#ALTER TABLE formation_partenaire_formation_session ADD FOREIGN KEY fk_session(sessions_id_session) REFERENCES formation_session (id_session);

alter TABLE prospect_formation modify formation_id_formation int(10) unsigned NULL;
ALTER TABLE prospect_formation ADD FOREIGN KEY fk_formation(formation_id_formation) REFERENCES formation (id_formation);



