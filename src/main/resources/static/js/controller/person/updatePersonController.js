(function () {
    angular.module("OfficeManagementSystem")
        .controller("UpdatePersonController", ["$scope", "$http", "$routeParams", "PersonService",
            function ($scope, $http, $routeParams) {
                $scope.person = {};

                var personId = $routeParams.personId;

                $scope.getPersonCredential = function () {
                    $http.get("/api/person/" + personId )
                        .then(function (callback) {
                            $scope.person = callback.data;
                            $scope.person.role = String($scope.person.role.id);
                        }, function (callback) {
                            console.log(callback)
                        })
                };

                $scope.getPersonCredential();

                $scope.sendPersonCredentials = function () {
                    $http.put("/api/person/" + personId, $scope.person)
                        .then(function (callback) {
                            window.location = "javascript:history.back()"
                        }, function (error) {
                            swal("Update Failure!", error.data.errors[0].detail, "error");
                        })
                };
            }])
})();
