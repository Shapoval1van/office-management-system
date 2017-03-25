(function () {
    angular.module("OfficeManagementSystem")
        .controller("AssignedRequestListController", ["$scope", "$location", "$rootScope", "PersonService", "RequestService",
            function ($scope, $location, $rootScope, PersonService, RequestService) {

                var currentUser = JSON.parse(localStorage.getItem("currentUser"));

                $scope.pageSize = 10;
                $scope.requests = {};
                $scope.maxSize = 5;
                $scope.totalItems = 0;
                $scope.currentPage = 1;
                $scope.selectedRequest = -1;
                $scope.requestListVisibility = true;

                $rootScope.sideBarActiveElem = "my-requests";

                $scope.pageChanged = function () {
                    RequestService.getAssignedRequestList($scope.currentPage, $scope.pageSize)
                        .then(function (response) {
                            $scope.requests = [];
                            $scope.requests = response.data.data;
                            $scope.totalItems = response.data.totalElements;
                            if (!$scope.requests.length) {
                                $scope.requestListVisibility = false;
                            }
                        }, function errorCallback(response) {
                        });
                };

                $scope.getTotalPage = function () {
                    return $scope.totalItems;
                };

                $scope.getTotalPage(); //
                $scope.pageChanged(1); // get first page

                $scope.goToRequestDetailsPage = function (requestId) {
                    $scope.goToUrl("/secured/request/" + requestId + "/details");
                };

            }])
})();
