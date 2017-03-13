(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestGroupController", ['$scope', '$routeParams', '$location', '$http', "WebSocketService",
            function ($scope, $routeParams, $location, $http, WebSocketService) {

                var requestId = $routeParams.requestId;

                var requestGroupDetails = "/request/";

                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.groupInput = '';
                $scope.groups = {};

                // --------------------------------------
                $scope.getGroups = function () {
                    if ($scope.groupInput.length >= 1) {
                        console.log($scope.groupInput);
                        // $http.get('/api/request-group/search/' + $scope.groupInput)
                        $http.get('/api/request-group/author/'+$scope.currentUser.id+'/search/' + $scope.groupInput)
                            .then(function (success) {
                                $scope.groups = success.data;
                            }, function (error) {

                            });
                    }
                }



                $scope.getAllGroups = function () {
                        console.log($scope.currentUser.id);
                        $http.get('/api/request-group/count/author/' + $scope.currentUser.id)
                            .then(function (success) {
                                var countGrp = success.data;
                                $http.get('/api/request-group/author/'+$scope.currentUser.id+'/page/1/size/'+ countGrp)
                                    .then(function (success) {
                                        $scope.groups = success.data;
                                    }, function (error) {

                                    });

                                $scope.groups = success.data;
                            }, function (error) {

                            });
                    }



                $scope.requestGroupDetails = function (requestGroupId) {
                    window.location = requestGroupDetails + requestGroupId;
                };


                $scope.createGroup = function() {
                    $http({
                        method: 'POST',
                        url: '/api/request-group/',
                        data: {
                            'name': $scope.groupInput ,
                            'author': $scope.currentUser.id
                        }
                    }).then(function successCallback(response) {

                        $http.get('/api/request-group/search/' + $scope.groupInput)
                            .then(function (success) {
                                $scope.groups = success.data;
                            }, function (error) {

                            });
                    }, function errorCallback(response) {
                    });
                };


            }])
})();

