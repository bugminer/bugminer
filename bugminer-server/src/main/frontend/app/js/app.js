(function(angular) {
	'use strict';
	
	var app = angular.module('bugminerApp', ['ngResource', 'ngRoute', 'ui.bootstrap']);
	
	app.config(['$routeProvider', function($routeProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'partials/projects/index.html',
				controller: 'ProjectsCtrl'
			})
			.when('/projects/:name/:tab?', {
				templateUrl: 'partials/projects/view.html',
				controller: 'ProjectCtrl',
				reloadOnSearch: false
			});
	}]);
	
	app.factory('Project', function($resource) {
		  return $resource('/api/projects/:name');
	});
	
	app.factory('BugPage', function($resource, $routeParams) {
		  return $resource('/api/projects/:name/bugs', {name: $routeParams.name, sort: 'reportTime', size: 10});
	});

	app.factory('LineChange', function($resource, $routeParams) {
		return $resource('/api/projects/:name/bugs/:tracker/:key/diff', {name: $routeParams.name});
	});
	
	app.controller('ProjectsCtrl', function($scope, Project) {
		Project.query(function(data) {
			$scope.projects = data;
			console.log(data);
		});
	});

	app.controller('ProjectCtrl', function($scope, $routeParams, $location) {
		$scope.tab = $routeParams.tab;

		// set dashboard to default
		if ($scope.tab == null) {
			$scope.tab = 'dashboard';
		}

		$scope.navigateTo = function(tab) {
			$location.path('/projects/' + $routeParams.name + '/' + tab);
			$location.search({});
		};
	});
	
	app.controller('ProjectBugsCtrl', function($scope, $routeParams, $location, BugPage, LineChange) {
		$scope.currentPage = $routeParams.page ? $routeParams.page : 1;
		$scope.currentBug = null;
		$scope.changedFiles = [];


		$scope.$watch('currentPage', function() {
			$location.search({page: $scope.currentPage});

			// reset when navigating, as bug will not be in the list anymore
			$scope.currentBug = null;

			BugPage.get({page: $scope.currentPage - 1}, function(data) {
				$scope.bugs = data.content;
				$scope.totalItems = data.totalElements;
				$scope.itemsPerPage = data.size;
			});
		});

		$scope.setCurrentBug = function(bug) {
			$scope.currentBug = bug;

			// fetch line changes
			LineChange.query({tracker: bug.issueTracker.name, key: bug.key}, function(data) {
				$scope.changedFiles = [];
				var lineChanges = data;
				var currentFileName = '';
				var currentFile = null;
				var currentEdit = {
					additions: [],
					deletions: [],
					lastLineNumber: 0
				};

				for (var i = 0; i < lineChanges.length; i++) {
					if (currentFileName !== lineChanges[i].fileName) {
						currentFileName = lineChanges[i].fileName;

						currentFile = {
							name: currentFileName,
							edits: [currentEdit]
						};

						$scope.changedFiles.push(currentFile);
					}

					if (lineChanges[i].oldLineNumber > currentEdit.lastLineNumber + 1) {
						currentEdit = {
							additions: [],
							deletions: [],
							lastLineNumber: 0
						};

						currentFile.edits.push(currentEdit);
					}

					if (lineChanges[i].kind === 'ADDITION') {
						currentEdit.additions.push(lineChanges[i]);
						currentEdit.lastLineNumber = lineChanges[i].oldLineNumber;
					} else {
						currentEdit.deletions.push(lineChanges[i]);
						currentEdit.lastLineNumber = lineChanges[i].oldLineNumber;
					}
				}

				console.log($scope.changedFiles);
			});
		};
	});

})(angular);
