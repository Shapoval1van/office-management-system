(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService","RegistrationService",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer, SessionService,RegistrationService) {

                if ($scope.Session.isUserLoggedIn()){
                    window.location.reload()
                }

                $scope.username = "";
                $scope.password = "";


                if (!!$routeParams.registrationToken) {
                    RegistrationService.activateUser($routeParams.registrationToken)
                        .then(function (response) {
                            $scope.personCredentials.username = response.data.email;
                        }, function (response) {
                            window.alert("Activation error.");
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