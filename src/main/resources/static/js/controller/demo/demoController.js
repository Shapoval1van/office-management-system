/**
 * Created by Max on 23.02.2017.
 */
(function () {
    angular.module("OfficeManagementSystem")
        .controller("DemoController", ["$scope", "$http", "$cookies",
            function ($scope, $http, $cookies) {

                $scope.message = "";

                $scope.token = 'Bearer ' + $cookies.get("access_token");

                $http.get("/secured", {
                    headers: {
                        "Authorization": $scope.token
                    }
                })
                    .then(function (callback) {
                        console.log("Success");
                        $scope.message = callback.data.message;
                        $scope.message = "Success";
                    }, function (callback) {
                        $scope.message = "Fail";
                    })

            }])
})();