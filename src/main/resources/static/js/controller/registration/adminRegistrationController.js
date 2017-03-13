(function () {
    angular.module("OfficeManagementSystem")
        .controller("AdminRegistrationController", ["$scope", "$http","RegistrationService",
            function ($scope, $http, RegistrationService) {

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
                    RegistrationService.registerAnyUser($scope.personCredentials)
                        .then(function (response) {
                            if (response.isError){
                                alert("Error!");
                            } else {
                                $scope.responseEmail = response.data.email;
                                $scope.expiration = response.data.expiration;
                                alert("Ok");
                            }
                        })

                };
            }])
})();