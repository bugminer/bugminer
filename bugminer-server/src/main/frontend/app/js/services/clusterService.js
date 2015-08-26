app.factory('Cluster', function($resource) {
	return $resource('/api/clusters/:name');
});
