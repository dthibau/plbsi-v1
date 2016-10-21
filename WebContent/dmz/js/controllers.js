var grilleControllers = angular.module('grilleControllers', []);

grilleControllers.controller('TabsCtrl', [ '$scope', function($scope) {
	$scope.tabs = [ {
		title : 'Vos informations',
		url : 'av_infos.seam'
	}, {
		title : 'Vos compétences',
		url : 'av_competences.seam'
	}];

	$scope.currentTab = 'av_competences.seam';

	$scope.onClickTab = function(tab) {
		$scope.currentTab = tab.url;
	};

	$scope.isActiveTab = function(tabUrl) {
		return tabUrl == $scope.currentTab;
	};
} ]);

grilleControllers.controller('CompetencesCtrl', [
		'$scope',
		'$http',
		'Competence',
		'Util',
		function($scope, $http, Competence,Util) {
			$scope.query = "";
			$scope.showMessage = false;
			$scope.selectedFormations = [];
			$scope.searchByKeyWord = true;
			$scope.formations = [new Array(),new Array(),new Array(),new Array()]; 
			$scope.formationsFiliere = [new Array(),new Array(),new Array(),new Array()];
			$scope.selectDate = "ALL";
			$scope.titleResult='Toutes vos compétences référencées chez PLB';
			$scope.titleSearch='Formations disponibles';
			$scope.fillDate = newDate.toLocaleDateString();
			// Liste des filières
			$http.get('../Rest/filieres.seam').success(function(data) {
				$scope.filieres = data;
			});
			// Compétences de l'intervenant
			Competence.all(idIntervenant, $scope.selectDate, function(data) {
				$scope.competences = data;
			});


			// Recherche de formations avec le champ de saisi
			$scope.switchTab = function() {
				$scope.showMessage=false;
				$scope.searchByKeyWord = !$scope.searchByKeyWord;
				if ( $scope.searchByKeyWord ) {
					Util.markFormations($scope.formations,$scope.competences,$scope.selectedFormations);				
				} else {
					Util.markFormations($scope.formationsFiliere,$scope.competences,$scope.selectedFormations);					
				}
				
			}
			$scope.isKeyWord = function() {
				return $scope.searchByKeyWord;
			};
			// Recherche de formations avec le champ de saisi
			$scope.searchFormations = function() {
				if ($scope.query.length > 2) {
					$scope.showMessage = false;
					$http.get('../Rest/formations.seam?query=' + $scope.query+ '&selectDate='+$scope.selectDate)
							.success(function(data) {
								$scope.formations = data;
								Util.markFormations($scope.formations,$scope.competences,$scope.selectedFormations);	
							})
				} 
			}
			// Affichage des formations d'une filière
			$scope.showFormations = function(filiere) {
				$scope.showMessage = false;
				$scope.selectedFiliere = filiere;
				var url = filiere != null ? '../Rest/formationsFiliere.seam?idFiliere='
						+ filiere.id + '&idIntervenant='+idIntervenant + '&selectDate='+$scope.selectDate : '../Rest/formationsFiliere.seam?idIntervenant='+idIntervenant+ '&selectDate='+$scope.selectDate;
				$http.get(url).success(function(data) {
					$scope.formationsFiliere = data;
					Util.markFormations($scope.formationsFiliere,$scope.competences,$scope.selectedFormations);	
					$scope.selectedFiliere = filiere;
				});
			};
			// Sélection(dé) d'une formation
			$scope.toggleFormation = function(formation) {
				Util.toggleFormation(formation,$scope.selectedFormations)
			};
			
			// Ajout des formations sélectionnées aux compétences de
			// l'intervenant
			$scope.addCompetences = function() {
				Competence.add(idIntervenant,$scope.selectedFormations, function(response) {
					$scope.message = response.data;
					$scope.showMessage = true;
					$scope.lastUpdate = new Date().toLocaleString();
					$scope.selectedFormations = [];
					// Compétences de l'intervenant
					Competence.all(idIntervenant, $scope.selectDate, function(data) {
						$scope.competences = data;
						Util.markFormations($scope.formationsFiliere,$scope.competences,$scope.selectedFormations);	
						Util.markFormations($scope.formations,$scope.competences,$scope.selectedFormations);	
						// $scope.$apply();
					});
//					setTimeout(function() {$scope.showMessage = false;$scope.$apply();}, 3000);
				});
			};
			$scope.updateCompetence = function(competence) {
				Competence.saveOrUpdate(competence, function(response) {
					$scope.message = response.data.message;
					$scope.showMessage = true;
					$scope.lastUpdate = new Date().toLocaleString();
				});
			};
			$scope.removeCompetence = function(competence) {
				Competence.remove(competence.id, function(data) {
					$scope.message = data;
					$scope.showMessage = true;
					$scope.lastUpdate = new Date().toLocaleString();
					Competence.all(idIntervenant, $scope.selectDate, function(data) {
						$scope.competences = data;
						Util.markFormations($scope.formationsFiliere,$scope.competences,$scope.selectedFormations);	
						Util.markFormations($scope.formations,$scope.competences,$scope.selectedFormations);	
					});
				});
			};
			$scope.saveDate = function() {
				if ( $scope.searchByKeyWord ) {
					$scope.searchFormations();
				} else {
					$scope.showFormations($scope.selectedFiliere);
				}
				// Les nouvelles compétences de l'intervenant
    			Competence.all(idIntervenant, $scope.selectDate, function(data) {
    				$scope.competences = data;
    				
    				Util.markFormations($scope.formationsFiliere,$scope.competences,$scope.selectedFormations);	
    			});
    			if ( $scope.selectDate == 'ALL' ) {
    				$scope.titleResult='Toutes vos compétences référencées chez PLB';
    				$scope.titleSearch='Formations disponibles';
    			} else if ( $scope.selectDate == '2Y' ) {
    				$scope.titleResult='Vos compétences référencées chez PLB depuis 2 ans';
    				$scope.titleSearch='Formations disponibles depuis 2 ans';
    			} else if ( $scope.selectDate == '1Y' ) {
    				$scope.titleResult='Vos compétences référencées chez PLB depuis 1 an';
    				$scope.titleSearch='Formations disponibles depuis 1 an';
    			}else if ( $scope.selectDate == '6M' ) {
    				$scope.titleResult='Vos compétences référencées chez PLB depuis 6 mois';
    				$scope.titleSearch='Formations disponibles depuis 6 mois';
    			} else if ( $scope.selectDate == '3M' ) {
    				$scope.titleResult='Vos compétences référencées chez PLB depuis 3 mois';
    				$scope.titleSearch='Formations disponibles depuis 3 mois';
    			} else if ( $scope.selectDate == '1M' ) {
    				$scope.titleResult='Vos compétences référencées chez PLB depuis 1 mois';
    				$scope.titleSearch='Formations disponibles depuis 1 mois';
    			}
			};
		} ]);

grilleControllers.controller('InfosCtrl', [
		'$scope',
		'$http',
		'Upload', '$timeout',
		function($scope, $http, Upload, $timeout) {
			$scope.showMessage = false;
			$scope.message = "";
			$scope.clean = true;
			$http
					.get(
							'../Rest/intervenant.seam?idIntervenant='
									+ idIntervenant).success(function(data) {
						$scope.intervenant = data;
					});

			$scope.updateInfos = function() {
				$http
						.post('../Rest/intervenantUpdate.seam',$scope.intervenant).then(
								function successcallback(response) {
									$scope.message = response.data;
									$scope.showMessage = true;
									$scope.clean = true;
								}

						);
			};
			$scope.changed = function() {
				$scope.clean = false;
			}
			$scope.uploadFiles = function(file, errFiles) {
		        $scope.f = file;
		        $scope.errFile = errFiles && errFiles[0];
		        if (file) {
		            file.upload = Upload.upload({
		                url: '../Rest/intervenantUpload.seam?idIntervenant='+idIntervenant,
		                data: {file: file}
		            });

		            file.upload.then(function (response) {
		                $timeout(function () {
		                    $scope.intervenant.cvName = response.data;
		                });
		            }, function (response) {
		                if (response.status > 0)
		                    $scope.errorMsg = response.status + ': ' + response.data;
		            }, function (evt) {
		                file.progress = Math.min(100, parseInt(100.0 * 
		                                         evt.loaded / evt.total));
		            });
		        }   
		    };
		    $scope.removeCv = function() {
		    	$http.get('../Rest/intervenantRemoveCv.seam?intervenant='+idIntervenant).success(function(data) {
    				$scope.intervenant.cvName = null;
    			});
		    };
		} ]);
