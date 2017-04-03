(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http", "$cookies", "$resource", "$routeParams", "$httpParamSerializer","SessionService","RegistrationService",
            function ($scope, $http, $cookies, $resource, $routeParams, $httpParamSerializer, SessionService,RegistrationService) {

                $scope._relocateUser = function () {
                    if ($scope.Session.getUserRole()=="ROLE_EMPLOYEE"){
                        window.location.href = "/secured/employee/dashboard"
                    }
                    if ($scope.Session.getUserRole()=="ROLE_OFFICE MANAGER"){
                        window.location.href = "/secured/employee/dashboard"
                    }
                    if ($scope.Session.getUserRole()=="ROLE_ADMINISTRATOR"){
                        window.location.href = "/secured/employee/dashboard"
                    }
                };


                if ($scope.Session.isUserLoggedIn()){
                    $scope._relocateUser();
                }



                $scope.person = {};
                $scope.username = "";
                $scope.password = "";
                $scope.hasError = false;

                var showErrorMessage = function (text) {
                    text = text?text:"";
                    swal({
                        title: "Error",
                        text: text,
                        type: "error",
                        confirmButtonText: "Close"
                    });
                };


                if (!!$routeParams.registrationToken) {
                    console.log("Activation!")
                    RegistrationService.activateUser($routeParams.registrationToken)
                        .then(function (response) {
                            $scope.person.username = response.data.email;
                            $scope.username = response.data.email;
                        }, function (response) {
                            showErrorMessage("Activation error.");
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
                            showErrorMessage(response.data.error_description);
                        } else {
                            $scope._relocateUser();
                        }
                    });
                };

                $scope.$watch("Session.isUserLoggedIn()", function (newValue, oldValue, scope) {
                    if(newValue == true){
                        console.log("Watcher time!!!!!");
                        $scope._relocateUser();
                    }
                });


            }])
})();