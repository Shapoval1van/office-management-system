(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestListByEmployeeController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {

                var requestDetails = "/request/";
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.pageSize = 10;
                $scope.requests = {};

                $scope.maxSize = 5;
                $scope.totalItems = 0;
                $scope.currentPage = 1;
                $scope.employeeName = currentUser.firstName + ' ' + currentUser.lastName;

                $scope.selectedRequest = -1;

                $scope.getTotalPage = function() {
                    $http({
                        method: 'GET',
                        url: '/api/request/countAllRequestByEmployee'
                    }).then(function successCallback(response) {
                        $scope.totalItems = response.data;
                    }, function errorCallback(response) {
                    });
                };

                $scope.pageChanged = function() {
                    $http({
                        method: 'GET',
                        url: '/api/request//requestListByEmployee/' +
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

                $scope.requestUpdate = function(requestId) {
                    window.location = requestDetails + requestId + '/update';
                };

                $scope.requestDelete = function(requestId) {
                    window.location = requestDetails + requestId + '/delete';
                };

                $scope.propertyName = 'name';
                $scope.reverse = false;

                $scope.sortBy = function(propertyName) {
                    $scope.reverse = ($scope.propertyName === propertyName) ? !$scope.reverse : false;
                    $scope.propertyName = propertyName;
                };

            }])
})();

