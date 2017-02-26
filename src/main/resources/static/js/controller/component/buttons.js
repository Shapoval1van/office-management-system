angular.module("OfficeManagementSystem").controller('ButtonsCtrl', function ($scope) {

    $scope.checkModel = {
        'low-priority': false,
        'high-priority': true,
        'mandatory-priority': false
    };

    $scope.checkResults = [];

    $scope.$watchCollection('checkModel', function () {
        $scope.checkResults = [];
        angular.forEach($scope.checkModel, function (value, key) {
            if (value) {
                $scope.checkResults.push(key);
            }
        });
    });
});