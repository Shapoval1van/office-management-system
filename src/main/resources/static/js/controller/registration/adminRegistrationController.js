(function () {
    angular.module("OfficeManagementSystem")
        .controller("AdminRegistrationController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.personCredentials = {};

                $scope.submitStatusArray = [true, true];
                $scope.submitStatus = true;

                $scope.responseEmail = "";
                $scope.expiration = "";

                $http.get("/api/v1/registration/roleData").then(function successCallback(response) {
                    $scope.data = {
                        model: null,
                        availableOptions: response.data.roles
                    };
                    $scope.data.availableOptions.forEach(function (item) {
                        if (item.id == 1) {
                            item.displayValue = 'Адмінстратор'
                        }

                        if (item.id == 2) {
                            item.displayValue = 'Офіс менеджер'
                        }

                    })

                }, function errorCallback(response) {
                    console.log("error");// called asynchronously if an error occurs
                    // or server returns response with an error status.
                });

                /*    $scope.confirmPassCheck = function () {
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
                 };*/
                $scope.formValidationCheck = function () {
                    var count = 0;
                    for (var i = 0; i < $scope.submitStatusArray.length; i++) {
                        if ($scope.submitStatusArray[i] == false) count++;
                        if (count == 2) $scope.submitStatus = false;
                        else $scope.submitStatus = true;
                    }
                }


                /*    $scope.confirmPassCheck = function(){
                 var pass = $scope.personCredentials.password,
                 confirmPass = $scope.personCredentials.confirmPassword;
                 if(pass != confirmPass){
                 $(".pass-wrapper").addClass("form-check-pass-error");
                 $("#reg-form-submit").addClass("form-control_offset");
                 $scope.submitStatusArray[3] = true;
                 $scope.formValidationCheck();
                 }
                 else{
                 $(".pass-wrapper").removeClass("form-check-pass-error");
                 $("#reg-form-submit").removeClass("form-control_offset");
                 $scope.submitStatusArray[3] = false;
                 $scope.formValidationCheck();
                 }
                 };
                 $scope.passCheck = function(){
                 var passValue = $scope.personCredentials.password,
                 regExp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
                 if(passValue){
                 var lookFor = passValue.search(regExp);
                 if(lookFor == -1){
                 $("#password-input-group").addClass("password-incorrect");
                 $("#password-check-input-group").addClass("form-control_offset");
                 $scope.submitStatusArray[2] = true;
                 $scope.formValidationCheck();
                 } else{
                 $("#password-input-group").removeClass("password-incorrect");
                 $("#password-check-input-group").removeClass("form-control_offset");
                 $scope.submitStatusArray[2] = false;
                 $scope.formValidationCheck();
                 }
                 }
                 };*/

                $scope.firstNameCheck = function () {
                    var nameValue = $scope.personCredentials.firstName,
                        regExp = /^[A-Z][a-zA-Z\d]{3,50}$/;

                    if (nameValue) {
                        var lookFor = nameValue.search(regExp);

                        if (lookFor == -1) {
                            $("#first-name-input-group").addClass("name-incorrect");
                            $("#last-name-input-group").addClass("form-control_offset");
                            $scope.submitStatusArray[0] = true;
                            $scope.formValidationCheck();
                        }
                        else {
                            $("#first-name-input-group").removeClass("name-incorrect");
                            $("#last-name-input-group").removeClass("form-control_offset");
                            $scope.submitStatusArray[0] = false;
                            $scope.formValidationCheck();
                        }
                    }
                };

                $scope.lastNameCheck = function () {
                    var nameValue = $scope.personCredentials.lastName,
                        regExp = /^[A-Z][a-zA-Z\d]{3,50}$/;

                    if (nameValue) {
                        var lookFor = nameValue.search(regExp);

                        if (lookFor == -1) {
                            $("#last-name-input-group").addClass("name-incorrect");
                            $("#email-input-group").addClass("form-control_offset");
                            $scope.submitStatusArray[1] = true;
                            $scope.formValidationCheck();
                        } else {
                            $("#last-name-input-group").removeClass("name-incorrect");
                            $("#email-input-group").removeClass("form-control_offset");
                            $scope.submitStatusArray[1] = false;
                            $scope.formValidationCheck();
                        }
                    }

                };

                $scope.sendPersonCredentials = function () {

                    $scope.personCredentials.role = $scope.data.model;
                    $http.post("/api/v1/registration/adminRegistrationPerson", $scope.personCredentials)
                        .then(function (callback) {
                            $scope.responseEmail = callback.data.email;
                            $scope.expiration = callback.data.expiration;
                        }, function (callback) {
                            console.log("Registration Failure!")
                        })

                };
            }])
})();