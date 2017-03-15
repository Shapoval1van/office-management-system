(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestGroupDetailsController", ["$scope", "$routeParams", "RequestGroupService", "RequestService",
            function ($scope, $routeParams, RequestGroupService, RequestService) {
                var requestGroupId = $routeParams.requestGroupId;

                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));

                $scope.requests = [];
                $scope.currentPage = 1;
                $scope.pageSize = 20;
                $scope.maxSize = 5;

                var inProgressStatusId = 2;
                var closedStatusId = 3;

                $scope.getPageMaxCount = function () {
                    return RequestGroupService.getGroupCountByAuthor($scope.currentUser.id)
                        .then(function (callback) {
                            $scope.maxSize = Math.floor(callback.data / $scope.pageSize);
                        }, function (callback) {

                        })
                };

                $scope.getPageMaxCount();

                $scope.goToRequestDetailsPage = function (requestId) {
                    return $scope.goToUrl("/request/" + requestId + "/details/");
                };

                $scope.getRequestByGroup = function () {
                    return RequestService.getRequestsByRequestGroup(requestGroupId, $scope.currentPage, $scope.pageSize)
                        .then(function (callback) {
                            $scope.requests = callback.data;
                        }, function () {

                        })
                };

                $scope.getRequestByGroup();

                $scope.startRequestGroupRequests = function () {
                    return RequestGroupService.setGroupStatus(requestGroupId, inProgressStatusId)
                        .then(function () {
                            $scope.getRequestByGroup();
                        }, function () {

                        })
                };

                $scope.finishRequestGroupRequests = function () {
                    return RequestGroupService.setGroupStatus(requestGroupId, closedStatusId)
                        .then(function () {
                            $scope.getRequestByGroup();
                        }, function () {

                        })
                };

                $scope.isRequestGroupFree = function () {
                    return $scope.requests.some(function (request) {
                        return request.status.name === "FREE";
                    })
                };

                $scope.isRequestGroupInProgress = function () {
                    return $scope.requests.some(function (request) {
                        return request.status.name === "IN PROGRESS";
                    })
                };

                $scope.isRequestGroupClosed = function () {
                    return $scope.requests.some(function (request) {
                        return request.status.name === "CLOSED";
                    })
                }

            }])
})();