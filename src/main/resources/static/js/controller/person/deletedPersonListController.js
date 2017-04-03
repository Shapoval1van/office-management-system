(function () {
    angular.module("OfficeManagementSystem")
        .controller("DeletedPersonListController", ["$scope", "$http", "$rootScope", "FieldFactory",
            function ($scope, $http, $rootScope, FieldFactory) {

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

                $scope.order = FieldFactory.person.FIRST_NAME;
                $scope.personFields = FieldFactory.person;

                $scope.orderPersons = function (fieldName) {
                    if (FieldFactory.isDescOrder($scope.order, fieldName))
                        $scope.order = FieldFactory.removeSortField($scope.order, fieldName);
                    else
                        $scope.order = FieldFactory.toggleOrder($scope.order, fieldName);
                    return $scope.pageChanged();
                };

                $scope.isDescOrder = function (fieldName) {
                    return FieldFactory.isDescOrder($scope.order, fieldName);
                };

                $scope.isAscOrder = function (fieldName) {
                    return FieldFactory.isAscOrder($scope.order, fieldName);
                };

                $scope.orderPersonsByFirstName = function () {
                    return $scope.orderPersons(FieldFactory.person.FIRST_NAME);
                };

                $scope.orderPersonsByLastName = function () {
                    return $scope.orderPersons(FieldFactory.person.LAST_NAME);
                };

                $scope.orderPersonsByEmail = function () {
                    return $scope.orderPersons(FieldFactory.person.EMAIL);
                };

                $scope.orderPersonsByRole = function () {
                    return $scope.orderPersons(FieldFactory.person.ROLE);
                };

                $scope.isUndefined = function (thing) {
                    return (typeof thing === "undefined");
                };

                $scope.pageChanged = function () {
                    if ($scope.selectedRole.roleId == 4) {
                        $http({
                            method: 'GET',
                            url: '/api/person/deleted-list' +
                            '?page=' + $scope.currentPage + '&size=' + $scope.pageSize + "&sort=" + $scope.order
                        }).then(function successCallback(response) {
                            $scope.persons = [];
                            $scope.persons = response.data.data;
                            $scope.totalItems = response.data.totalElements;
                        }, function errorCallback(response) {
                        });
                    } else {
                        $http({
                            method: 'GET',
                            url: '/api/person/deleted-list/' + $scope.selectedRole.roleId +
                            '?page=' + $scope.currentPage + '&size=' + $scope.pageSize + "&sort=" + $scope.order
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

                $scope.roleChange = function (roleId) {
                    $scope.getTotalPage();
                    $scope.pageChanged(1);
                };


                $scope.isSelected = function (roleId) {
                    return roleId === $scope.selectedRole.roleId;
                };


                $scope.personRecover = function (person) {
                    $scope.person = person;
                    $http.post("/api/person/recoverPerson", person.email, $scope.currentUser).then(function successCallback(response) {
                        swal("User recovered!");
                        $scope.persons = $scope.persons.filter(function (person) {
                            return person.email !== $scope.person.email;
                        });
                    }, function errorCallback(response) {
                        console.log(response);
                    });


                };

            }])
})();

