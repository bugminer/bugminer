(function(angular) {
	'use strict';
	
	var app = angular.module('bugminerApp', ['ngResource', 'ui.router', 'ui.bootstrap']);
	
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
			});
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

			//// fetch line changes
			//LineChange.query({tracker: bug.issueTracker.name, key: bug.key}, function(data) {
			//	result.changedFiles = [];
			//	var lineChanges = data;
			//	var currentFileName = '';
			//	var currentFile = null;
			//	var currentEdit = {
			//		additions: [],
			//		deletions: [],
			//		lastLineNumber: 0
			//	};
			//
			//	for (var i = 0; i < lineChanges.length; i++) {
			//		if (currentFileName !== lineChanges[i].fileName) {
			//			currentFileName = lineChanges[i].fileName;
			//
			//			currentFile = {
			//				name: currentFileName,
			//				edits: [currentEdit]
			//			};
			//
			//			result.changedFiles.push(currentFile);
			//		}
			//
			//		if (lineChanges[i].oldLineNumber > currentEdit.lastLineNumber + 1) {
			//			currentEdit = {
			//				additions: [],
			//				deletions: [],
			//				lastLineNumber: 0
			//			};
			//
			//			currentFile.edits.push(currentEdit);
			//		}
			//
			//		lineChanges[i].edit = currentEdit;
			//		lineChanges[i].file = currentFile;
			//
			//		if (lineChanges[i].kind === 'ADDITION') {
			//			currentEdit.additions.push(lineChanges[i]);
			//			currentEdit.lastLineNumber = lineChanges[i].oldLineNumber;
			//		} else {
			//			currentEdit.deletions.push(lineChanges[i]);
			//			currentEdit.lastLineNumber = lineChanges[i].oldLineNumber;
			//		}
			//	}
			//
			//	callback(result);
			//});
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

	app.controller('ProjectClassifyCtrl', function($scope, $location, bugPage, DiffService) {
		angular.extend($scope, bugPage);
		$scope.changedFiles = [];
		$scope.currentBug = null;
		$scope.bugKeyUrl = null;
		$scope.classificationHasChanged = false;

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

		$scope.classify = function(lineChange, classification) {

		}

		setInterval(function() {
			if ($scope.classificationHasChanged) {
				$scope.classificationHasChanged = false;

				console.log("about to send classification");
			}
		}, 100);

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

})(angular);
