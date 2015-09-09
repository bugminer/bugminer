var app;

(function(angular) {
	'use strict';
	
	app = angular.module('bugminerApp', ['ngResource', 'ui.router', 'ui.bootstrap', 'restangular']);
	
	app.config(['$stateProvider', function($stateProvider) {
		$stateProvider
			.state('index', {
				url: '',
				templateUrl: 'partials/index.html',
			})
			.state('index2', {
				url: '/',
				templateUrl: 'partials/index.html',
			})
			.state('projects', {
				url: '/projects',
				templateUrl: 'partials/projects/index.html',
				controller: 'ProjectsCtrl'
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
					bugPage: function(BugPage, $stateParams) {
						var page = $stateParams.page ? $stateParams.page : 1;

						return BugPage.get({page: page - 1, name: $stateParams.name}).$promise.then(function(data) {
							return {
								bugs: data.content,
								totalItems: data.totalElements,
								itemsPerPage: data.size,
								data: {
									'currentPage': page
								}
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
					bugPage: function(BugPage, $stateParams) {
						var page = $stateParams.page ? $stateParams.page : 1;

						return BugPage.get({page: page - 1, name: $stateParams.name}).$promise.then(function(data) {
							return {
								bugs: data.content,
								totalItems: data.totalElements,
								itemsPerPage: data.size,
								data: {
									'currentPage': page
								}
							};
						});
					}
				}
			})
			.state('task', {
				url: '/tasks',
				templateUrl: 'partials/tasks/index.html',
				controller: 'TasksCtrl',
				resolve: {
					params: function($stateParams) {
						return $stateParams;
					}
				}
			})
			.state('cluster', {
				url: '/clusters',
				templateUrl: 'partials/clusters/index.html',
				controller: 'ClusterCtrl',
				resolve: {
					params: function($stateParams) {
						return $stateParams;
					}
				}
			});
	}]);

	app.config(['RestangularProvider', function(RestangularProvider) {
		RestangularProvider.setBaseUrl('/api');
	}]);

	app.run(['Restangular', function(Restangular) {
		function getIDField(route) {
			switch (route) {
				case 'clusters':
					return 'name';
				default:
					return 'id';
			}
		}
		Restangular.configuration.getIdFromElem = function(elem) {
			return elem[getIDField(elem.route)];
		};

		Restangular.configuration.setIdToElem = function(elem, id) {
			elem[getIDField(elem.route)] = id;
		};
	}]);
})(angular);
