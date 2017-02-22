(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams",
            function ($scope, $http, $cookies, $resource, $routeParams) {
                $scope.personCredentials = {};

                var registrationToken = $routeParams.registrationToken;

                if (!!registrationToken) {
                    $http.get("/api/v1/registration/" + registrationToken)
                        .then(function (callback) {
                            $scope.personCredentials.email = callback.data.email;
                        }, function () {
                            console.log("Registration error")
                        })
                }

                $scope.recoverEmail = "";

                $scope.personCredentials = {
                    grant_type: "password",
                    username: "",
                    password: "",
                    client_id: "clientIdPassword"
                };
                $scope.encoded = btoa("clientIdPassword:secret");

                $scope.sendPersonCredentials = function () {
                    var req = {
                        method: 'POST',
                        url: "http://localhost:8080/spring-security-oauth-server/oauth/token",
                        headers: {
                            "Authorization": "Basic " + $scope.encoded,
                            "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                        },
                        data: $httpParamSerializer($scope.personCredentials)
                    };
                    $http(req).then(function (callback) {
                        $http.defaults.headers.common.Authorization =
                            'Bearer ' + callback.data.access_token;
                        $cookies.put("access_token", callback.data.access_token);
                        window.location.href = "index";
                    });
                };

                $scope.$on('oauth:login', function (event, token) {
                    $http.defaults.headers.common.Authorization = 'Bearer ' + token.access_token;
                });

                $scope.foo = {id: 0, name: "sample foo"};
                $scope.foos = $resource(
                    "http://localhost:8080/spring-security-oauth-resource/foos/:fooId",
                    {fooId: '@id'});
                $scope.getFoo = function () {
                    $scope.foo = $scope.foos.get({fooId: $scope.foo.id});
                };


                //FIXME: Change url, object and event on success and failure
                $scope.sendRecoverRequest = function () {
                    $http.put("/some_url/", $scope.recoverEmail)
                        .then(function (callback) {
                            //    Success
                            console.log("Login Success!")
                        }, function (callback) {
                            //    Failure
                            console.log("Login Failure!")
                        })
                }
            }])
})();