(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestListByUserController", ["$scope", "$location", "$rootScope", "$window", "$http", "PersonService", "RequestService", "FieldFactory",
            function ($scope, $location, $rootScope, $window,  $http, PersonService, RequestService, FieldFactory) {

                $scope.selectedCurrentManager = undefined;
                $scope.selectedUser = undefined;
                $scope.managers = [];
                $scope.users = [];

                $scope.order = FieldFactory.desc(FieldFactory.request.CREATE_TIME);
                $scope.requestFields = FieldFactory.request;

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
                $scope.placeholder = '';
                $scope.title= '';
                $scope.requestListByUserVisibility = true;


                $scope.assigned = false;
                var path = $location.path();

                if (path=="/secured/admin/request/assigned"){
                    $scope.assigned = true;
                    $scope.personType = "Employee";
                    $scope.placeholder = "Find assigned requests by manager name";
                    $scope.title = "Enter manager name to get all assigned requests";

                    $rootScope.sideBarActiveElem = "request-assigned";

                    $scope.updateManager = function() {
                        if($scope.selectedCurrentManager.length >= 2) {
                            console.log($scope.selectedCurrentManager);
                            $http({
                                method: 'GET',
                                url: '/api/person/managers/' +  $scope.selectedCurrentManager +
                                '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                            }).then(function successCallback(response) {
                                $scope.managers = response.data;
                                console.log($scope.managers);
                            }, function errorCallback(response) {
                            });
                        }
                    };

                    $scope.pageChanged = function() {
                        if ($scope.selectedCurrentManager!=undefined)
                            $http({
                                method: 'GET',
                                url: '/api/request/list/assigned/' +  $scope.selectedCurrentManager.id +
                                '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize + "&sort=" + $scope.order
                            }).then(function (response) {
                                $scope.requests = [];
                                $scope.requests = response.data.data;
                                $scope.totalItems = response.data.totalElements;
                                $scope.requestListByUserVisibility = false;


                            }, function errorCallback(response) {
                                swal("Wrong manager name", "Manager with this name no exist", "error");
                            });
                    };

                    $scope.getTotalPage = function(){
                        return $scope.totalItems;
                    };

                    $scope.getTotalPage(); //
                    $scope.pageChanged(1); // get first page

                } else {

                    $scope.personType = "Manager";
                    $scope.placeholder = "Find all requests by user name";
                    $scope.title = "Enter user name to get all user requests";

                    $rootScope.sideBarActiveElem = "request-user";

                    $scope.updateUser = function() {
                        if($scope.selectedUser.length >= 2) {
                            console.log($scope.selectedUser);
                            $http({
                                method: 'GET',
                                url: '/api/person/users/' +  $scope.selectedUser +
                                '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                            }).then(function successCallback(response) {
                                $scope.users = response.data;
                                console.log($scope.users);
                            }, function errorCallback(response) {
                            });
                        }
                    };

                    $scope.pageChanged = function() {
                        if ($scope.selectedUser != undefined)
                        $http({
                            method: 'GET',
                            url: '/api/request/list/user/' + $scope.selectedUser.id +
                            '?page=' + $scope.currentPage + '&size=' + $scope.pageSize + "&sort=" + $scope.order
                        }).then(function (response) {
                            $scope.requests = [];
                            $scope.requests = response.data.data;
                            $scope.totalItems = response.data.totalElements;

                            $scope.requestListByUserVisibility = false;
                        }, function errorCallback(response) {
                            swal("Wrong user name", "User with this name no exist", "error");
                        });
                    };

                    $scope.requestDetails = function (requestId) {
                        window.location = requestDetails + requestId;
                    };

                    $scope.getTotalPage = function(){
                        return $scope.totalItems;
                    };

                    $scope.getTotalPage(); //
                    $scope.pageChanged(1); // get first page

                    $scope.requestUpdate = function(requestId) {
                        window.location = requestDetails + requestId + '/update';
                    };
                }


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

                $scope.requestDetails = function (requestId) {
                    window.location = requestDetails + requestId;
                };

                $scope.isUndefined = function (thing) {
                    return (typeof thing === "undefined");
                };

                $scope.isAdmin = function (thing) {
                    return currentUser.role === 'ROLE_ADMINISTRATOR';
                };

                $scope.selectRequest = function (requestId) {
                    $scope.selectedRequest = requestId;
                };

                $scope.goToRequestDetailsPage = function (requestId) {
                    $window.open("/secured/employee/request/" + requestId + "/details", '_blank');
                };

                $scope.notifyAboutExpiringEstimateTime = function() {
                    return RequestService.notifyAboutExpiringEstimateTime();
                };
            }])
})();
