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
