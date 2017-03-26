(function () {
    angular.module("OfficeManagementSystem")
        .controller("DashboardController", ["$scope", "$http", "$rootScope",
            function ($scope, $http, $rootScope) {


                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.userName = currentUser.firstName + " " +  currentUser.lastName;
                $scope.userRole = currentUser.role.substr(5, currentUser.role.length);
                $scope.email = currentUser.email;
                $scope.isAdministrator = false;
                $scope.isManager = false;
                $scope.isEmployee = false;

                $rootScope.sideBarActiveElem = "dashboard";

                // for employee
                $scope.requestListSize = 0;
                $scope.freeRequestListCount = 0;
                $scope.progressRequestListCount = 0;
                $scope.closedRequestListCount = 0;
                $scope.canceledRequestListCount = 0;

                // for manager
                $scope.freeAssignedRequestCount = 0;
                $scope.progressAssignedRequestCount = 0;
                $scope.closedAssignedRequestCount = 0;

                // for admin
                $scope.freeRequestListNoManager = 0;
                $scope.allRequestListSize = 0;
                $scope.allFreeRequestCount = 0;
                $scope.allProgressRequestCount = 0;
                $scope.allClosedRequestCount = 0;
                $scope.allCancelRequestCount = 0;
                $scope.personListSize = 0;
                $scope.adminCount = 0;
                $scope.managerCount = 0;
                $scope.employeeCount = 0;

                if ($scope.userRole=="EMPLOYEE"){
                    $scope.isEmployee = true;
                    $scope.getData = function() {
                        $http({
                            method: 'GET',
                            url: '/api/dashboard/data'
                        }).then(function (response) {
                            $scope.requestListSize = response.data.requestListSize;
                            $scope.freeRequestListCount = response.data.freeRequestListCount;
                            $scope.progressRequestListCount = response.data.progressRequestListCount;
                            $scope.closedRequestListCount = response.data.closedRequestListCount;
                            $scope.canceledRequestListCount = response.data.canceledRequestListCount;

                        }, function errorCallback(response) {
                        });
                    };
                } else if ($scope.userRole=="OFFICE MANAGER"){
                    $scope.isManager = true;
                    $scope.getData = function() {
                        $http({
                            method: 'GET',
                            url: '/api/dashboard/data'
                        }).then(function (response) {
                            $scope.requestListSize = response.data.requestListSize;
                            $scope.freeRequestListCount = response.data.freeRequestListCount;
                            $scope.progressRequestListCount = response.data.progressRequestListCount;
                            $scope.closedRequestListCount = response.data.closedRequestListCount;
                            $scope.canceledRequestListCount = response.data.canceledRequestListCount;

                            $scope.freeAssignedRequestCount = response.data.freeAssignedRequestCount;
                            $scope.progressAssignedRequestCount = response.data.progressAssignedRequestCount;
                            $scope.closedAssignedRequestCount = response.data.closedAssignedRequestCount;

                        }, function errorCallback(response) {
                        });
                    };
                } else if ($scope.userRole=="ADMINISTRATOR"){
                    $scope.isAdministrator = true;

                    $scope.getData = function() {
                        $http({
                            method: 'GET',
                            url: '/api/dashboard/data'
                        }).then(function (response) {
                            $scope.requestListSize = response.data.requestListSize;
                            $scope.freeRequestListCount = response.data.freeRequestListCount;
                            $scope.progressRequestListCount = response.data.progressRequestListCount;
                            $scope.closedRequestListCount = response.data.closedRequestListCount;
                            $scope.canceledRequestListCount = response.data.canceledRequestListCount;

                            $scope.freeAssignedRequestCount = response.data.freeAssignedRequestCount;
                            $scope.progressAssignedRequestCount = response.data.progressAssignedRequestCount;
                            $scope.closedAssignedRequestCount = response.data.closedAssignedRequestCount;

                            $scope.freeRequestListNoManager = response.data.freeRequestListNoManager;
                            $scope.allRequestListSize = response.data.allRequestListSize;
                            $scope.allFreeRequestCount = response.data.allFreeRequestCount;
                            $scope.allProgressRequestCount = response.data.allProgressRequestCount;
                            $scope.allClosedRequestCount = response.data.allClosedRequestCount;
                            $scope.allCancelRequestCount = response.data.allCancelRequestCount;
                            $scope.personListSize = response.data.personListSize;
                            $scope.adminCount = response.data.adminCount;
                            $scope.managerCount = response.data.managerCount;
                            $scope.employeeCount = response.data.employeeCount;
                        }, function errorCallback(response) {
                        });
                    };
                }

                $scope.getData();

            }])
})();

