update information_intra set  statut_intra='Recherche' where statut_intra='AD en recherche';
update information_intra set  statut_intra='Audit planifié' where statut_intra='Attente Audit';
update information_intra set  statut_intra='Commercial' where statut_intra='Attente com';
update information_intra set  statut_intra='Recherche modifié' where statut_intra='Attente AD';
