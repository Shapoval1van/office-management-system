(function () {
    angular.module("OfficeManagementSystem")
        .controller("RegistrationController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.personCredentials = {};
                $scope.submitStatus = true;

                $scope.responseEmail = "";
                $scope.expiration = "";

                $scope.confirmPassCheck = function () {
                    var pass = $scope.personCredentials.password,
                        confirmPass = $scope.personCredentials.confirmPassword;
                    if (pass != confirmPass) {
                        $(".pass-wrapper").addClass("form-check-pass-error");
                        $("#reg-form-submit").addClass("form-control_offset");
                        $scope.submitStatus = true;
                    }
                    else {
                        $(".pass-wrapper").removeClass("form-check-pass-error");
                        $("#reg-form-submit").removeClass("form-control_offset");
                        $scope.submitStatus = false;
                    }
                };

                $scope.sendPersonCredentials = function () {
                    $http.post("/api/v1/registration", $scope.personCredentials)
                        .then(function (callback) {
                            $scope.responseEmail = callback.data.email;
                            $scope.expiration = callback.data.expiration;
                        }, function (callback) {
                            console.log("Registration Failure!")
                        })
                };
            }])
})();

