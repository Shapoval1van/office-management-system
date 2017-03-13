(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService",
            function ($scope, $http, $resource, $routeParams) {

                if ($scope.Session.isUserLoggedIn()){
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


                $scope.performLogin = function () {
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