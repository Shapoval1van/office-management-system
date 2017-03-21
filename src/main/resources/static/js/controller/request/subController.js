(function () {
    angular.module("OfficeManagementSystem")
        .controller("SubController", ["$scope","$routeParams", "SubService",
            function ($scope, $routeParams, SubService) {
                var requestId = $routeParams.requestId;

                $scope.subs = [];
                $scope.statuses = [];
                $scope.priorities = [];

                var tempSub = {};

                SubService.getStatuses().then(function (response) {
                    if (response.isError == false){
                        $scope.statuses = response.data;
                    }
                });

                SubService.getPriorities().then(function (response) {
                    if (response.isError == false){
                        $scope.priorities = response.data;
                    }
                });

                SubService.getSubRequests(requestId).then(function (data) {
                    if (data.isError == false){
                        $scope.subs = data.data;
                    }
                });


                $scope._toggleEdit = function (sub) {
                    sub.showEdit = !sub.showEdit;
                    console.log(sub);
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