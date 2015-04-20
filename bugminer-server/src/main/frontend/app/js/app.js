(function(angular) {
	'use strict';
	
	var app = angular.module('bugminerApp', ['ngResource', 'ngRoute']);
	
	app.config(['$routeProvider', function($routeProvider) {
		$routeProvider
			.when('/', {
				templateUrl: 'partials/projects/index.html',
				controller: 'ProjectsCtrl'
			})
			.when('/projects/:name/bugs', {
				templateUrl: 'partials/projects/bugs.html',
				controller: 'ProjectBugsCtrl'
			});
	}]);
	
	app.factory('Project', function($resource) {
		  return $resource("/api/projects/:name");
	});
	
	app.factory('Bug', function($resource) {
		  return $resource("/api/projects/:name/bugs");
	});
	
	app.controller('ProjectsCtrl', function($scope, Project) {
		Project.query(function(data) {
			$scope.projects = data;
			console.log(data);
		});
	});
	
	app.controller('ProjectBugsCtrl', function($scope, $routeParams, $location, Bug) {
		if ($routeParams.page) {
			var page = $routeParams.page;
		} else {
			var page = 0;
		}

		Bug.query({name: $routeParams.name, page: page}, function(data) {
			$scope.bugs = data;
			console.log(data);
		});

		$scope.switchPage = function(page) {
			var url = $location.path() + "?page=" + page;
			$location.url(url);
		};
	});

})(angular);
