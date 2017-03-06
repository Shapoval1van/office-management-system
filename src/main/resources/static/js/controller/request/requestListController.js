(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestListController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {

                $scope.selectedManager = undefined;
                $scope.managers = [];

                var requestDetails = "/request/";
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
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

                $scope.pageChanged = function() {
                    $http({
                        method: 'GET',
                        url: '/api/request/available/' + $scope.selectedPriority.priorityId +
                        '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                    }).then(function successCallback(response) {
                        $scope.requests = response.data;
                    }, function errorCallback(response) {
                    });
                };

                $scope.getTotalPage(); //
                $scope.pageChanged(1); // get first page

                $scope.requestDetails = function(requestId) {
                    window.location = requestDetails + requestId;
                };

                $scope.isSelected = function(requestId) {
                    return requestId === $scope.selectedPriority.priorityId;
                };

                $scope.priorityChange = function(priorityId) {
                    $scope.getTotalPage(); //
                    $scope.pageChanged(1); // get first page
                };

                $scope.assignToMe = function(requestId) {
                    $http({
                        method: 'POST',
                        url: '/api/request/assignRequest',
                        data: {
                            'requestId': requestId,
                            'personId': currentUser.id
                        }
                    }).then(function successCallback(response) {
                        $scope.assignedMessage = response.data.message;
                    }, function errorCallback(response) {
                        $scope.assignedMessage = response.data.errors
                            .map(function(e) {return e.detail})
                            .join('. ');
                    });
                };

                $scope.assign = function() {
                    $http({
                        method: 'POST',
                        url: '/api/request/assignRequest',
                        data: {
                            'requestId': $scope.selectedRequest,
                            'personId': $scope.selectedManager.id
                        }
                    }).then(function successCallback(response) {
                        $scope.assignedMessage = response.data.message;
                    }, function errorCallback(response) {
                        $scope.assignedMessage = response.data.errors
                            .map(function(e) {return e.detail})
                            .join('. ');
                    });
                };

                $scope.update = function() {
                    if($scope.selectedManager.length >= 2) {
                        console.log($scope.selectedManager);

                        $http({
                            method: 'GET',
                            url: '/api/person/managers/' +  $scope.selectedManager +
                            '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                        }).then(function successCallback(response) {
                            $scope.managers = response.data;
                        }, function errorCallback(response) {
                        });
                    }
                };

                $scope.selectRequest = function(requestId) {
                    $scope.selectedRequest = requestId;
                };

            }])
})();
