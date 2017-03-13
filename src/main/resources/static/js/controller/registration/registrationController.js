(function () {
    angular.module("OfficeManagementSystem")
        .controller("RegistrationController", ["$scope", "$http","RegistrationService",
            function ($scope, $http, RegistrationService) {

                if ($scope.Session.isUserLoggedIn() && !($scope.Session.getCurrentUser().role == 'ROLE_ADMINISTRATOR')){
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

                $http.get("/api/v1/registration/roleData")
                    .then(function (response) {
                        response.data.roles.forEach(function (role){
                            if (role.id == 1){
                                role.name = "Administrator";
                            }
                            if (role.id == 2){
                                role.name = "Manager";
                            }
                            $scope.roles.push(role);
                        });
                    }, function (response) {
                        alert("Load role error!");
                    });


                $scope.sendPersonCredentials = function () {
                    if ($scope.Session.getCurrentUser().role == 'ROLE_ADMINISTRATOR'){
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
