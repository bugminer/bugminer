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
