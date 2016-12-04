alter TABLE information_intra modify connaissance_sujet text NULL;
alter TABLE information_intra modify objectif text NULL;
alter TABLE information_intra modify coordonnees_contact_tech text NULL;
alter TABLE information_intra modify note_reservation text NULL;
alter TABLE information_intra modify raison_perte text NULL;
alter TABLE information_intra modify coordonnees_contact_support text NULL;
alter TABLE information_intra modify environnement_tech text NULL;
alter TABLE information_intra modify profil_participant text NULL;
alter TABLE information_intra modify prob_transformation text NULL;

 update account set role='INTERVENANTS_MANAGER' where login='aderuy' or login='lbourquard';