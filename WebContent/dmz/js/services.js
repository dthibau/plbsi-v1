var intervenantService = angular.module('intervenantService', []);

intervenantService.factory('Intervenant', 
		function($http) {
			return {
				all : function(idIntervenant,callback) {
					$http.get('../Rest/intervenant.seam?idIntervenant=' + idIntervenant)
					.success(callback);
				}
			};
		} );


var competenceService = angular.module('competenceService', []);

competenceService.factory('Competence', 
		function($http) {
			return {
				all : function(idIntervenant,selectDate, callback) {
					$http.get('../Rest/competences.seam?idIntervenant=' + idIntervenant +'&selectDate='+selectDate)
					.success(callback);
				},
				allNew : function(idIntervenant,callback) {
					$http.get('../Rest/competencesNew.seam?idIntervenant=' + idIntervenant)
					.success(callback);
				},
				add :  function(idIntervenant,formations,callback) {
					var idFormations = [];
    				for (var i = 0; i < formations.length; i++) {
    					idFormations[i] = formations[i].id;
    				}
    				$http.get(
    						'../Rest/addCompetences.seam?idIntervenant='
    								+ idIntervenant + '&selectedFormations='
    								+ idFormations).then(callback);	
				},
				saveOrUpdate :  function(competence,callback) {
					$http
					.post('../Rest/competenceUpdate.seam',competence,{header : {'Content-Type' : 'application/json; charset=UTF-8'}
					}).then(callback);	
				},
				remove :  function(idCompetence,callback) {
					$http.get('../Rest/removeCompetence.seam?idCompetence=' + idCompetence)
					.success(callback);		
				},
				removeByFormation :  function(idFormation,idIntervenant,callback) {
					$http.get('../Rest/competenceRemoveByFormation.seam?idFormation=' + idFormation + '&idIntervenant='+idIntervenant)
					.success(callback);		
				}
			};
		} );

var utilService = angular.module('utilService', []);

utilService.factory('Util', 
		function() {
			return {
				markFormations : function(formations,competences,selectedFormations) {
					var markOneArray = function(formations,competences,selectedFormations) {
						var length = formations.length;
						var competenceLength = competences.length;
						var selectionLength = selectedFormations.length;
						for (var i = 0; i < length; i++) {
							var idFormation = formations[i].id;
							formations[i].known = false;
							formations[i].selected = false;
							for (var j = 0; j < competenceLength; j++) {
								if (idFormation == competences[j].idFormation) {
									formations[i].known = true;
									formations[i].selected = true;
								}
							}
							for (var j = 0; j < selectionLength; j++) {
								if (idFormation == selectedFormations[j].id) {
									formations[i].selected = true;
								}
							}
						}
					};
					markOneArray(formations[0],competences,selectedFormations);
					markOneArray(formations[1],competences,selectedFormations);
					markOneArray(formations[2],competences,selectedFormations);
					markOneArray(formations[3],competences,selectedFormations);
				},
				toggleFormation : function(formation, selectedFormations) {
					var result = selectedFormations.filter(function(obj) {
						return obj.id == formation.id;
					});
					if (result.length == 0) {
						selectedFormations.push(formation);
					} else {
						var selectionLength = selectedFormations.length;
						var index = -1;
						for (var j = 0; j < selectionLength; j++) {
							if (formation.id == selectedFormations[j].id) {
								index=j;
								break;
							}
						}
						if ( index != -1 ) {
							selectedFormations.splice(index, 1);
						}
					}
				}
			};
		} );

