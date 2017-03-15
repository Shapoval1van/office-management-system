(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService","RegistrationService",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer, SessionService,RegistrationService) {

                $scope._relocateUser = function () {
                    if ($scope.Session.getUserRole()=="ROLE_EMPLOYEE"){
                        window.location.href = "/requestListByEmployee"
                    }
                    if ($scope.Session.getUserRole()=="ROLE_OFFICE MANAGER"){
                        window.location.href = "/requestList"
                    }
                    if ($scope.Session.getUserRole()=="ROLE_ADMINISTRATOR"){
                        window.location.href = "/demo"
                    }
                }


                if ($scope.Session.isUserLoggedIn()){
                    $scope._relocateUser();
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
                            $scope._relocateUser();
                        }
                    });
                };


            }])
})();