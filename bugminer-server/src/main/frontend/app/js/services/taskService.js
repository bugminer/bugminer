app.factory('Task', function($resource) {
	return $resource('/api/tasks/:name');
});
