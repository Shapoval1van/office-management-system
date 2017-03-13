(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer, SessionService) {

                if (SessionService.isUserLoggedIn()){
                    window.location.reload()
                }

                $scope.username = "";
                $scope.password = "";

                $scope.personCredentials = {
                    grant_type: "password",
                    username: "",
                    password: "",
                    client_id: "client",
                    scope: "read write"
                };

                var registrationToken = $routeParams.registrationToken;
                var encoded = btoa("client:");
                // Cookies living time (in milliseconds)
                var cookiesLivingTime = 1000 * 60 * 60 * 24 * 7;
                // Cookies expiration date
                var cookiesExpirationDate = new Date(Number(new Date()) + cookiesLivingTime);
                //var host = "https://management-office.herokuapp.com";
                var host = "http://localhost:8080";

                if (!!registrationToken) {
                    $http.get("/api/v1/registration/" + registrationToken)
                        .then(function (callback) {
                            $scope.personCredentials.username = callback.data.email;
                        }, function () {
                            console.log("Registration error")
                        })
                }

                $scope.sendPersonCredentials = function () {
                    $scope.Session.performLogin($scope.username, $scope.password).then(function (response) {
                        if(response.isError){
                            window.alert(response.data.error_description);
                        } else {
                            window.location.reload();
                        }
                    });
                };

            }])
})();