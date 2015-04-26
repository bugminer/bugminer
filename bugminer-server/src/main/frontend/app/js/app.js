(function(angular) {
	'use strict';
	
	var app = angular.module('bugminerApp', ['ngResource', 'ngRoute', 'ui.bootstrap']);
	
	app.config(['$routeProvider', function($routeProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'partials/projects/index.html',
				controller: 'ProjectsCtrl'
			})
			.when('/projects/:name/:tab', {
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
	
	app.controller('ProjectsCtrl', function($scope, Project) {
		Project.query(function(data) {
			$scope.projects = data;
			console.log(data);
		});
	});

	app.controller('ProjectCtrl', function($scope, $routeParams) {
		$scope.tab = $routeParams.tab;
	});
	
	app.controller('ProjectBugsCtrl', function($scope, $routeParams, $location, BugPage) {
		$scope.currentPage = $routeParams.page;
		$scope.currentBug = null;

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
		};
	});

})(angular);
