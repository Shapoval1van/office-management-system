(function () {
    angular.module("OfficeManagementSystem")
    .controller("RegistrationController", ["$scope", "$http",
        function ($scope, $http) {
            $scope.personCredentials = {};
            $scope.submitStatus = true;

            $scope.confirmPassCheck = function(){
                var pass = $scope.personCredentials.password,
                confirmPass = $scope.personCredentials.confirmPassword;
                if(pass != confirmPass){ 
                    $(".pass-wrapper").addClass("form-check-pass-error");
                    $("#reg-form-submit").addClass("form-control_offset");
                    $scope.submitStatus = true;
                }
                else{ 
                    $(".pass-wrapper").removeClass("form-check-pass-error");
                    $("#reg-form-submit").removeClass("form-control_offset");
                    $scope.submitStatus = false;
                }
            };

                //FIXME: Change url, object and event on success and failure
                $scope.sendPersonCredentials = function () {
                    $http.post("/some_url/", $scope.personCredentials)
                    .then(function (callback) {
                            //    Success
                            console.log("Registration Success!")
                        }, function (callback) {
                            //    Failure
                            console.log("Registration Failure!")
                        })
                };
            }])
})();

