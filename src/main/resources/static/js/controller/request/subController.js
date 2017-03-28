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
                    priority: 2,
                    estimate: ""
                };
                $scope.validationError = {
                    newSubTittle:false,
                    editSubTittle:false
                };
                $scope.showNewSubForm = false;
                $scope.statusComparator = function (v1, v2) {
                    if (v1.value == 3) {
                      return 1;
                  } else {
                      return -1;
                  }
                };
                $scope.showFinished = true;
                $scope.dateOptions = {
                    format: "DD.MM.YYYY HH:mm",
                    daysOfWeekDisabled: [0,6],
                    minDate: new Date(),
                    useCurrent: false
                };

                var showErrorMessage = function (text) {
                    text = text?text:"";
                    swal({
                        title: "Error",
                        text: text,
                        type: "error",
                        confirmButtonText: "Close"
                    });
                };

                $scope.getStatusName = function (id) {
                    var status = "";
                    angular.forEach($scope.statuses, function (obj) {
                        if (obj.id == id){
                            status = obj.name;
                        }
                    });
                    return status;
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
                          $scope.subs.unshift(response.sub);
                          $scope.newSub = {
                              name: "",
                              priority: 2
                          };
                      } else {
                          if (response.data!=null && response.data.errors[0]){
                              showErrorMessage(response.data.errors[0].detail);
                          } else {
                              showErrorMessage();
                          }
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
                        } else {
                            if (response.data!=null && response.data.errors[0]){
                                showErrorMessage(response.data.errors[0].detail);
                            } else {
                                showErrorMessage();
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
                    console.log(sub);
                    if (sub.name==""||sub.name.length<3){
                        $scope.validationError.editSubTittle = true;
                        return;
                    }


                    sub.status = sub.shownStatus;
                    SubService.updateSubRequest(sub.id, sub, requestId).then(function (response) {
                        if (response.isError == false){
                            angular.forEach($scope.tempSubs, function (obj) {
                                if (obj.id == sub.id){
                                    var i = $scope.tempSubs.indexOf(obj);
                                    if(i != -1) {
                                        $scope.tempSubs.splice(i, 1);
                                    }
                                }
                            });
                            $scope.subs[$scope.subs.indexOf(sub)] = response.sub;
                        } else {
                            if (response.data!=null && response.data.errors[0]){
                                showErrorMessage(response.data.errors[0].detail);
                            } else {
                                showErrorMessage();
                            }
                        }
                    });
                };

                $scope.finishedFilter = function (showFinished, sub) {
                    if (showFinished == true){
                        return true;
                    } else if (sub.status == 3){
                        return false;
                    }
                    return true;
                }

                $scope.onStatusChange = function (sub) {
                    if (!sub.showEdit){
                        $scope.updateSub(sub);
                    }
                };

                $scope.goEdit = function (sub) {
                    $scope._toTempSub(sub);
                    $scope._toggleEdit(sub);
                };

                $scope.resetEdit = function (sub) {
                    $scope._fromTempSub(sub);
                };

                $scope.toggleNewSub = function () {
                    $scope.showNewSubForm =  $scope.showNewSubForm?false:true;
                    $scope.newSub = {
                        name: "",
                        priority: 2,
                        estimate: ""
                    };
                };

                $scope.$watch('request.status', function (newValue, oldValue, scope) {
                    SubService.getSubRequests(requestId).then(function (data) {
                        if (data.isError == false){
                            $scope.subs = data.data;
                        }
                    });
                });

            }])
})();