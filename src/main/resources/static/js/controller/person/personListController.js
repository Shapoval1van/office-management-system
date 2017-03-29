(function () {
    angular.module("OfficeManagementSystem")
        .controller("PersonListController", ["$scope", "$http", "$rootScope",
            function ($scope, $http, $rootScope) {

                var personDetails = "/secured/employee/person";
                var personUpdate = "/secured/admin/person";
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

                $rootScope.sideBarActiveElem = "users";

                $scope.isUndefined = function (thing) {
                    return (typeof thing === "undefined");
                };

                $scope.pageChanged = function () {
                    if ($scope.selectedRole.roleId==4){
                        $http({
                            method: 'GET',
                            url: '/api/person/list' +
                            '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                        }).then(function successCallback(response) {
                            $scope.persons = [];
                            $scope.persons = response.data.data;
                            $scope.totalItems = response.data.totalElements;
                        }, function errorCallback(response) {
                        });
                    } else {
                        $http({
                            method: 'GET',
                            url: '/api/person/list/' + $scope.selectedRole.roleId +
                            '?page=' + $scope.currentPage + '&size=' + $scope.pageSize
                        }).then(function successCallback(response) {
                            $scope.persons = [];
                            $scope.persons = response.data.data;
                            $scope.totalItems = response.data.totalElements;
                        }, function errorCallback(response) {
                        });
                    }
                };
                $scope.getTotalPage = function () {
                    return $scope.totalItems;
                };

                $scope.getTotalPage();
                $scope.pageChanged(1);

                $scope.roleChange = function(roleId) {
                    $scope.getTotalPage();
                    $scope.pageChanged(1);
                };


                $scope.isSelected = function(roleId) {
                    return roleId === $scope.selectedRole.roleId;
                };

                $scope.personUpdate = function(personId) {
                    window.location = personUpdate + personId + '/update';
                };
                $scope.personDelete = function(person) {
                        $scope.person = person;
                        $http.post("/api/person/deletePerson", person.email, $scope.currentUser).
                        then(function successCallback(response) {
                            if (response.data.deleted === true){
                                $scope.persons =$scope.persons.filter(function(person) {
                                    return person.email !== $scope.person.email;
                                });
                                swal(response.data.message);
                            } else{
                                swal(response.data.message);
                            }

                        }, function errorCallback(response) {
                            console.log(response);
                        });
                    };
                $scope.goToPersonDetailsPage = function (personId) {
                    $scope.goToUrl("/secured/employee/person/" + personId + "/details");
                };

            }])
})();

