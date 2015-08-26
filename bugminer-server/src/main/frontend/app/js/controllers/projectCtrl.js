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
