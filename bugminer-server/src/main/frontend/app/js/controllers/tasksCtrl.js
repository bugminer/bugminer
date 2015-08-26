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
