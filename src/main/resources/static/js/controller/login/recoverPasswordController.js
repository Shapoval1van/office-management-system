/**
 * Created by Max on 23.02.2017.
 */
(function () {
    angular.module("OfficeManagementSystem")
        .controller("RecoverPasswordController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {
                $scope.submitStatusArray = [true, true, true, true];
                $scope.password = "";
                $scope.confirmPassword = "";
                $scope.recoverEmail = "";

                $scope.sendRecoverRequest = function () {
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
                            console.log("Login Success!")
                        }, function (callback) {
                            //    Failure
                            console.log("Login Failure!")
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
                            //    Failure
                            console.log(callback);
                            console.log("Login Failure!")
                        })
                };


                $scope.confirmPassCheck = function () {
                    var pass = $scope.password,
                        confirmPass = $scope.confirmPassword;
                    if (pass != confirmPass) {
                        $(".pass-wrapper").addClass("form-check-pass-error");
                        $("#reg-form-submit").addClass("form-control_offset");
                        $scope.submitStatusArray[3] = true;
                    }
                    else {
                        $(".pass-wrapper").removeClass("form-check-pass-error");
                        $("#reg-form-submit").removeClass("form-control_offset");
                        $scope.submitStatusArray[3] = false;
                    }
                };

                $scope.passCheck = function () {
                    var passValue = $scope.password,
                        regExp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
                    if (passValue) {
                        var lookFor = passValue.search(regExp);
                        if (lookFor == -1) {
                            $("#password-input-group").addClass("password-incorrect");
                            $("#password-check-input-group").addClass("form-control_offset");
                            $scope.submitStatusArray[2] = true;
                        } else {
                            $("#password-input-group").removeClass("password-incorrect");
                            $("#password-check-input-group").removeClass("form-control_offset");
                            $scope.submitStatusArray[2] = false;
                        }
                    }
                };

            }])
})();