(function () {
    angular.module("OfficeManagementSystem")
        .controller("SubController", ["$scope","$routeParams", "SubService",
            function ($scope, $routeParams, SubService) {
                var requestId = $routeParams.requestId;

                $scope.subs = [];
                $scope.tempSubs = [];
                $scope.statuses = [];
                $scope.priorities = [];
                $scope.newSub = {
                    name: "",
                    priority: 2
                };
                $scope.validationError = {
                    newSubTittle:false,
                    editSubTittle:false
                };

                SubService.getStatuses().then(function (response) {
                    if (response.isError == false){
                        $scope.statuses = response.data;
                    }
                });

                $scope.getPriorityName = function (id) {
                    var priority = "";
                    angular.forEach($scope.priorities, function (obj) {
                        if (obj.id == id){
                            priority = obj.name;
                        }
                    });
                    return priority;
                };

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
                  if ($scope.newSub.name==""||$scope.newSub.name.length<3){
                      $scope.validationError.newSubTittle = true;
                      return;
                  }
                  SubService.addSubRequest($scope.newSub, requestId).then(function (response) {
                      if (response.isError == false){
                          $scope.subs.push(response.sub);
                          $scope.newSub = {
                              priority: 1
                          };
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
                };

                $scope._toTempSub = function (sub) {
                    var mObj = JSON.parse(JSON.stringify(sub));
                    $scope.tempSubs.push(mObj);
                };

                $scope._fromTempSub = function (sub) {
                    angular.forEach($scope.tempSubs, function (obj) {
                        if (obj.id == sub.id){
                            $scope.subs[$scope.subs.indexOf(sub)]=obj;
                            obj.showEdit = false;
                            var i = $scope.tempSubs.indexOf(obj);
                            if(i != -1) {
                                $scope.tempSubs.splice(i, 1);
                            }
                        }
                    });
                };

                $scope.updateSub = function (sub) {
                    angular.forEach($scope.tempSubs, function (obj) {
                        if (obj.id == sub.id){
                            var i = $scope.tempSubs.indexOf(obj);
                            if(i != -1) {
                                $scope.tempSubs.splice(i, 1);
                            }
                        }
                    });
                    $scope._toggleEdit(sub);
                };

                $scope.goEdit = function (sub) {
                    $scope._toTempSub(sub);
                    $scope._toggleEdit(sub);
                };

                $scope.resetEdit = function (sub) {
                    $scope._fromTempSub(sub);
                };

                $scope.showNewSubForm = false;

                $scope.toggleNewSub = function () {
                    $scope.showNewSubForm =  $scope.showNewSubForm?false:true;
                };

            }])
})();