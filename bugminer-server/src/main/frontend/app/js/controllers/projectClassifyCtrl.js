app.controller('ProjectClassifyCtrl', function($scope, $state, $stateParams, bugPage, DiffService) {
	angular.extend($scope, bugPage);
	$scope.changedFiles = [];
	$scope.currentBug = null;
	$scope.bugKeyUrl = null;
	$scope.classificationHasChanged = false;
	$scope.classification = {
		'bug': null,
		'user': null,
		'lineChangeClassifications': []
	};

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

	$scope.classify = function(change, classificationForm) {
		console.log('in classify');
		var elementFound = false;
		var lineChange;

		if (change.deletion !== null) {
			lineChange = change.deletion;
		} else {
			lineChange = change.addition;
		}

		var newLineChangeClassification = {
			'lineChange': lineChange,
			'classification': null,
			'lineChangeClassificationForm': classificationForm
		};

		for (var i = 0; i < $scope.classification.lineChangeClassifications.length; i++) {
			var lineChangeClassification = $scope.classification.lineChangeClassifications[i];

			if (lineChangeClassification.lineChange.oldLineNumber == lineChange.oldLineNumber) {
				elementFound = true;
				$scope.classification.lineChangeClassifications[i] = newLineChangeClassification;
			}
		}

		if (!elementFound) {
			$scope.classification.lineChangeClassifications.push(newLineChangeClassification);
		}

		console.log($scope.classification);
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
