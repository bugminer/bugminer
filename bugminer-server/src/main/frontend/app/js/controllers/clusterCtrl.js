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
