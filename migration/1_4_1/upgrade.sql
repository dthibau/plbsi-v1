update formation set distance="Non";

insert into categorie (id_categorie,cat_libelle) values (999,'Dummy');
update formation set id_categorie=999 where id_categorie=0;
update categorie_cat set id_categorie_assoc = 999 where id_categorie_assoc =0;
update categorie set id_categorie = 222 where id_categorie =0;
update categorie_cat set id_categorie_assoc = 222 where id_categorie_assoc =999;
update formation set id_categorie=222 where id_categorie=999;
delete from categorie where id_categorie=999;