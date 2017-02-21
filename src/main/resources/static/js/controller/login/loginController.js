/**
 * Created by Max on 21.02.2017.
 */
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
                        }, function (callback) {
                            //    Failure
                        })
                };

                //FIXME: Change url, object and event on success and failure
                $scope.sendRecoverRequest = function () {
                    $http.put("/some_url/", $scope.recoverEmail)
                        .then(function (callback) {
                            //    Success
                        }, function (callback) {
                            //    Failure
                        })
                }
            }])
})();