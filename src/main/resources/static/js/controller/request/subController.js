(function () {
    angular.module("OfficeManagementSystem")
        .controller("SubController", ["$scope","$routeParams", "SubService",
            function ($scope, $routeParams, SubService) {
                var requestId = $routeParams.requestId;

                $scope.subs = [];
                $scope.statuses = [];
                $scope.priorities = [];
                $scope.newSub = {};

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


                $scope.addSub = function () {
                  console.log($scope.newSub) ;
                  SubService.addSubRequest($scope.newSub, requestId).then(function (response) {
                      if (response.isError == false){
                          $scope.subs.push(response.sub);
                          $scope.newSub = {};
                      }
                  });
                };

                $scope.deleteSub = function (sub) {
                    SubService.deleteSubRequest(sub.id, requestId).then(function (response) {
                        if (response.isError == false){
                            var i = $scope.subs.indexOf(sub);
                            if(i != -1) {
                                $scope.subs.splice(i, 1);
                            }
                        }
                    });
                };


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