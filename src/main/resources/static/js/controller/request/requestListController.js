(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestListController", ["$scope", "$http", "$location", "$routeParams", "PersonService", "RequestService",
            function ($scope, $http, $location,  $routeParams, PersonService, RequestService) {

                $scope.selectedManager;
                $scope.managers = [];


                var requestDetails = "/request/";
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.personType = "";
                $scope.pageSize = 10;
                $scope.requests = {};
                $scope.priorities = [{priorityId: 1, name: 'HIGH'},
                    {priorityId: 2, name: 'NORMAL'},
                    {priorityId: 3, name: 'LOW'},
                    {priorityId: 4, name: 'ALL'}]; // TODO need controller for priorities
                $scope.maxSize = 5;
                $scope.totalItems = 0;
                $scope.currentPage = 1;
                $scope.selectedPriority = $scope.priorities[0];
                $scope.assignedMessage = '';
                $scope.selectedRequest = -1;

                $scope.my = false;
                var path = $location.path();
                if (path.toString()=="/request/my"){

                    $scope.my = true;
                    $scope.personType = "Manager";

                    $scope.pageChanged = function() {
                        $http({
                            method: 'GET',
                            url: '/api/request/requestListByEmployee/' +
                            '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                        }).then(function successCallback(response) {
                            $scope.requests = [];
                            $scope.requests = response.data;

                        }, function errorCallback(response) {
                        });
                    };
                } else {
                    $scope.personType = "Employee";

                    $scope.pageChanged = function() {
                        $http({
                            method: 'GET',
                            url: '/api/request/available/' + $scope.selectedPriority.priorityId +
                            '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                        }).then(function successCallback(response) {
                            $scope.requests = [];
                            $scope.requests = response.data;
                        }, function errorCallback(response) {
                        });
                    };
                }




                $scope.isUndefined = function (thing) {
                    return (typeof thing === "undefined");
                };

                $scope.isAdmin = function (thing) {
                    return currentUser.role === 'ROLE_ADMINISTRATOR';
                };

                $scope.getTotalPage = function() {
                    $http({
                        method: 'GET',
                        url: '/api/request/count/' + $scope.selectedPriority.priorityId
                    }).then(function successCallback(response) {
                        $scope.totalItems = response.data;
                    }, function errorCallback(response) {
                    });
                };

                $scope.requestUpdate = function(requestId) {
                    window.location = requestDetails + requestId + '/update';
                };


                $scope.getTotalPage(); //
                $scope.pageChanged(1); // get first page

                $scope.requestDetails = function (requestId) {
                    window.location = requestDetails + requestId;
                };

                $scope.isSelected = function (requestId) {
                    return requestId === $scope.selectedPriority.priorityId;
                };

                $scope.priorityChange = function (priorityId) {
                    $scope.getTotalPage(); //
                    $scope.pageChanged(1); // get first page
                };

                $scope.assignToMe = function (requestId) {
                    return PersonService.assign(requestId, currentUser.id)
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

                $scope.assignToSmb = function () {
                    return PersonService.assign($scope.request.id, $scope.selectedManager.id)
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

                // $scope.requestDelete = function(requestId) {
                //     RequestService.cancelRequest(requestId)
                // };

                $scope.requestDelete = function(requestId) {
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to cancel this request",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, cancel it!",
                            closeOnConfirm: false},
                        function(){
                            $http({
                                method: 'DELETE',
                                url: '/api/request/' + requestId + '/delete'
                            }).then(function successCallback(response) {
                                $scope.requests = response.data;
                            }, function errorCallback(error) {
                                swal("Cancel Failure!", error.data.errors[0].detail, "error");
                                console.log(error);
                            });

                            swal("Request canceled!", "", "success");
                            window.setTimeout(function(){
                                location.reload()}, 1000)
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
                    $scope.goToUrl("/request/" + requestId + "/details");
                }
            }])
})();
