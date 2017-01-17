delete from message where prospect_id in (select id from formulaire_contact where datecreation < '2015-01-01 00:00:00');

delete from event_account where event_id in (select id from event where prospect_id in (select id from formulaire_contact where datecreation < '2015-01-01 00:00:00'));
delete from event where prospect_id in (select id from formulaire_contact where datecreation < '2015-01-01 00:00:00');

delete from formulaire_contact_detail where id_formulaire_contact in (select id from formulaire_contact where datecreation < '2015-01-01 00:00:00');

delete from information_intra where id_formulaire_contact in (select id from formulaire_contact where datecreation < '2015-01-01 00:00:00');

delete from formulaire_contact where datecreation < '2015-01-01 00:00:00';


