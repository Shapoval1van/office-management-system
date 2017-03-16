(function () {
    angular.module("OfficeManagementSystem")
        .controller("UpdatePersonController", ["$scope", "$http", "$routeParams", "PersonService",
            function ($scope, $http, $routeParams, PersonService) {
                $scope.person = {};

                var personId = $routeParams.personId;

                $scope.getPersonCredential = function () {
                    PersonService.getPersonById(personId)
                        .then(function (callback) {
                            $scope.person = callback.data;
                            $scope.person.role = $scope.person.role.id;
                        }, function (error) {
                            console.log(error)
                        })
                };

                $scope.getPersonCredential();

                $scope.sendPersonCredentials = function () {

                    $http.put("/api/person/" + personId + "/update", $scope.person)
                        .then(function (callback) {
                            window.location = "/users";
                        }, function (error) {
                            console.log(error);
                            swal("Updare Failure!", error.data.message, "error");
                            console.log($scope.person)
                        })
                };

            }])
})();
