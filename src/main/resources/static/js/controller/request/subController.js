(function () {
    angular.module("OfficeManagementSystem")
        .controller("SubController", ["$scope","$routeParams", "SubService",
            function ($scope, $routeParams, SubService) {
                var requestId = $routeParams.requestId;
                $scope.subs = [];
                $scope.formMode = "showing";

                $scope.showNewSubtaskForm = function () {
                    $scope.formMode = "adding";
                };

                $scope.closeNewSubtaskForm = function () {
                    $scope.formMode = "showing";
                };

            }])
})();