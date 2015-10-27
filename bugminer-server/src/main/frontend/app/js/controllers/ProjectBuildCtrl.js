app.controller('ProjectBuildCtrl', function($scope, $state, $stateParams, Restangular, Project) {
	$scope.state = $state.current.name;

	Project.get({name: $stateParams.name}, function(project) {
		$scope.project = project;
	});

	Restangular.all('clusters').getList().then(function(clusters) {
		$scope.clusters = clusters;
		clusters.forEach(function(cluster) {
			cluster.nodes = cluster.getList('nodes').$object;
		});
	});

	$scope.build = function() {
		if (!$scope.revision) {
			alert('Revision is required');
			return;
		}

		if (!$scope.node) {
			alert('Node is required');
			return;
		}

		$scope.project.$build({revision: $scope.revision, node: $scope.node});
	};
});
