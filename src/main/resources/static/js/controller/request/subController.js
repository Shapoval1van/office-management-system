(function () {
    angular.module("OfficeManagementSystem")
        .controller("SubController", ["$scope","$routeParams", "SubService",
            function ($scope, $routeParams, SubService) {
                var requestId = $routeParams.requestId;

                $scope.subs = [
                    {},
                    {}
                ];

                var tempSub = {};

                angular.forEach($scope.employees, function (obj) {
                    obj["showEdit"] = false;
                });

                $scope._toggleEdit = function (sub) {
                    sub.showEdit = sub.showEdit ? false : true;
                };

                $scope._toTempSub = function (sub) {
                  tempSub.showEdit = sub.showEdit;
                };

                $scope._fromTempSub = function (sub) {
                    sub.showEdit = tempSub.showEdit;
                };

                $scope.updateSub = function (sub) {

                    $scope._toggleEdit(sub);
                };

                $scope.deleteSub = function (sub) {

                };

                $scope.goEdit = function (sub) {
                    $scope._toTempSub(sub);

                    $scope._toggleEdit(sub);
                };

                $scope.resetEdit = function (sub) {
                    $scope._toTempSub(sub);
                    $scope._toggleEdit(sub);
                };

                $scope.showNewSubForm = false;

                $scope.toggleNewSub = function () {
                    $scope.showNewSubForm =  $scope.showNewSubForm?false:true;
                };

            }])
})();