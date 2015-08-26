app.factory('BugPage', function($resource) {
	return $resource('/api/projects/:name/bugs', {sort: 'reportTime', size: 10});
});
