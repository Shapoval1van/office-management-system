(function () {
    angular.module("OfficeManagementSystem")
        .controller("SubController", ["$scope","$routeParams", "SubService",
            function ($scope, $routeParams, SubService) {
                var requestId = $routeParams.requestId;

                $scope.subs = [
                    {},
                    {}
                ];

                angular.forEach($scope.employees, function (obj) {
                    obj["showEdit"] = false;
                });

                $scope._toggleEdit = function (sub) {
                    sub.showEdit = sub.showEdit ? false : true;
                };

                $scope.updateSub = function (sub) {

                    $scope._toggleEdit(sub);
                };

                $scope.deleteSub = function (sub) {

                };
                
                $scope.goEdit = function (sub) {

                    $scope._toggleEdit(sub);
                };

                $scope.resetEdit = function (sub) {

                    $scope._toggleEdit(sub);
                };

                $scope.formMode = "showing";

                $scope.showNewSubtaskForm = function () {
                    $scope.formMode = "adding";
                };

                $scope.closeNewSubtaskForm = function () {
                    $scope.formMode = "showing";
                };

            }])
})();