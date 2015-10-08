app.controller('ProjectsAddCtrl', function($scope, Project) {

    $scope.add = function(project) {
        Project.save(project);
    };
});
