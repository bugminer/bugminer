app.controller('ProjectsCtrl', function($scope, Project) {
	Project.query(function(data) {
		$scope.projects = data;
	});
});
