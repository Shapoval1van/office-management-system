(function () {
    angular.module("OfficeManagementSystem")
        .controller("PersonListController", ["$scope", "$http",
            function ($scope, $http) {

                var personDetails = "/person/";
                $scope.pageSize = 10;
                $scope.persons = {};
                $scope.roles = [{roleId: 4, name: 'ALL'},
                    {roleId: 1, name: 'ADMINISTRATOR'},
                    {roleId: 2, name: 'OFFICE MANAGER'},
                    {roleId: 3, name: 'EMPLOYEE'}]; // TODO need controller for roles
                $scope.maxSize = 5;
                $scope.totalItems = 0;
                $scope.currentPage = 1;
                $scope.selectedRole = $scope.roles[0];
                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.isUndefined = function (thing) {
                    return (typeof thing === "undefined");
                };


                $scope.getTotalPage = function() {
                    $http({
                        method: 'GET',
                        url: '/api/person/count/' + $scope.selectedRole.roleId
                    }).then(function successCallback(response) {
                        $scope.totalItems = response.data;
                    }, function errorCallback(response) {
                    });
                };

                $scope.pageChanged = function() {
                    $http({
                        method: 'GET',
                        url: '/api/person/list/' + $scope.selectedRole.roleId +
                        '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                    }).then(function successCallback(response) {
                        $scope.persons = [];
                        $scope.persons = response.data;
                    }, function errorCallback(response) {
                    });
                };

                $scope.getTotalPage(); //
                $scope.pageChanged(1); // get first page

                $scope.isSelected = function(roleId) {
                    return roleId === $scope.selectedRole.roleId;
                };

                $scope.roleChange = function(roleId) {
                    $scope.getTotalPage(); //
                    $scope.pageChanged(1); // get first page
                };

                $scope.personUpdate = function(personId) {
                    window.location = personDetails + personId + '/update';
                };


                $scope.personDelete = function(person) {
                    if (person.email === $scope.currentUser.email)
                        window.alert("You cannot delete yourself")
                    else{
                        $scope.person = person;
                        $http.post("/api/person/deletePerson", person.email, $scope.currentUser).
                        then(function successCallback(response) {
                            $scope.persons =$scope.persons.filter(function(person) {
                                return person.email !== $scope.person.email;
                            });
                        }, function errorCallback(response) {
                            console.log(response);
                        });
                    }

                };

            }])
})();

