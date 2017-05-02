update information_intra set statut_intra='statut.intra.recherche' where statut_intra='Recherche';
update information_intra set statut_intra='statut.intra.audit' where statut_intra='Audit planifié';
update information_intra set statut_intra='statut.intra.commercial' where statut_intra='Commercial';
update information_intra set statut_intra='statut.intra.recherche_modifiee' where statut_intra='Recherche modifiée';
update information_intra set statut_intra='statut.intra.logistique' where statut_intra='Logistique';
update information_intra set statut_intra='statut.intra.annule' where statut_intra='Annulé';
update information_intra set statut_intra='statut.intra.ok' where statut_intra='OK';
