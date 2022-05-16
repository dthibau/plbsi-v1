alter table information_intra add column format varchar(80);
alter table information_intra add column certification varchar(80);

ALTER TABLE information_intra CHANGE environnement_tech pre_requis text;

update information_intra set connaissance_sujet=substring(connaissance_sujet,0,80);
ALTER TABLE information_intra CHANGE connaissance_sujet connaissance_sujet varchar(80);

alter table information_intra add column contraintes_particulieres text;
alter table information_intra add column handicap text;

