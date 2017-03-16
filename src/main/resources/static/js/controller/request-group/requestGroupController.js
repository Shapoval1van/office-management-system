(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestGroupController", ['$scope', '$routeParams', 'RequestGroupService',
            function ($scope, $routeParams, RequestGroupService) {

                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.groups = [];
                $scope.currentPage = 1;
                $scope.pageSize = 20;
                $scope.requestGroupNamePattern = "";
                $scope.maxPageSize = 20;

                $scope.getPageMaxCount = function () {
                    return RequestGroupService.getGroupCountByAuthor($scope.currentUser.id)
                        .then(function (callback) {
                            $scope.maxPageSize = callback.data;
                        }, function (callback) {

                        })
                };

                $scope.getPageMaxCount();

                $scope.getGroupByAuthor = function () {
                    $scope.getPageMaxCount();
                    return RequestGroupService.getGroupByAuthor($scope.currentUser.id, $scope.currentPage, $scope.maxPageSize)
                        .then(function (callback) {
                            $scope.groups = callback.data;
                        }, function (callback) {
                            console.log("Failure")
                        });
                };

                $scope.searchByNamePattern = function () {
                    return RequestGroupService.findGroupByNamePattern($scope.currentUser.id, $scope.requestGroupNamePattern)
                        .then(function (callback) {
                            $scope.groups = callback.data;
                        }, function (callback) {

                        })
                };

                $scope.createGroup = function () {
                    return RequestGroupService.createGroup($scope.requestGroupNamePattern)
                        .then(function (callback) {
                            $scope.searchByNamePattern();
                        }, function (callback) {

                        })
                };

                $scope.goToRequestGroupPage = function (requestGroupId) {
                    return $scope.goToUrl("/request-group/" + requestGroupId + "/requests");
                };

            }])
})();

