(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestGroupDetailsController", ["$scope", "$routeParams", "RequestGroupService", "RequestService", "FieldFactory",
            function ($scope, $routeParams, RequestGroupService, RequestService, FieldFactory) {
                var requestGroupId = $routeParams.requestGroupId;

                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));

                $scope.requests = [];
                $scope.currentPage = 1;
                $scope.pageSize = 20;
                $scope.maxPageSize = 20;
                $scope.request = {};

                $scope.order = FieldFactory.request.CREATE_TIME;
                $scope.requestFields = FieldFactory.request;

                var inProgressStatusId = 2;
                var closedStatusId = 3;

                $scope.goToRequestDetailsPage = function (requestId) {
                    return $scope.goToUrl("/secured/employee/request/" + requestId + "/details/");
                };

                $scope.getRequestByGroup = function () {
                    return RequestService.getRequestsByRequestGroup(requestGroupId, $scope.currentPage,
                        $scope.pageSize, $scope.order)
                        .then(function (callback) {
                            $scope.requests = callback.data.data;
                            $scope.maxPageSize = callback.data.totalElements;
                            $scope.currentPage = callback.data.pageNumber + 1;
                            $scope.pageSize = callback.data.pageSize;
                        }, function () {

                        })
                };

                $scope.orderRequests = function (fieldName) {
                    if (FieldFactory.isDescOrder($scope.order, fieldName))
                        $scope.order = FieldFactory.removeSortField($scope.order, fieldName);
                    else
                        $scope.order = FieldFactory.toggleOrder($scope.order, fieldName);
                    return $scope.getRequestByGroup();
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

                $scope.getRequestByGroup();

                $scope.startRequestGroupRequests = function () {
                    return RequestGroupService.setGroupStatus(requestGroupId, inProgressStatusId)
                        .then(function () {
                            swal("All requests in this group was started", "", "success");
                            $scope.getRequestByGroup();
                        }, function () {
                            swal("Request starting failure", "", "error");
                        })
                };

                $scope.finishRequestGroupRequests = function () {
                    return RequestGroupService.setGroupStatus(requestGroupId, closedStatusId)
                        .then(function () {
                            swal("All requests in this group was finished", "", "success");
                            $scope.getRequestByGroup();
                            $scope.deleteRequestGroup();
                        }, function () {
                            swal("Request finishing failure", "", "error");
                        })
                };

                $scope.removeFromRequestGroup = function (requestId) {
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to remove request from this group",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, remove it!",
                            closeOnConfirm: false
                        },
                        function () {
                            RequestService.removeFromRequestGroup(requestId)
                                .then(function (callback) {
                                    $scope.getRequestByGroup();
                                    swal("Request was removed from request group!", "", "success");
                                }, function () {
                                    swal("Remove Request From Group Failure!", "", "error");
                                });
                        });
                };

                $scope.deleteRequestGroup = function () {
                    swal({
                            title: "Group deleting?",
                            text: "You finished all requests in group. Do you wan't to delete the group?",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, delete it!",
                            closeOnConfirm: false
                        },
                        function () {
                            RequestGroupService.deleteRequestGroup(requestGroupId)
                                .then(function () {
                                    swal("Request group deleted!", "", "success");
                                    $scope.goToUrl("/secured/manager/request-group");
                                }, function (error) {
                                    swal("Delete Request Group Failure!", error, "error");
                                });
                        });
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