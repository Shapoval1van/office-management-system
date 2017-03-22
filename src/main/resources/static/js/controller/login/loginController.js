(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService","RegistrationService",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer, SessionService,RegistrationService) {

                $scope._relocateUser = function () {
                    if ($scope.Session.getUserRole()=="ROLE_EMPLOYEE"){
                        window.location.href = "/dashboard"
                    }
                    if ($scope.Session.getUserRole()=="ROLE_OFFICE MANAGER"){
                        window.location.href = "/dashboard"
                    }
                    if ($scope.Session.getUserRole()=="ROLE_ADMINISTRATOR"){
                        window.location.href = "/dashboard"
                    }
                }


                if ($scope.Session.isUserLoggedIn()){
                    $scope._relocateUser();
                }

                $scope.username = "";
                $scope.password = "";
                $scope.hasError = false;


                if (!!$routeParams.registrationToken) {
                    RegistrationService.activateUser($routeParams.registrationToken)
                        .then(function (response) {
                            $scope.person.username = response.data.email;
                        }, function (response) {
                            window.alert("Activation error.");
                        })
                }

                $scope.performLogin = function () {
                    if ($scope.username=="" || $scope.password==""){
                        $scope.hasError = true;
                        return;
                    } else{
                        $scope.hasError = false;
                    }
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