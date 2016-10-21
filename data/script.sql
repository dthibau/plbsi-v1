-- DROP TABLE intervenant_formation;
-- DROP TABLE Intervenant;

/*pour avoir les accents
-- SET SESSION collation_database=latin1_swedish_ci; 
-- SET SESSION character_set_database=latin1;*/
LOAD DATA LOCAL INFILE 'LIEC.csv' INTO TABLE Intervenant FIELDS TERMINATED BY ';' ENCLOSED BY '"' LINES TERMINATED BY '\n';

SELECT * FROM Intervenant LIMIT 0, 2000;

UPDATE Intervenant SET dateMisAJour=null; 

UPDATE Intervenant SET rang=50 WHERE rang=0;







