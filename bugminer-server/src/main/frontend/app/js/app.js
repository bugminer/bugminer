(function(angular) {
	'use strict';
	
	var app = angular.module('bugminerApp', ['ngResource', 'ui.router', 'ui.bootstrap', 'restangular']);
	
	app.config(['$stateProvider', function($stateProvider) {
		$stateProvider
			.state('index', {
				url: '/',
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
	
	app.factory('Project', function($resource) {
		  return $resource('/api/projects/:name', {
			  name: '@name'
		  }, {
			  synchronize: {
				  method: 'POST',
				  url: '/api/projects/:name/synchronize'
			  },
			  mapCommits: {
				  method: 'POST',
				  url: '/api/projects/:name/map-commits'
			  }
		  });
	});
	
	app.factory('BugPage', function($resource) {
		return $resource('/api/projects/:name/bugs', {sort: 'reportTime', size: 10});
	});

	app.factory('LineChange', function($resource, $stateParams) {
		return $resource('/api/projects/:name/bugs/:tracker/:key/diff', {name: $stateParams.name});
	});

	app.factory('Task', function($resource) {
		return $resource('/api/tasks/:name');
	});

	app.factory('Cluster', function($resource) {
		return $resource('/api/clusters/:name');
	});

	app.factory('ClusterNode', function($resource) {
		return $resource('/api/nodes/:id', {
			id: '@id'
		}, {
			  start: {
				  method: 'POST',
				  url: '/api/nodes/:id/start'
			  },
			  stop: {
				  method: 'POST',
				  url: '/api/nodes/:id/start'
			  }
		  });
	});

	app.service('DiffService', function(LineChange) {
		this.computeDiff = function(bug, callback) {
			var result = {};

			LineChange.query({tracker: bug.issueTracker.name, key: bug.key}, function(lineChanges) {
				result.files = [];
				var currentFileName = '';
				var currentFile = null;
				var currentLine = {
					additions: [],
					deletions: []
				};
				var currentHunk = {
					lines: [currentLine],
					lastLineNumber: -1
				};


				for (var i = 0; i < lineChanges.length; i++) {
					var currentLineChange = lineChanges[i];
					var oldLineNumber = currentLineChange.oldLineNumber;

					// if file name changed then start next file
					if (currentFileName !== currentLineChange.fileName) {
						currentFileName = currentLineChange.fileName;

						currentFile = {
							name: currentFileName,
							hunks: []
						};

						result.files.push(currentFile);
					}

					// if line number of this line change is greater than the last + 1
					// this line change belongs to the next hunk
					if (oldLineNumber > currentHunk.lastLineNumber + 1) {
						var currentLine = {
							additions: [],
							deletions: []
						};
						var currentHunk = {
							lines: [currentLine],
							lastLineNumber: 0
						};

						currentFile.hunks.push(currentHunk);
					} else if (oldLineNumber > currentHunk.lastLineNumber) {
						var currentLine = {
							additions: [],
							deletions: []
						};

						currentHunk.lines.push(currentLine);
					}

					if (currentLineChange.kind === 'ADDITION') {
						currentLine.additions.push(currentLineChange);
						currentHunk.lastLineNumber = oldLineNumber;
					} else {
						currentLine.deletions.push(currentLineChange);
						currentHunk.lastLineNumber = oldLineNumber;
					}
				}
				console.log(result);
				callback(result);
			});
		};
	});
	
	app.controller('ProjectsCtrl', function($scope, Project) {
		Project.query(function(data) {
			$scope.projects = data;
		});
	});

	app.controller('ProjectCtrl', function($scope, $state, $stateParams, Project) {
		$scope.state = $state.current.name;

		Project.get({name: $stateParams.name}, function(project) {
			$scope.project = project;
		});

		$scope.navigateTo = function(state) {
			$state.transitionTo(state, {name: $stateParams.name}, {reload: true});
		};

		$scope.synchronize = function() {
			$scope.project.$synchronize();
		}

		$scope.mapCommits = function() {
			$scope.project.$mapCommits();
		}
	});

	app.controller('ProjectClassifyCtrl', function($scope, $state, $stateParams, bugPage, DiffService) {
		angular.extend($scope, bugPage);
		$scope.changedFiles = [];
		$scope.currentBug = null;
		$scope.bugKeyUrl = null;
		$scope.classificationHasChanged = false;

		if ($stateParams.bug) {
			$scope.bugKeyUrl = $stateParams.bug;
			for (var i = 0; i < $scope.bugs.length; i++) {
				if ($scope.bugs[i].key == $scope.bugKeyUrl) {
					var bug = $scope.bugs[i];
					$scope.currentBug = bug;

					DiffService.computeDiff(bug, function(result) {
						angular.extend($scope, result);
					});
				}
			}
		}

		$scope.classify = function(lineChange, classification) {

		};

		setInterval(function() {
			if ($scope.classificationHasChanged) {
				$scope.classificationHasChanged = false;

				console.log("about to send classification");
			}
		}, 100);

		$scope.$watch('data.currentPage', function(newPage, oldPage) {
			if (newPage !== oldPage) {
				$state.transitionTo('project.classify', {name: $stateParams.name, page: $scope.data.currentPage}, {reload: true});
			}
		});

		$scope.setCurrentBug = function(bug) {
			$state.transitionTo('project.classify', {name: $stateParams.name, page: $scope.data.currentPage, bug: bug.key}, {reload: true});
		};
	});
	
	app.controller('ProjectBugsCtrl', function($scope, $state, $stateParams, bugPage, DiffService) {
		angular.extend($scope, bugPage);
		$scope.changedFiles = [];
		$scope.currentBug = null;
		$scope.bugKeyUrl = null;

		if ($stateParams.bug) {
			$scope.bugKeyUrl = $stateParams.bug;
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

		$scope.$watch('data.currentPage', function(newPage, oldPage) {
			if (newPage !== oldPage) {
				$state.transitionTo('project.bugs', {name: $stateParams.name, page: $scope.data.currentPage}, {reload: true});
			}
		});

		$scope.setCurrentBug = function(bug) {
			$state.transitionTo('project.bugs', {name: $stateParams.name, page: $scope.data.currentPage, bug: bug.key}, {reload: true});
		};
	});

	app.controller('TasksCtrl', function($scope, $timeout, Task) {
		var cancelled = false;
		function update() {
			$timeout(function() {
				if (cancelled) {
					return;
				}
				Task.query(function(data) {
					$scope.tasks = data;
					update();
				});
			}, 500);
		}
		$scope.$on("$destroy", function() {
			cancelled = true;
		});
		$scope.panelClass = function(state) {
			var classes = {
				FINISHED: 'success',
				FAILED: 'danger'
			};
			if (state in classes) {
				return 'panel panel-' + classes[state];
			}
			return 'panel panel-info';
		};
		$scope.glyphicon = function(state) {
			var icons = {
				INITIALIZING: 'wait',
				SCHEDULED: 'wait',
				RUNNING: 'play',
				FAILED: 'exclamation-sign',
				FINISHED: 'ok'
			};
			if (state in icons) {
				return 'glyphicon glyphicon-' + icons[state];
			}
			return '';
		};
		update();
	});

	app.controller('ClusterCtrl', function($scope, Restangular) {
		Restangular.all('clusters').getList().then(function(clusters) {
			$scope.clusters = clusters;
			clusters.forEach(function(cluster) {
				cluster.nodes = cluster.getList('nodes').$object;
			});
		});

		$scope.startNode = function(node) {
			node.customPOST({}, 'start');
		};
		$scope.stopNode = function(node) {
			node.customPOST({}, 'stop');
		};
	});

})(angular);
