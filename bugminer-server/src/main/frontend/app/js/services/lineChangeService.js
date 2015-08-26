app.factory('LineChange', function($resource, $stateParams) {
	return $resource('/api/projects/:name/bugs/:tracker/:key/diff', {name: $stateParams.name});
});
