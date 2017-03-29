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

                $scope.request = {};

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
                    $scope.goToUrl("/secured/employee/request/" + requestId + "/details");
                };

                $scope.updateRequestStatus = function (statusId, request) {
                    $scope.request = request;
                    $scope.request.status = statusId;
                    $scope.request.priority = $scope.request.priority.id;
                    if (!!$scope.request.employee)
                        $scope.request.employee = $scope.request.employee.id;
                    if (!!$scope.request.manager)
                        $scope.request.manager = $scope.request.manager.id;
                    if (!!$scope.request.parent)
                        $scope.request.parent = $scope.request.parent.id;
                    if (!!$scope.request.requestGroup)
                        $scope.request.requestGroup = $scope.request.requestGroup.id;

                    return RequestService.updateRequestStatus($scope.request.id, statusId, $scope.request)
                        .then(function (callback) {
                            $scope.pageChanged();
                        }, function () {

                        })
                };

                $scope.setInProgressStatus = function (request) {
                    swal("Request start", "Request successful start!", "success");
                    return $scope.updateRequestStatus(2, request);
                };

                $scope.setClosedStatus = function (request) {
                    swal("Request finished", "Request successful finished!", "success");
                    return $scope.updateRequestStatus(3, request);
                };

                $scope.isInProgress = function (statusName) {
                    return statusName === "IN PROGRESS";
                };

                $scope.isFree = function (statusName) {
                    return statusName === "FREE";
                };

            }])
})();
