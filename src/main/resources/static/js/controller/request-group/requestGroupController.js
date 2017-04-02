(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestGroupController", ['$scope', '$routeParams', "$rootScope", 'RequestGroupService', "FieldFactory",
            function ($scope, $routeParams, $rootScope, RequestGroupService, FieldFactory) {

                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.groups = [];
                $scope.currentPage = 1;
                $scope.pageSize = 20;
                $scope.requestGroupNamePattern = "";
                $scope.maxPageSize = 20;

                $scope.order = FieldFactory.requestGroup.NAME;

                $rootScope.sideBarActiveElem = "request-group";

                $scope.currentRequestGroup = {};

                $scope.requestGroupFields = FieldFactory.requestGroup;

                $scope.getGroupByAuthor = function () {
                    return RequestGroupService.getGroupByAuthor($scope.currentUser.id, $scope.currentPage,
                        $scope.pageSize, $scope.order)
                        .then(function (callback) {
                            $scope.groups = callback.data.data;
                            $scope.maxPageSize = callback.data.totalElements;
                            $scope.currentPage = callback.data.pageNumber + 1;
                            $scope.pageSize = callback.data.pageSize;
                        }, function (callback) {
                            console.log("Failure")
                        });
                };

                $scope.orderRequestGroups = function (fieldName) {
                    if (FieldFactory.isDescOrder($scope.order, fieldName))
                        $scope.order = FieldFactory.removeSortField($scope.order, fieldName);
                    else
                        $scope.order = FieldFactory.toggleOrder($scope.order, fieldName);
                    return $scope.getGroupByAuthor();
                };


                $scope.orderGroupsByName = function () {
                    return $scope.orderRequestGroups(FieldFactory.requestGroup.NAME);
                };

                $scope.isDescOrder = function (fieldName) {
                    return FieldFactory.isDescOrder($scope.order, fieldName);
                };

                $scope.isAscOrder = function (fieldName) {
                    return FieldFactory.isAscOrder($scope.order, fieldName);
                };

                $scope.getGroupByAuthor();

                $scope.searchByNamePattern = function () {
                    if ($scope.requestGroupNamePattern.length > 2 || $scope.requestGroupNamePattern.length == 0) {
                        $(".request-group-input-wrapper").removeClass("warning");
                        return RequestGroupService.findGroupByNamePattern($scope.currentUser.id, $scope.requestGroupNamePattern)
                            .then(function (callback) {
                                $scope.groups = callback.data;
                            }, function (callback) {

                            })
                    }
                };

                $scope.createGroup = function () {
                    if ($scope.requestGroupNamePattern.length < 3) {
                        $(".request-group-input-wrapper").addClass("warning");
                    }
                    else {
                        $(".request-group-input-wrapper").removeClass("warning");
                        return RequestGroupService.createGroup($scope.requestGroupNamePattern)
                            .then(function (callback) {
                                $scope.searchByNamePattern();
                            }, function (callback) {
                                swal("Create Group Error", callback.data, "error");
                            })
                    }
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
                                    swal("Request group deleted!", "", "success");
                                }, function (error) {
                                    swal("Delete Request Group Failure!", error, "error");
                                });
                        });
                };

                $scope.setCurrentRequestGroup = function (requestGroup) {
                    $scope.currentRequestGroup = requestGroup;
                };

                $scope.goToRequestGroupPage = function (requestGroupId) {
                    return $scope.goToUrl("/secured/manager/request-group/" + requestGroupId + "/requests");
                };

            }])
})();

