update hibernate_sequence set next_val=9999;

-- Tarif intra
insert into tarif_intra (id_tarif_intra,tar_code_intra,tar_tarif) values (1,'T1',1000);
insert into tarif_intra (id_tarif_intra,tar_code_intra,tar_tarif) values (2,'T2',1500);
insert into tarif_intra (id_tarif_intra,tar_code_intra,tar_tarif) values (3,'T3',2000);
insert into tarif_intra (id_tarif_intra,tar_code_intra,tar_tarif) values (4,'T4',2500);
insert into tarif_intra (id_tarif_intra,tar_code_intra,tar_tarif) values (5,'T5',3000);
insert into tarif_intra (id_tarif_intra,tar_code_intra,tar_tarif) values (6,'T6',3500);
insert into tarif_intra (id_tarif_intra,tar_code_intra,tar_tarif) values (99,'',0);

-- alter table formation drop tar_code_intra;
alter table formation drop tar1_code_inter;

alter table formation change for_objectifs for_objectifs mediumtext NULL;
alter table formation change for_participants for_participants mediumtext NULL;
alter table formation change for_prerequis for_prerequis mediumtext NULL;
alter table formation change for_travaux_pratiques for_travaux_pratiques mediumtext NULL;
alter table formation change for_contenu for_contenu mediumtext NULL;
alter table formation change for_partenariat for_partenariat varchar(15) NULL;
alter table formation change for_balise_description for_balise_description mediumtext NULL;
alter table formation change for_balise_keywords for_balise_keywords mediumtext NULL;
alter table formation change for_infos for_infos mediumtext NULL;
alter table formation change for_description for_description mediumtext NULL;
alter table formation change for_rang_categorie for_rang_categorie int(11) NULL;
alter table formation change for_url for_url text NULL;
alter table formation change for_type for_type text NULL;

alter table formation_filiere change forfil_remarques forfil_remarques mediumtext NULL;

update formation set for_type='mauve';

-- insert into categorie_formation (select id_formation,id_categorie from formation);

-- categorie test ne pointe sur aucune filiere !!
-- delete from categorie where id_categorie=188;

-- HP Manager vers supervision (RHOM)
update formation set id_categorie=120 where id_categorie=101;

