(function(angular) {
	'use strict';
	
	var app = angular.module('bugminerApp', ['ngResource']);
	
	app.factory('Project', function($resource) {
		  return $resource("/projects/:name");
	});
	
	app.controller('ProjectsCtrl', function($scope, Project) {
		Project.query(function(data) {
			$scope.projects = data;
			console.log(data);
		});
	});

})(angular);
