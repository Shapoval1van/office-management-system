(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer, SessionService) {

                if (SessionService.isUserLoggedIn()){
                    window.location.reload()
                }

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
                    console.log($scope.personCredentials);
                    var req = {
                        method: 'POST',
                        url: host + "/oauth/token",
                        headers: {
                            "Authorization": "Basic " + encoded,
                            "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                        },
                        data: $httpParamSerializer($scope.personCredentials)
                    };
                    $http(req).then(function (callback) {
                        SessionService.createSession(callback);
                        window.location.reload();
                    }, function (callback) {
                        console.log("Error");
                        swal("Oops...", callback.data.error_description, "error");
                        //window.alert(callback.data.error_description)
                    });
                };

            }])
})();