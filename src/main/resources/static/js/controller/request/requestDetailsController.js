(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestDetailsController", ['$scope', '$routeParams', '$location', '$http',
            function ($scope, $routeParams, $location, $http) {
                var host = $location.host;
                $http({
                    method:'GET',
                    url:'/api/request/'+$routeParams.requestId
                }).then(function successCallback(response) {
                    $scope.request = response.data;
                    console.log(response.data);
                    $scope.creationTime = new Date(response.data.creationTime).toLocaleDateString("nl",{year:"2-digit",month:"2-digit", day:"2-digit"});
                },function errorCallback(response) {

                });
                $http({
                    method:'GET',
                    url:'/api/request/sub/'+$routeParams.requestId
                }).then(function successCallback(response) {
                    $scope.subRequest = response.data;
                },function errorCallback(response) {

                });

        }])
})();

