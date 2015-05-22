(function(angular) {
	'use strict';
	
	var app = angular.module('bugminerApp', ['ngResource', 'ui.router', 'ui.bootstrap']);
	
	app.config(['$stateProvider', function($stateProvider) {
		$stateProvider
			.state('index', {
				url: '/',
				templateUrl: 'partials/projects/index.html'
			})
			.state('project', {
				url: '/projects/:name',
				templateUrl: 'partials/projects/view.html'
			})
			.state('project.bugs', {
				url: '/bugs',
				templateUrl: 'partials/projects/bugs.html'
			});
	}]);
	
	app.factory('Project', function($resource) {
		  return $resource('/api/projects/:name');
	});
	
	app.factory('BugPage', function($resource, $stateParams) {
		  return $resource('/api/projects/:name/bugs', {name: $stateParams.name, sort: 'reportTime', size: 10});
	});

	app.factory('LineChange', function($resource, $stateParams) {
		return $resource('/api/projects/:name/bugs/:tracker/:key/diff', {name: $stateParams.name});
	});
	
	app.controller('ProjectsCtrl', function($scope, Project) {
		Project.query(function(data) {
			$scope.projects = data;
			console.log(data);
		});
	});

	app.controller('ProjectCtrl', function($scope) {

	});
	
	app.controller('ProjectBugsCtrl', function($scope, $location, BugPage, LineChange) {
		console.log($location.search());
		console.log($location.search().page ? true : false);
		$scope.currentPage = $location.search().page ? $location.search().page : 1;
		console.log($scope.currentPage);
		$scope.currentBug = null;
		$scope.changedFiles = [];

		BugPage.get({page: $scope.currentPage - 1}, function(data) {
			$scope.bugs = data.content;
			$scope.totalItems = data.totalElements;
			$scope.itemsPerPage = data.size;
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
