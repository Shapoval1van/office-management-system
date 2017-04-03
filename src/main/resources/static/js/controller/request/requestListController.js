(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestListController", ["$scope", "$location", "PersonService", "RequestService", "FieldFactory",
            function ($scope, $location, PersonService, RequestService, FieldFactory) {

                $scope.selectedManager;
                $scope.managers = [];

                var requestDetails = "/secured/employee/request/";

                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.personType = "";
                $scope.pageSize = 10;
                $scope.requests = {};
                $scope.priorities = [{priorityId: 4, name: 'ALL'},
                    {priorityId: 1, name: 'HIGH'},
                    {priorityId: 2, name: 'NORMAL'},
                    {priorityId: 3, name: 'LOW'}]; // TODO need controller for priorities
                $scope.maxSize = 5;
                $scope.totalItems = 0;
                $scope.currentPage = 1;
                $scope.selectedPriority = $scope.priorities[0];
                $scope.assignedMessage = '';
                $scope.selectedRequest = -1;
                $scope.requestListVisibility = true;

                $scope.order = FieldFactory.desc(FieldFactory.request.CREATE_TIME);
                $scope.requestFields = FieldFactory.request;

                $scope.orderRequests = function (fieldName) {
                    if (FieldFactory.isDescOrder($scope.order, fieldName))
                        $scope.order = FieldFactory.removeSortField($scope.order, fieldName);
                    else
                        $scope.order = FieldFactory.toggleOrder($scope.order, fieldName);
                    return $scope.pageChanged();
                };

                $scope.isDescOrder = function (fieldName) {
                    return FieldFactory.isDescOrder($scope.order, fieldName);
                };

                $scope.isAscOrder = function (fieldName) {
                    return FieldFactory.isAscOrder($scope.order, fieldName);
                };

                $scope.orderRequestsByName = function () {
                    return $scope.orderRequests(FieldFactory.request.NAME);
                };

                $scope.sortRequestsByEstimate = function () {
                    return $scope.orderRequests(FieldFactory.request.ESTIMATE);
                };

                $scope.sortRequestsByPriority = function () {
                    return $scope.orderRequests(FieldFactory.request.PRIORITY);
                };

                $scope.sortRequestsByCreatingTime = function () {
                    return $scope.orderRequests(FieldFactory.request.CREATE_TIME);
                };

                $scope.sortRequestsByStatus = function () {
                    return $scope.orderRequests(FieldFactory.request.STATUS);
                };

                $scope.my = false;
                var path = $location.path();
                if (path.toString()==="/secured/employee/request/my"){

                    $scope.my = true;
                    $scope.personType = "Manager";

                    $scope.pageChanged = function() {
                        RequestService.getAllRequestByEmployee($scope.currentPage, $scope.pageSize, $scope.order)
                            .then(function (response) {
                                $scope.requests = [];
                                $scope.requests = response.data.data;
                                $scope.totalItems = response.data.totalElements;
                                if(!$scope.requests.length){
                                    $scope.requestListVisibility = false;
                                }
                            }, function errorCallback(response) {
                            });
                    };

                    $scope.requestUpdate = function(requestId) {
                        window.location = requestDetails + requestId + '/update';
                    };

                    $scope.isFree = function(request) {
                        return request.status.name === "FREE";
                    };

                    $scope.requestDetails = function (requestId) {
                        window.location = requestDetails + requestId;
                    };

                    $scope.getTotalPage = function(){
                        return $scope.totalItems;
                    };

                    $scope.getTotalPage();
                    $scope.pageChanged();

                } else {
                    $scope.personType = "Employee";

                    $scope.pageChanged = function() {
                        if($scope.selectedPriority.priorityId==4){
                            RequestService.getAvailableRequest($scope.currentPage, $scope.pageSize, $scope.order)
                                .then(function (response) {
                                    $scope.requests = [];
                                    $scope.requests = response.data.data;
                                    $scope.totalItems = response.data.totalElements;
                                }, function errorCallback(response) {
                                });
                        }
                        else {
                            RequestService.getAvailableRequestByPriority($scope.selectedPriority.priorityId,
                                $scope.currentPage, $scope.pageSize, $scope.order)
                                .then(function (response) {
                                    $scope.requests = [];
                                    $scope.requests = response.data.data;
                                    $scope.totalItems = response.data.totalElements;
                                }, function errorCallback(response) {
                                });
                        }
                    };

                    $scope.getTotalPage = function(){
                        return $scope.totalItems;
                    };

                    $scope.getTotalPage();
                    $scope.pageChanged();

                    $scope.priorityChange = function (priorityId) {
                        $scope.getTotalPage();
                        $scope.pageChanged();
                    };
                }


                $scope.isAssigned = function (request) {
                    return RequestService.isAssigned(request);
                };

                $scope.isUndefined = function (thing) {
                    return (typeof thing === "undefined");
                };

                $scope.isAdmin = function () {
                    return PersonService.isAdministrator(currentUser.role);
                };

                $scope.isManager = function () {
                    return PersonService.isManager(currentUser.role);
                };

                $scope.assignToMe = function (requestId) {
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to assign this request",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, assign!",
                            closeOnConfirm: false
                        },
                        function(){
                            PersonService.assignToMe(requestId)
                                .then(function (response) {
                                    $scope.requests = response.data;
                                    swal("Request assigned!", "Request successful assigned", "success");
                                    $scope.pageChanged();
                                }, function (response) {
                                    swal("Assigning Failure!", response.data.errors, "error");
                                });
                        });
                };

                $scope.assignToSmb = function () {
                    return PersonService.assign($scope.selectedRequest, $scope.selectedManager.id)
                        .then(function (response) {
                            $scope.assignedMessage = response.data.message;
                            swal("Request assigned!", "Request successful assigned", "success");
                            $scope.pageChanged();
                        }, function (response) {
                            $scope.assignedMessage = response.data.errors;
                            swal("Assigning Failure!", response.data.errors, "error");
                        });
                };

                $scope.unassign = function(requestId) {
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want unassign manager from this request",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, unasigne!",
                            closeOnConfirm: false
                        },
                        function(){
                            RequestService.unassign(requestId)
                                .then(function (callback) {
                                    $scope.requests = callback.data;
                                    swal("Request unassigned!", "", "success");
                                    $scope.pageChanged();
                                }, function (error) {
                                    console.log(error);
                                });
                        });
                };

                $scope.requestDelete = function(requestId) {
                    swal({
                        title: "Are you sure?",
                        text: "Do you really want to cancel this request",
                        type: "warning",
                        showCancelButton: true,
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: "Yes, cancel it!",
                        closeOnConfirm: false
                    },
                        function(){
                        RequestService.cancelRequest(requestId)
                        .then(function (callback) {
                            $scope.requests = callback.data;
                            swal("Request canceled!", "", "success");
                            $scope.pageChanged();
                        }, function (error) {
                            console.log(error);
                        });
                    });
                };

                $scope.update = function () {
                    //TODO: Change page number and page size
                    return PersonService.searchManagerByName($scope.selectedManager, 1, 20)
                        .then(function (callback) {
                            $scope.managers = callback.data;
                        }, function () {
                            console.log("Failure");
                        })
                };

                $scope.selectRequest = function (requestId) {
                    $scope.selectedRequest = requestId;
                };

                $scope.goToRequestDetailsPage = function (requestId) {
                    $scope.goToUrl("/secured/employee/request/" + requestId + "/details");
                };

                $scope.notifyAboutExpiringEstimateTime = function() {
                    return RequestService.notifyAboutExpiringEstimateTime();
                };
            }])
})();
