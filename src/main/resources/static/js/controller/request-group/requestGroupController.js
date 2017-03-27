(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestGroupController", ['$scope', '$routeParams', "$rootScope", 'RequestGroupService',
            function ($scope, $routeParams, $rootScope, RequestGroupService) {

                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.groups = [];
                $scope.currentPage = 1;
                $scope.pageSize = 20;
                $scope.requestGroupNamePattern = "";
                $scope.maxPageSize = 20;

                $rootScope.sideBarActiveElem = "request-group";

                $scope.currentRequestGroup = {};

                $scope.getGroupByAuthor = function () {
                    return RequestGroupService.getGroupByAuthor($scope.currentUser.id, $scope.currentPage, $scope.pageSize)
                        .then(function (callback) {
                            $scope.groups = callback.data.data;
                            $scope.maxPageSize = callback.data.totalElements;
                            $scope.currentPage = callback.data.pageNumber + 1;
                            $scope.pageSize = callback.data.pageSize;
                        }, function (callback) {
                            console.log("Failure")
                        });
                };

                $scope.getGroupByAuthor();

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
                            swal("Create Group Error", callback.data, "error");
                        })
                };

                $scope.deleteRequestGroup = function (groupId) {
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to delete this request group",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, delete it!",
                            closeOnConfirm: false
                        },
                        function () {
                            RequestGroupService.deleteRequestGroup(groupId)
                                .then(function () {
                                    $scope.getGroupByAuthor();
                                }, function (error) {
                                    swal("Delete Request Group Failure!", error, "error");
                                });
                            swal("Request group deleted!", "", "success");
                        });
                };

                // $scope.deleteRequestGroup = function () {
                //     return RequestGroupService.deleteRequestGroup($scope.currentRequestGroup.id)
                //         .then(function () {
                //             $scope.getGroupByAuthor();
                //         }, function () {
                //             console.log("Failure")
                //         })
                // };

                $scope.setCurrentRequestGroup = function (requestGroup) {
                    $scope.currentRequestGroup = requestGroup;
                };

                $scope.goToRequestGroupPage = function (requestGroupId) {
                    return $scope.goToUrl("/secured/request-group/" + requestGroupId + "/requests");
                };

            }])
})();

