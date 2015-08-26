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
				$scope.currentBug = bug;

				DiffService.computeDiff(bug, function(result) {
					angular.extend($scope, result);
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
