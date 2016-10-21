update formation_session 
set forsess_date_fin=ADDDATE(forsess_date_fin, INTERVAL 1 MONTH),forsess_date_debut=forsess_date_debut where forsess_date_fin<forsess_date_debut

19740,19743,19783
