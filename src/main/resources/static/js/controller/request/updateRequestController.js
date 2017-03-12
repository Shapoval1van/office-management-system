(function () {
    angular.module("OfficeManagementSystem")
        .controller("UpdateRequestController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {
                $scope.requestCredentials = {};

                //$scope.selectedManager = undefined;
                //$scope.managers = [];
                //$scope.currentPage = 1;
                //$scope.pageSize = 10;
                $scope.estimateTime = undefined;
                $scope.calendarClick = false;


                var reguestId = $routeParams.requestId;

                function formatDate(dateVal) {
                    var newDate = new Date(dateVal);
                    var sMonth = padValue(newDate.getMonth() + 1);
                    var sDay = padValue(newDate.getDate());
                    var sYear = newDate.getFullYear();
                    var sHour = newDate.getHours();
                    var sMinute = padValue(newDate.getMinutes());
                    var sAMPM = "AM";
                    var iHourCheck = parseInt(sHour);

                    if (iHourCheck > 12) {
                        sAMPM = "PM";
                        sHour = iHourCheck - 12;
                    }
                    else if (iHourCheck === 0) {
                        sHour = "12";
                    }
                    sHour = padValue(sHour);
                    return sMonth + "/" + sDay + "/" + sYear + " " + sHour + ":" + sMinute + " " + sAMPM;
                }

                function padValue(value) {
                    return (value < 10) ? "0" + value : value;
                }

                $scope.getRequestCredential = function () {
                    $http.get("/api/request/" + reguestId )
                        .then(function (callback) {
                            $scope.requestCredentials = callback.data;
                            if ($scope.requestCredentials.estimate!=null){
                                $scope.estimateTime = formatDate($scope.requestCredentials.estimate);
                            }
                            // if ($scope.requestCredentials.manager == null){
                            //     $("#input-manager").parents(".col-md-offset-3").fadeOut();
                            //     $scope.requestCredentials.manager = null;
                            // }
                            // else {
                            //     $scope.selectedManager = $scope.requestCredentials.manager;
                            // }
                            // if ($scope.requestCredentials.requestGroup == null) {
                            //     $("#input-request-group").parents(".col-md-offset-3").fadeOut();
                            // }
                            // else {
                            //     $scope.requestCredentials.requestGroup = $scope.requestCredentials.requestGroup.name;
                            // }
                            if ($scope.requestCredentials.manager!=null) {
                                $scope.requestCredentials.manager = $scope.requestCredentials.manager.id;
                            }
                            if ($scope.requestCredentials.parent!=null) {
                                $scope.requestCredentials.parent = $scope.requestCredentials.parent.id;
                            }
                            if ($scope.requestCredentials.requestGroup!=null) {
                                $scope.requestCredentials.requestGroup = $scope.requestCredentials.requestGroup.id;
                            }
                            $scope.requestCredentials.priority = $scope.requestCredentials.priority.id;
                            $scope.requestCredentials.status = $scope.requestCredentials.status.id;
                            $scope.requestCredentials.employee = $scope.requestCredentials.employee.id;
                        }, function (callback) {
                            console.log(callback)
                        })
                };


                $scope.calendarButtonClick = function(){
                    $scope.calendarClick = true;
                };

                $scope.getRequestCredential();

                $scope.sendRequestCredentials = function () {
                    if ($scope.calendarClick==true) {
                        $scope.requestCredentials.estimate = new Date($('#datetimepicker1').data('date')).getTime();
                    } else if ($scope.estimateTime!=undefined && $scope.calendarClick==false){
                        $scope.requestCredentials.estimate = new Date($scope.estimateTime).getTime();
                    } else if ($scope.estimateTime==undefined){
                        $scope.requestCredentials.estimate = null;
                    }
                    $http.put("/api/request/" + reguestId + "/update/", $scope.requestCredentials)
                        .then(function (callback) {
                            window.location = "/requestListByEmployee";
                        }, function (callback) {
                            console.log("Updating request Failure!");
                            console.log($scope.requestCredentials)
                        })
                };

                // $scope.update = function() {
                //     if($scope.selectedManager.length >= 2) {
                //         console.log($scope.selectedManager);
                //         $http({
                //             method: 'GET',
                //             url: '/api/person/managers/' +  $scope.selectedManager +
                //             '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                //         }).then(function successCallback(response) {
                //             $scope.managers = response.data;
                //             console.log($scope.managers);
                //         }, function errorCallback(response) {
                //         });
                //     }
                // };

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