(function () {
    angular.module("OfficeManagementSystem")
        .controller("LoginController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.personCredentials = {};

                $scope.recoverEmail = "";

                //FIXME: Change url, object and event on success and failure
                $scope.sendPersonCredentials = function () {
                    $http.post("/some_url/", $scope.personCredentials)
                        .then(function (callback) {
                            //    Success
                            console.log("Login Success!")
                        }, function (callback) {
                            //    Failure
                            console.log("Login Failure!")
                        })
                };

                //FIXME: Change url, object and event on success and failure
                $scope.sendRecoverRequest = function () {
                    $http.put("/some_url/", $scope.recoverEmail)
                        .then(function (callback) {
                            //    Success
                            console.log("Login Success!")
                        }, function (callback) {
                            //    Failure
                            console.log("Login Failure!")
                        })
                }
            }])
})();