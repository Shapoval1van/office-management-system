(function () {
    angular.module("OfficeManagementSystem")
        .controller("UpdateRequestLinkToGroupController", ["$scope", "$http", "$routeParams", "RequestGroupService",
            function ($scope, $http, $routeParams, RequestGroupService) {
                $scope.assignedMessage = ""; // for message popup
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                var requestId = $routeParams.requestId;

                $scope.groups = [];
                $scope.selectedRequestGroup = "";

                $scope.updateGroupInput = function () {
                    if ($scope.selectedRequestGroup.length >= 2) {
                        console.log($scope.selectedRequestGroup);

                        $http({
                            method: 'GET',
                            url: '/api/request-group/author/ ' + currentUser.id + '/search/' + $scope.selectedRequestGroup
                        }).then(function successCallback(response) {
                            $scope.groups = response.data;
                        }, function errorCallback(response) {
                        });
                    }
                };

                $scope.link = function () {
                    $http({
                        method: 'PUT',
                        url: '/api/request/' + requestId + '/grouping',
                        data: {
                            'id': $scope.selectedRequestGroup.id,
                            'name': $scope.selectedRequestGroup.name,
                            'author': $scope.selectedRequestGroup.author.id
                        }
                    }).then(function successCallback(response) {
                        $scope.groups = response.data;
                        $scope.assignedMessage = "Linked";
                        $scope.getRequest();
                        $('.modal-backdrop').fadeOut();
                    }, function errorCallback(response) {
                        $scope.assignedMessage = response.data.errors
                            .map(function (e) {
                                return e.detail
                            })
                            .join('. ');
                    });
                };

                $scope.isGroupSelected = function () {
                    return $scope.selectedRequestGroup.id !== undefined;
                };

                $scope.createAndLinkGroup = function () {
                    return RequestGroupService.createGroup($scope.selectedRequestGroup)
                        .then(function (callback) {
                            $scope.selectedRequestGroup = callback.data;
                            $scope.link();
                            $('.modal-backdrop').fadeOut();
                        }, function (response) {
                            $scope.assignedMessage = response.data.errors
                                .map(function (e) {
                                    return e.detail
                                })
                                .join('. ');
                        });
                };

            }])
})();

