(function () {
    angular.module("OfficeManagementSystem")
        .controller("RegistrationController", ["$scope", "$http","RegistrationService",
            function ($scope, $http, RegistrationService) {

                if ($scope.Session.isUserLoggedIn() && !($scope.Session.getUserRole() == 'ROLE_ADMINISTRATOR')){
                    window.location = "/";
                }

                $scope.emailRegexp = new RegExp("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
                $scope.passwordRegexp = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,70}$");

                $scope.person = {
                    firstName:"",
                    lastName:"",
                    email:"",
                    password:"",
                    role:3,
                    enabled: false
                };

                $scope._errorPerson = {
                    firstName:false,
                    lastName:false,
                    email:false,
                    password:false
                }

                $scope._validate = function () {
                    if ($scope.person.firstName==""||$scope.person.firstName.length<3){
                        $scope._errorPerson.firstName = true;
                    } else {
                        $scope._errorPerson.firstName = false;
                    }
                    if ($scope.person.lastName==""||$scope.person.lastName.length<3){
                        $scope._errorPerson.lastName = true;
                    } else {
                        $scope._errorPerson.lastName = false;
                    }
                    if (!$scope.emailRegexp.test($scope.person.email)){
                        $scope._errorPerson.email = true;
                    } else {
                        $scope._errorPerson.email = false;
                    }
                    if (!$scope.passwordRegexp.test($scope.person.password)){
                        $scope._errorPerson.password = true;
                    } else {
                        $scope._errorPerson.password = false;
                    }

                    return (!$scope._errorPerson.firstName&&
                            !$scope._errorPerson.lastName&&
                            !$scope._errorPerson.email&&
                            !$scope._errorPerson.password);
                };

                $scope.roles = [

                ];

                if ($scope.Session.getUserRole() == 'ROLE_ADMINISTRATOR'){
                    RegistrationService.loadRoles()
                        .then(function (response) {
                            if (response.isError){
                                swal("Load role error!", "", "error");
                            } else {
                                $scope.roles = response.roles;
                            }
                        })
                }

                $scope.sendPersonCredentials = function () {
                    if (!$scope._validate()){
                        return;
                    }
                    if ($scope.Session.getUserRole() == 'ROLE_ADMINISTRATOR'){
                        RegistrationService.registerAnyUser($scope.person)
                            .then(function (response) {
                                if (response.isError){
                                    swal("Registration Failure!", "", "error")
                                } else {
                                    swal("Registration successful", "To verify the data provided we have " +
                                        "sent you a message to your email : " + response.data.email, "success");
                                    //window.alert("To verify the data provided we have sent you a message to your email : "+response.responseEmail)
                                }
                            })
                    } else {
                        RegistrationService.registerEmployee($scope.person)
                            .then(function (response) {
                                if (response.isError){
                                    swal("Registration Failure!", "", "error")
                                } else {
                                    swal("Registration successful", "To verify the data provided we have " +
                                        "sent you a message to your email : " + response.data.email, "success");
                                    //window.alert("To verify the data provided we have sent you a message to your email : "+response.responseEmail)
                                }
                            })
                    }
                };
            }])
})();
