(function () {
    angular.module("OfficeManagementSystem")
        .controller("RegistrationController", ["$scope", "$http","RegistrationService",
            function ($scope, $http, RegistrationService) {

                $scope.personCredentials = {
                    firstName:"",
                    lastName:"",
                    email:"",
                    password:""
                };

                $scope.sendPersonCredentials = function () {
                    RegistrationService.registerEmployee($scope.personCredentials)
                        .then(function (response) {
                            if (response.isError){
                                window.alert("Registration Failure!")
                            } else {
                                window.alert("To verify the data provided we have sent you a message to your email : "+response.responseEmail)
                            }

                        })
                };
            }])
})();
