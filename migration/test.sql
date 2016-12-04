insert into account (id,email,nom,prenom,role,login,password) values (1,'pbourquard@plb.fr','BOURQUARD','Pierre','MANAGER','pbourquard','secret');
insert into account (id,email,nom,prenom,role,login,password) values (2,'jtaliercio@plb.fr','TALIERCIO','Julie','MANAGER','jtaliercio','secret');
insert into account (id,email,nom,prenom,role,login,password) values (3,'bmilic@plb.fr','MILIC','Boban','COMMERCIAL','bmilic','secret');
insert into account (id,email,nom,prenom,role,login,password) values (4,'spardessus@plb.fr','PARDESSUS','Stéphane','COMMERCIAL','spardessus','secret');
insert into account (id,email,nom,prenom,role,login,password) values (5,'scarmel@plb.fr','CARMEL','Sébastien','COMMERCIAL','scarmel','secret');

insert into account (id,email,nom,prenom,role,login,password) values (8,'vleclerc@inow.fr','LECLERC','Vincent','MANAGER','vleclerc','secret');
insert into account (id,email,nom,prenom,role,login,password) values (9,'sconti@inow.fr','CONTI','Sébastien','MANAGER','sconti','secret');

delete from updatetarif;
insert into updatetarif (id,date,updater_id) values (1,'2013-08-01',1);
update formation set lastUpdatePrix='2013-08-01';
