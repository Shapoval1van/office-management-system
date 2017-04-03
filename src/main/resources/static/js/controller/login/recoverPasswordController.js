
(function () {
    angular.module("OfficeManagementSystem")
        .controller("RecoverPasswordController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {

                $scope.passErrorMessage="The password must contain at least 8 characters, 1 capital letter and 1 number!";
                $scope.confirmPassErrorMessage="Does not matches with the password!";

                $scope.password = "";
                $scope.confirmPassword = "";
                $scope.recoverEmail = "";
                $scope.hasError = false;

                if ($scope.Session.isUserLoggedIn()){
                    window.location.href = "/secured/employee/dashboard"
                }

                var showErrorMessage = function (text) {
                    text = text ? text : "";
                    swal({
                        title: "Error",
                        text: text,
                        type: "error",
                        confirmButtonText: "Close"
                    });
                };
                $scope.sendRecoverRequest = function () {
                    if ($scope.recoverEmail==""){
                        $scope.hasError = true;
                        return;
                    } else {
                        $scope.hasError = false;
                    }
                    $http({
                        method: "POST",
                        url: "/api/v1/resetPassword",
                        headers: {
                            "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                        },
                        transformRequest: function (obj) {
                            var str = [];
                            for (var p in obj)
                                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                            return str.join("&");
                        },
                        data: {
                            email: $scope.recoverEmail
                        }
                    })
                        .then(function (callback) {
                            //    Success
                            showErrorMessage("Recover message sent to your email "+$scope.recoverEmail);
                            console.log("Login Success!");
                        }, function (callback) {
                            //    Failure
                            showErrorMessage("This email is not registered in our service");
                            console.log("Login Failure!");
                        })
                };

                $scope.sendNewPassword = function () {
                    var fd = new FormData();
                    fd.append('password', $scope.password);

                    $http.post("/api/v1/resetPassword/" + $routeParams.token, fd, {
                        transformRequest: angular.identity,
                        headers: {'Content-Type': undefined}
                    })
                        .then(function (callback) {
                            //    Success
                            window.location.href = "/login";
                            console.log(callback);
                            console.log("Login Success!")
                        }, function (callback) {
                            if(callback.status===500){
                                showErrorMessage("Bad credentials");
                            }else {
                                showErrorMessage(callback.data.message);
                            }
                            console.log(callback);
                            console.log("Login Failure!")
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