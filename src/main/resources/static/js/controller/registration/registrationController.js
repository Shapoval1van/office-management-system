(function () {
    angular.module("OfficeManagementSystem")
        .controller("RegistrationController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.personCredentials = {};
                $scope.nameRegExp = /^[A-Z][a-zA-Z\d]{2,50}$/;
                $scope.passRegExp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
                $scope.nameErrorMessage = "Name must contain at least 3 letters and start with capital letter!.";
                $scope.emailErrorMessage="Invalid email address.";
                $scope.passErrorMessage="The password must contain at least 8 characters, 1 capital letter and 1 number!";
                $scope.confirmPassErrorMessage="Does not matches with the password!"

                $scope.responseEmail = "";
                $scope.expiration = "";

                $scope.sendPersonCredentials = function () {
                    $http.post("/api/v1/registration", $scope.personCredentials)
                        .then(function (callback) {
                            $scope.responseEmail = callback.data.email;
                            $scope.expiration = callback.data.expiration;
                            window.alert("To verify the data provided we have sent you a message to your email : "+$scope.responseEmail)
                        }, function (callback) {
                            window.alert("Registration Failure! This email is already used!")

                        })
                };
            }])
            .directive('pwCheck', [function () {
                            return {
                              require: 'ngModel',
                              link: function (scope, elem, attrs, ctrl) {
                                var firstPassword = '#' + attrs.pwCheck;
                                elem.add(firstPassword).on('keyup', function () {
                                  scope.$apply(function () {
                                    var v = elem.val()===$(firstPassword).val();
                                    ctrl.$setValidity('pwmatch', v);
                                  });
                                });
                              }
                            }
                          }]);

})();
