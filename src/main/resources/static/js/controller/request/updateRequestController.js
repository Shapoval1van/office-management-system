(function () {
    angular.module("OfficeManagementSystem")
        .controller("UpdateRequestController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {
                $scope.requestCredentials = {
                    priority:'2'
                };

                $scope.selectedManager = undefined;
                $scope.managers = [];
                $scope.currentPage = 1;
                $scope.pageSize = 10;

                var reguestId = $routeParams.requestId;

                $scope.getRequestCredential = function () {
                    $http.get("/api/request/" + reguestId )
                        .then(function (callback) {
                            $scope.requestCredentials = callback.data;
                            if ($scope.requestCredentials.manager == null){
                                $("#input-manager").parents(".col-md-offset-3").fadeOut();
                            }
                            else $scope.selectedManager = $scope.requestCredentials.manager;
                            if ($scope.requestCredentials.requestGroup == null) {
                                $("#input-request-group").parents(".col-md-offset-3").fadeOut();
                            }
                            else $scope.requestCredentials.requestGroup = $scope.requestCredentials.requestGroup.name;
                            $scope.requestCredentials.priority = $scope.requestCredentials.priority.id;
                        }, function (callback) {
                            console.log(callback)
                        })
                };


                $scope.getRequestCredential();

                $scope.requestNameCheck = function () {
                    var nameValue = $scope.requestCredentials.name,
                        regExp = /^[A-Z][a-zA-Z\d\s]{2,50}$/;
                    if (nameValue){
                        var lookFor = nameValue.search(regExp);
                        if (lookFor == -1){
                            $("#request-name-input-group").addClass("request-name-incorrect");
                            $("#request-description-field").addClass("form-control_offset");
                        }else{
                            $("#request-name-input-group").removeClass("request-name-incorrect");
                            $("#request-description-field").removeClass("form-control_offset");
                        }
                    }
                };

                $scope.sendRequestCredentials = function () {
                    $scope.requestCredentials.estimate = new Date($('#datetimepicker1').data('date')).getTime();
                    $scope.requestCredentials.status = $scope.requestCredentials.status.id;
                    $scope.requestCredentials.employee = $scope.requestCredentials.employee.id;
                    if ($scope.requestCredentials.manager!=null) {
                        $scope.requestCredentials.manager = $scope.selectedManager.id;
                    }
                    $http.put("/api/request/" + reguestId + "/update/", $scope.requestCredentials)
                        .then(function (callback) {
                        }, function (callback) {
                            console.log("Updating request Failure!");
                            console.log($scope.requestCredentials)
                        })
                };

                $scope.update = function() {
                    if($scope.selectedManager.length >= 2) {
                        console.log($scope.selectedManager);
                        $http({
                            method: 'GET',
                            url: '/api/person/managers/' +  $scope.selectedManager +
                            '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                        }).then(function successCallback(response) {
                            $scope.managers = response.data;
                            console.log($scope.managers);
                        }, function errorCallback(response) {
                        });
                    }
                };

                $scope.updateStatus = function (status) {
                    var updateStatus = $scope.requestCredentials.status.id;
                    switch (status){
                        case 'begin':
                            updateStatus = 2;
                            break;
                        case 'finish':
                            updateStatus = 3;
                            break;
                        case 'reopen':
                            updateStatus = 1;
                            break;
                        case 'cancel':
                            updateStatus = 4;
                            break;
                    }
                    $scope.requestCredentials.status = updateStatus;

                    $scope.requestCredentials.estimate = $scope.requestCredentials.estimate;
                    $scope.requestCredentials.employee = $scope.requestCredentials.employee.id;
                    $scope.requestCredentials.manager = $scope.selectedManager.id;

                    $http.put("/api/request/" + reguestId + "/update", $scope.requestCredentials)
                        .then(function (callback) {

                        }, function (callback) {
                            console.log("Changing status failure!");
                            console.log($scope.requestCredentials)
                        });
                };

            }])
})();
