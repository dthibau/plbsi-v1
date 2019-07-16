



# Catégorie secondaires non renseignées
select formation.for_libelle,formation.for_reference from formation_filiere,formation
where forfil_filiere_principale ='non'
and formation_filiere.id_formation=formation.id_formation
and formation_filiere.id_categorie is null;

# Existant : filiére secondaires 
select formation.for_libelle,formation.for_reference,fil_libelle from formation_filiere,formation,filiere
where forfil_filiere_principale ='non'
and formation_filiere.id_formation=formation.id_formation
and formation_filiere.id_filiere=filiere.id_filiere;


# Existant : categorie ne correspondant à la filiere principale
select formation.for_libelle,formation.for_reference,categorie.cat_libelle,filiere.fil_libelle from formation_filiere,formation,categorie,filiere
where forfil_filiere_principale ='oui'
and categorie.id_filiere_principale != formation_filiere.id_filiere
and formation.id_categorie = categorie.id_categorie
and formation_filiere.id_filiere = filiere.id_filiere
and formation_filiere.id_formation = formation.id_formation

# Existant : formation n existant plus présent dans la table formation_filiere : 758 !!
select * from formation_filiere where id_formation not in (select id_formation from formation);


# Mise en cohérence filiere principale
------------------------------------------------------------------
delete from formation_filiere where forfil_filiere_principale ='oui'

delete from formation_filiere where CONCAT_WS('-',id_formation,id_filiere) IN (select CONCAT_WS('-',id_formation,id_filiere_principale) from formation,categorie where formation.id_categorie=categorie.id_categorie)
----------------------------------------------------------------------
delete from formation_filiere;

insert into formation_filiere (id_formation,id_filiere,forfil_rang,forfil_filiere_principale,id_categorie) 
(select id_formation,id_filiere_principale,for_rang_categorie,'oui',formation.id_categorie from formation,categorie where formation.id_categorie=categorie.id_categorie and for_rang_categorie is not null);

/* Requêtes Web */
/* Formations de la Filiere 1 */
SELECT filiere.fil_libelle,categorie.cat_libelle,formation.for_libelle,formation.for_reference,formation.for_duree 
FROM plbconsult.formation_filiere,formation,filiere,categorie
WHERE formation_filiere.id_filiere = 1
AND formation_filiere.id_formation = formation.id_formation
AND formation_filiere.id_categorie = categorie.id_categorie
AND formation_filiere.id_filiere = filiere.id_filiere
AND formation.archivedDate is null
order by categorie.cat_rang,formation_filiere.forfil_rang;

/* Formations de la Catégorie 55*/
SELECT categorie.cat_libelle,categorie.cat_description,formation.for_libelle,formation.for_reference,formation.for_duree 
FROM plbconsult.formation_filiere,formation,categorie
WHERE formation_filiere.id_categorie = 55
AND formation_filiere.id_formation = formation.id_formation
AND formation_filiere.id_categorie = categorie.id_categorie
AND formation.archivedDate is null
order by formation_filiere.forfil_rang;

/* Catégories associée de la catégorie 55 */
SELECT categorie.cat_libelle,categorie.cat_description 
FROM categorie_cat,categorie
WHERE categorie_cat.id_categorie = 55
AND categorie_cat.id_categorie_assoc = categorie.id_categorie
order by categorie_cat.order;



