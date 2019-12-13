update formation set for_niveau='Fondamental' where for_niveau='Débutant';
update formation set for_niveau='Avancé' where for_niveau='Expert';

delete from typecontact where id_typecontact=18;
insert into typecontact (id_typecontact,libelle,DTYPE) values (18,'EDOF','SI');