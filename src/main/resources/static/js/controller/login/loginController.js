(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer) {
                if (!!$cookies.get("access_token")) {
                    window.location.reload();
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

                if (!!registrationToken) {
                    $http.get("/api/v1/registration/" + registrationToken)
                        .then(function (callback) {
                            $scope.personCredentials.username = callback.data.email;
                        }, function () {
                            console.log("Registration error")
                        })
                }

                $scope.sendPersonCredentials = function () {
                    var req = {
                        method: 'POST',
                        url: "https://management-office.herokuapp.com/oauth/token",
                        headers: {
                            "Authorization": "Basic " + encoded,
                            "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                        },
                        data: $httpParamSerializer($scope.personCredentials)
                    };
                    $http(req).then(function (callback) {
                        $http.defaults.headers.common.Authorization =
                            'Bearer ' + callback.data.access_token;

                        $cookies.put("access_token", callback.data.access_token, {
                            expires: cookiesExpirationDate
                        });
                        window.location.reload();
                    }, function (callback) {
                        console.log("Error");
                        window.alert(callback.data.error_description)
                    });
                };

            }])
})();