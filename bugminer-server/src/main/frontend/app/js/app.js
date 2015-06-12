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
				templateUrl: 'partials/projects/view.html',
				abstract: true,
				resolve: {
					params: function($stateParams) {
						return $stateParams;
					}
				}
			})
			.state('project.dashboard', {
				url: '',
				templateUrl: 'partials/projects/dashboard.html'
			})
			.state('project.classify', {
				url: '/classify?page&bug',
				templateUrl: 'partials/projects/classify.html',
				controller: 'ProjectClassifyCtrl',
				resolve: {
					bugPage: function(BugPage, $location, params) {
						var page = $location.search().page ? $location.search().page : 1;

						return BugPage.get({page: page - 1, name: params.name}).$promise.then(function(data) {
							return {
								bugs: data.content,
								totalItems: data.totalElements,
								itemsPerPage: data.size,
								currentPage: page
							};
						});
					}
				}
			})
			.state('project.bugs', {
				url: '/bugs?page&bug',
				templateUrl: 'partials/projects/bugs.html',
				controller: 'ProjectBugsCtrl',
				resolve: {
					bugPage: function(BugPage, $location, params) {
						var page = $location.search().page ? $location.search().page : 1;

						return BugPage.get({page: page - 1, name: params.name}).$promise.then(function(data) {
							return {
								bugs: data.content,
								totalItems: data.totalElements,
								itemsPerPage: data.size,
								currentPage: page
							};
						});
					}
				}
			});
	}]);
	
	app.factory('Project', function($resource) {
		  return $resource('/api/projects/:name');
	});
	
	app.factory('BugPage', function($resource) {
		return $resource('/api/projects/:name/bugs', {sort: 'reportTime', size: 10});
	});

	app.factory('LineChange', function($resource, $stateParams) {
		return $resource('/api/projects/:name/bugs/:tracker/:key/diff', {name: $stateParams.name});
	});

	app.service('DiffService', function(LineChange) {
		this.computeDiff = function(bug, callback) {
			var result = {};

			// fetch line changes
			LineChange.query({tracker: bug.issueTracker.name, key: bug.key}, function(data) {
				result.changedFiles = [];
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

						result.changedFiles.push(currentFile);
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

				callback(result);
			});
		};
	});
	
	app.controller('ProjectsCtrl', function($scope, Project) {
		Project.query(function(data) {
			$scope.projects = data;
		});
	});

	app.controller('ProjectCtrl', function($scope, $state, $stateParams) {
		$scope.state = $state.current.name;

		$scope.navigateTo = function(state) {
			$state.transitionTo(state, {name: $stateParams.name}, {reload: true});
		};
	});

	app.controller('ProjectClassifyCtrl', function($scope, $location, bugPage, DiffService) {
		angular.extend($scope, bugPage);
		$scope.changedFiles = [];
		$scope.currentBug = null;
		$scope.bugKeyUrl = null;

		if ($location.search().bug) {
			$scope.bugKeyUrl = $location.search().bug;
			for (var i = 0; i < $scope.bugs.length; i++) {
				if ($scope.bugs[i].key == $scope.bugKeyUrl) {
					var bug = $scope.bugs[i];

					DiffService.computeDiff(bug, function(result) {
						angular.extend($scope, result);
						$scope.currentBug = bug;
					});
				}
			}
		}

		$scope.$watch('currentPage', function(newPage, oldPage) {
			if (newPage !== oldPage) {
				$location.search({page: $scope.currentPage});
			}
		});

		$scope.setCurrentBug = function(bug) {
			$location.search({page: $scope.currentPage, bug: bug.key});
		};
	});
	
	app.controller('ProjectBugsCtrl', function($scope, $location, bugPage, DiffService) {
		angular.extend($scope, bugPage);
		$scope.changedFiles = [];
		$scope.currentBug = null;
		$scope.bugKeyUrl = null;

		if ($location.search().bug) {
			$scope.bugKeyUrl = $location.search().bug;
			for (var i = 0; i < $scope.bugs.length; i++) {
				if ($scope.bugs[i].key == $scope.bugKeyUrl) {
					var bug = $scope.bugs[i];

					DiffService.computeDiff(bug, function(result) {
						angular.extend($scope, result);
						$scope.currentBug = bug;
					});
				}
			}
		}

		$scope.$watch('currentPage', function(newPage, oldPage) {
			if (newPage !== oldPage) {
				$location.search({page: $scope.currentPage});
			}
		});

		$scope.setCurrentBug = function(bug) {
			$location.search({page: $scope.currentPage, bug: bug.key});
		};
	});

})(angular);
