(function () {
    angular.module("OfficeManagementSystem")
        .controller("RegistrationController", ["$scope", "$http","RegistrationService",
            function ($scope, $http, RegistrationService) {

                if ($scope.Session.isUserLoggedIn() && !($scope.Session.getUserRole() == 'ROLE_ADMINISTRATOR')){
                    window.location = "/";
                }

                $scope.personCredentials = {
                    firstName:"",
                    lastName:"",
                    email:"",
                    password:"",
                    role:-1
                };

                $scope.roles = [

                ];

                if ($scope.Session.getUserRole() == 'ROLE_ADMINISTRATOR'){
                    RegistrationService.loadRoles()
                        .then(function (response) {
                            if (response.isError){
                                alert("Load role error!");
                            } else {
                                $scope.roles = response.roles;
                            }
                        })
                }

                $scope.sendPersonCredentials = function () {
                    if ($scope.Session.getUserRole() == 'ROLE_ADMINISTRATOR'){
                        RegistrationService.registerAnyUser($scope.personCredentials)
                            .then(function (response) {
                                if (response.isError){
                                    window.alert("Registration Failure!")
                                } else {
                                    window.alert("To verify the data provided we have sent you a message to your email : "+response.responseEmail)
                                }
                            })
                    } else {
                        RegistrationService.registerEmployee($scope.personCredentials)
                            .then(function (response) {
                                if (response.isError){
                                    window.alert("Registration Failure!")
                                } else {
                                    window.alert("To verify the data provided we have sent you a message to your email : "+response.responseEmail)
                                }
                            })
                    }
                };
            }])
})();
