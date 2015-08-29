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
	$scope.sendClassificationFn = debounce(function() {
		console.log('about to send classification');
	}, 500);

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

		$scope.sendClassificationFn();
	};

	$scope.$watch('data.currentPage', function(newPage, oldPage) {
		if (newPage !== oldPage) {
			$state.transitionTo('project.classify', {name: $stateParams.name, page: $scope.data.currentPage}, {reload: true});
		}
	});

	$scope.setCurrentBug = function(bug) {
		$state.transitionTo('project.classify', {name: $stateParams.name, page: $scope.data.currentPage, bug: bug.key}, {reload: true});
	};

	function debounce(func, wait, immediate) {
		var timeout;
		return function() {
			var context = this, args = arguments;
			var later = function() {
				timeout = null;
				if (!immediate) func.apply(context, args);
			};
			var callNow = immediate && !timeout;
			clearTimeout(timeout);
			timeout = setTimeout(later, wait);
			if (callNow) func.apply(context, args);
		};
	}
});
