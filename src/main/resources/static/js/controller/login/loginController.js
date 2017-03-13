(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer, SessionService) {

                if (SessionService.isUserLoggedIn()){
                    window.location.reload()
                }

                $scope.username = "";
                $scope.password = "";


                var registrationToken = $routeParams.registrationToken;

                if (!!registrationToken) {
                    $http.get("/api/v1/registration/" + registrationToken)
                        .then(function (callback) {
                            $scope.personCredentials.username = callback.data.email;
                        }, function () {
                            console.log("Registration error")
                        })
                }

                $scope.sendPersonCredentials = function () {
                    SessionService.login($scope.username, $scope.password).then(function (callback) {
                        // тут callback вже приходить обробленим і ми його можем юзати
                        window.location.reload();
                    }, function (callback) {
                        window.alert(callback.data.error_description)
                    });
                };

            }])
})();