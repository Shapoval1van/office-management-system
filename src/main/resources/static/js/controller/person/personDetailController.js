(function () {
    angular.module("OfficeManagementSystem")
        .controller("PersonDetailController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {

                $scope.personId = $routeParams.personId;
                $scope.userName = '';
                $scope.userRole = '';
                $scope.email = '';
                $scope.isEmployee = false;

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

                $scope.getUserInfo = function () {
                    $http({
                        method: 'GET',
                        url: '/api/person/' + $scope.personId
                    }).then(function (response) {
                        $scope.userName = response.data.firstName + " " + response.data.lastName;
                        $scope.userRole = response.data.role.name.substr(5, response.data.role.name.length);
                        $scope.email = response.data.email;
                    }, function errorCallback(response) {
                    });
                };

                $scope.getUserInfo();

                $scope.getData = function () {
                    $http({
                        method: 'GET',
                        url: '/api/dashboard/data/' + $scope.personId
                    }).then(function (response) {
                        $scope.requestListSize = response.data.requestListSize;
                        $scope.freeRequestListCount = response.data.freeRequestListCount;
                        $scope.progressRequestListCount = response.data.progressRequestListCount;
                        $scope.closedRequestListCount = response.data.closedRequestListCount;
                        $scope.canceledRequestListCount = response.data.canceledRequestListCount;

                        $scope.freeAssignedRequestCount = response.data.freeAssignedRequestCount;
                        $scope.progressAssignedRequestCount = response.data.progressAssignedRequestCount;
                        $scope.closedAssignedRequestCount = response.data.closedAssignedRequestCount;

                        if ($scope.freeAssignedRequestCount==null && $scope.progressAssignedRequestCount==null
                        && $scope.closedAssignedRequestCount==null){
                            $scope.isEmployee = true;
                        }

                    }, function errorCallback(response) {
                    });
                };

                $scope.getData();

            }])
})();

