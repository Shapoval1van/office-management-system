(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestListController", ["$scope", "$location", "$rootScope", "PersonService", "RequestService",
            function ($scope, $location, $rootScope, PersonService, RequestService) {

                $scope.selectedManager;
                $scope.managers = [];

                var requestDetails = "/secured/request/";
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

                $scope.my = false;
                var path = $location.path();
                if (path.toString()=="/secured/request/my"){

                    $rootScope.sideBarActiveElem = "my-requests";

                    $scope.my = true;
                    $scope.personType = "Manager";

                    $scope.pageChanged = function() {
                        RequestService.getAllRequestByEmployee($scope.currentPage, $scope.pageSize)
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

                    $scope.requestDetails = function (requestId) {
                        window.location = requestDetails + requestId;
                    };

                    $scope.getTotalPage = function(){
                        return $scope.totalItems;
                    };

                    $scope.getTotalPage(); //
                    $scope.pageChanged(1); // get first page

                } else {
                    $scope.personType = "Employee";

                    $rootScope.sideBarActiveElem = "free-requests";

                    $scope.pageChanged = function() {
                        if($scope.selectedPriority.priorityId==4){
                            RequestService.getAvailableRequest($scope.currentPage, $scope.pageSize)
                                .then(function (response) {
                                    $scope.requests = [];
                                    $scope.requests = response.data.data;
                                    $scope.totalItems = response.data.totalElements;
                                }, function errorCallback(response) {
                                });
                        }
                        else {
                            RequestService.getAvailableRequestByPriority($scope.selectedPriority.priorityId,
                                $scope.currentPage, $scope.pageSize)
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

                    $scope.getTotalPage(); //
                    $scope.pageChanged(1); // get first page

                    $scope.priorityChange = function (priorityId) {
                        $scope.getTotalPage(); //
                        $scope.pageChanged(1); // get first page
                    };

                }



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
                    return PersonService.assignToMe(requestId)
                        .then(function (response) {
                            $scope.assignedMessage = response.data.message;
                        }, function (response) {
                            $scope.assignedMessage = response.data.errors
                                .map(function (e) {
                                    return e.detail
                                })
                                .join('. ');
                        });
                };

                $scope.assignToSmb = function (requestId) {
                    return PersonService.assign(requestId, $scope.selectedManager.id)
                        .then(function (response) {
                            $scope.assignedMessage = response.data.message;
                        }, function (response) {
                            $scope.assignedMessage = response.data.errors
                                .map(function (e) {
                                    return e.detail
                                })
                                .join('. ');
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
                        }, function (error) {
                            console.log(error);
                        });

                        swal("Request canceled!", "", "success");
                        window.setTimeout(function(){
                            location.reload();}, 2000)
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
                    $scope.goToUrl("/secured/request/" + requestId + "/details");
                };

                $scope.notifyAboutExpiringEstimateTime = function() {
                    return RequestService.notifyAboutExpiringEstimateTime();
                };
            }])
})();
