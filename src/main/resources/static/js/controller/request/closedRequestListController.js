(function () {
    angular.module("OfficeManagementSystem")
        .controller("ClosedRequestListController", ["$scope", "$location", "$rootScope","RequestService",  "FieldFactory",
            function ($scope, $location, $rootScope, RequestService, FieldFactory) {

                var currentUser = JSON.parse(localStorage.getItem("currentUser"));

                $scope.pageSize = 10;
                $scope.requests = {};
                $scope.maxSize = 5;
                $scope.totalItems = 0;
                $scope.currentPage = 1;
                $scope.selectedRequest = -1;
                $scope.requestListVisibility = true;

                $scope.order = FieldFactory.requestGroup.NAME;

                $scope.pageChanged = function () {
                    RequestService.getAllClosedRequestByEmployee($scope.currentPage, $scope.pageSize, $scope.order)
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

                $scope.orderRequests = function (order) {
                    if(!$scope.order.includes(FieldFactory.desc(order))){
                        var contain = $scope.order.includes(order);
                        $scope.order = contain ? $scope.order.replace(order,FieldFactory.desc(order)): order+','+$scope.order;
                    }
                    return $scope.pageChanged();
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

                $scope.sortRequestsByManager = function () {
                    return $scope.orderRequests(FieldFactory.request.MANAGER);
                };

                $scope.sortRequestsByStatus = function () {
                    return $scope.orderRequests(FieldFactory.request.STATUS);
                };
            }])
})();
