(function () {
    angular.module("OfficeManagementSystem")
        .controller("ReportController", ["$scope", "$routeParams", "$location", "$http", "ReportService","FieldFactory",
            function ($scope, $routeParams, $location, $http, ReportService, FieldFactory) {

                $scope.currentPage = 1;
                $scope.pageSize = 10;
                $scope.totalItems = 0;
                $scope.maxSize = 5;

                //var that = this;
                var personId = $routeParams.personId;

                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.userRole = currentUser.role.substr(5, currentUser.role.length);

                $scope.isAdminRadio = false;
                var functionalityOfManager = true;
                var ManagerFunctionality = 2;
                var UserFunctionality = 3;
                $scope.userInput = undefined;
                $scope.userToShow = undefined;

                $scope.isAdministrator = false;
                $scope.isManager = false;
                $scope.isEmployee = false;

                $scope.order = FieldFactory.request.NAME;

                $scope.requestFields = FieldFactory.request;


                //fetch period
                if($location.search().period == undefined){
                    $scope.period = 'month';
                    $scope.title = 'Report by '+$scope.period;
                }else{
                    $scope.period = $location.search().period;
                    if($scope.period!="month"&&$scope.period!="quarter"&&$scope.period!="year"){
                        $scope.period = 'month';
                    }
                    $scope.title = 'Report by '+$scope.period;
                }

                $scope.namesOfPeriod = {
                    "type": "select",
                    "value": $scope.period.substr(0,1).toUpperCase()+$scope.period.substr(1).toLocaleLowerCase(),
                    "values": ["Month", "Quarter", "Year"]
                };

                $scope.namesOfFunctionality = {
                    "type": "select",
                    "value": "Manager",
                    "values": ["Manager", "User"]
                };


                //chose type of first charts
                if($scope.period=='year'){
                    $scope.type = 'column2d';
                }else {
                    $scope.type = 'area2d';
                }

                $scope.getSelectFunc = function (valueOfFunc) {
                    if(valueOfFunc=="Manager"){
                        functionalityOfManager = true;
                        $scope.getData();
                    } else if(valueOfFunc=="User"){
                        functionalityOfManager = false;
                        $scope.getData();
                    }
                };

                $scope.orderRequests = function (fieldName) {
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

                $scope.orderRequestsByName = function () {
                    return $scope.orderRequests(FieldFactory.request.NAME);
                };

                $scope.sortRequestsByEstimate = function () {
                    return $scope.orderRequests(FieldFactory.request.ESTIMATE);
                };

                $scope.sortRequestsByPriority = function () {
                    return $scope.orderRequests(FieldFactory.request.PRIORITY);
                };

                $scope.sortRequestsByCreatingTime = function () {
                    return $scope.orderRequests(FieldFactory.request.CREATE_TIME);
                };

                $scope.sortRequestsByStatus = function () {
                    return $scope.orderRequests(FieldFactory.request.STATUS);
                };

                if ($scope.userRole=="EMPLOYEE"){
                    $scope.isEmployee = true;
                    $scope.getData = function() {
                        $http({
                            method: 'GET',
                            url: 'api/person/' + personId
                        }).then(function (response) {
                            $scope.userToShow = response.data;
                            if ($scope.userToShow.role.id == 3) {
                                $scope.SimpleChartForEmployee();
                                $scope.PieChartForEmployee();
                                $scope.pageChanged(1);
                                $scope.getTotalPage(personId);
                            } else {
                                window.location = "javascript:history.back()";
                            }
                        }, function errorCallback(response) {
                        });
                    };
                } else if ($scope.userRole=="OFFICE MANAGER"){
                    $scope.isManager = true;
                    $scope.getData = function() {
                        $http({
                            method: 'GET',
                            url: 'api/person/' + personId
                        }).then(function (response) {
                            $scope.userToShow = response.data;
                            if ($scope.userToShow.role.id == 2) {
                                $scope.SimpleChartForManager();
                                $scope.PieChartForManager();
                                $scope.pageChanged(1);
                                $scope.getTotalPage(personId);
                            } else {
                                window.location = "javascript:history.back()";
                            }
                        }, function errorCallback(response) {
                        });
                    };
                } else if ($scope.userRole=="ADMINISTRATOR"){
                    $scope.isAdministrator = true;
                    $scope.isAdminRadio = true;
                    $scope.getData = function() {
                        $http({
                            method: 'GET',
                            url: 'api/person/' + personId
                        }).then(function (response) {
                            $scope.userToShow = response.data;
                            if ($scope.userToShow.role.id == 3) {
                                $scope.isAdminRadio = false;
                                $scope.SimpleChartForEmployee();
                                $scope.PieChartForEmployee();
                                $scope.pageChanged(1);
                                $scope.getTotalPage(personId);
                            } else if($scope.userToShow.role.id == 2) {
                                $scope.isAdminRadio = false;
                                $scope.SimpleChartForManager();
                                $scope.PieChartForManager();
                                $scope.pageChanged(1);
                                $scope.getTotalPage(personId);
                            }else if($scope.userToShow.role.id == 1) {
                                if(functionalityOfManager){
                                    $scope.SimpleChartForAdmin(ManagerFunctionality);
                                    $scope.PieChartForAdmin(ManagerFunctionality);
                                    $scope.pageChanged(1);
                                    $scope.getTotalPage(personId);
                                }else if(!functionalityOfManager){
                                    $scope.SimpleChartForAdmin(UserFunctionality);
                                    $scope.PieChartForAdmin(UserFunctionality);
                                     //
                                    $scope.pageChanged(1);
                                    $scope.getTotalPage(personId);
                                }
                            }else {
                                window.location = "javascript:history.back()";
                            }
                        }, function errorCallback(response) {
                        });
                    };
                }

                $scope.getData();




                $scope.getTotalPage = function (personId) {
                    if ($scope.userToShow.role.id == 1){
                        if(functionalityOfManager){
                            var functionality = ManagerFunctionality;
                        }else {
                            var functionality = UserFunctionality;
                        }
                        return ReportService.getTotalPageAdmin(personId, $scope.period.toLowerCase(), functionality )
                            .then(function successCallback(response) {
                                $scope.totalItems = response.data;
                            }, function (response) {
                            })
                    } else {
                        return ReportService.getTotalPage(personId, $scope.period.toLowerCase())
                            .then(function successCallback(response) {
                                $scope.totalItems = response.data;
                            }, function (response) {
                            })
                    }
                };

                $scope.pageChanged = function(currentPage) {
                    if(currentPage!=undefined){
                        $scope.currentPage = currentPage;
                    }

                    if($scope.userToShow.role.id == 1){
                        if(functionalityOfManager){
                            var functionality = ManagerFunctionality;
                        }else {
                            var functionality = UserFunctionality;
                        }
                        return ReportService.pageChangedAdmin(personId , $scope.currentPage ,$scope.period.toLowerCase(), $scope.pageSize, functionality, $scope.order)
                            .then(function successCallback(response) {
                                $scope.requestList =  response.data;
                            }, function (response) {
                                $scope.requestList = [];
                            })
                    } else {
                        return ReportService.pageChanged(personId , $scope.currentPage ,$scope.period.toLowerCase(), $scope.pageSize, $scope.order)
                            .then(function successCallback(response) {
                                $scope.requestList =  response.data;
                            }, function (response) {
                                $scope.requestList = [];
                            })
                    }
                };

                $scope.SimpleChartForManager = function () {
                    return ReportService.getSimpleChartForManager(personId, $scope.period.toLowerCase())
                        .then(function successCallback(response) {
                            $scope.reportForTime.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.PieChartForManager = function () {
                    return ReportService.getPieChartForManager(personId, $scope.period.toLowerCase())
                        .then(function successCallback(response) {
                            $scope.pieChart.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.SimpleChartForAdmin = function (functionality) {
                    return ReportService.getSimpleChartForAdmin(personId, $scope.period.toLowerCase(), functionality)
                        .then(function successCallback(response) {
                            $scope.reportForTime.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.PieChartForAdmin = function (functionality) {
                    return ReportService.getPieChartForAdmin(personId, $scope.period.toLowerCase(), functionality)
                        .then(function successCallback(response) {
                            $scope.pieChart.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.SimpleChartForEmployee = function () {
                    return ReportService.getSimpleChartEmployee(personId, $scope.period.toLowerCase())
                        .then(function successCallback(response) {
                            $scope.reportForTime.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.PieChartForEmployee = function () {
                    return ReportService.getPieChartEmployee(personId, $scope.period.toLowerCase())
                        .then(function successCallback(response) {
                            $scope.pieChart.data = response.data;
                        }, function (callback) {
                        })
                };


                $scope.getPeriodData = function (periodItem) {
                    $scope.period=periodItem.toLowerCase();
                    window.location = "/secured/employee/report/"+ personId +"?period=" + periodItem.toLowerCase();
                };

                $scope.getUser = function (userInput) {
                    window.location = "/secured/employee/report/"+ userInput.id
                };

                $scope.updateUser = function() {
                    if($scope.userInput.length >= 2) {
                        $http({
                            method: 'GET',
                            url: '/api/person/users/' +  $scope.userInput +
                            '?page=' +  $scope.currentPage + '&size=' + $scope.pageSize
                        }).then(function successCallback(response) {
                            $scope.users = response.data;
                        }, function errorCallback(response) {
                        });
                    }
                };

                $scope.goToRequestDetailsPage = function (requestId) {
                    window.location ="/secured/employee/request/" + requestId + "/details";
                };



                $scope.reportForTime  = {
                    "chart": {
                        "caption": "The number of requests for a period of time",
                        "xAxisName": "Data",
                        "yAxisName": "Count of requests",
                        "paletteColors": "#0075c2",
                        "bgColor": "#f4f4f4",
                        "bgAlpha": "100",
                        "showBorder": "0",
                        "showCanvasBorder": "0",
                        "plotBorderAlpha": "10",
                        "usePlotGradientColor": "0",
                        "plotFillAlpha": "50",
                        "showXAxisLine": "1",
                        "axisLineAlpha": "25",
                        "divLineAlpha": "10",
                        "showValues": "1",
                        "showAlternateHGridColor": "0",
                        "captionFontSize": "14",
                        "subcaptionFontSize": "14",
                        "subcaptionFontBold": "0",
                        "toolTipColor": "#ffffff",
                        "toolTipBorderThickness": "0",
                        "toolTipBgColor": "#000000",
                        "toolTipBgAlpha": "80",
                        "toolTipBorderRadius": "2",
                        "toolTipPadding": "5"
                    },
                    data: [

                    ]
                };

                $scope.pieChart = {
                    "chart": {
                        "caption": "Report on the status of the request",
                        "paletteColors": "#0075c2,#1aaf5d,#f2c500,#f45b00,#8e0000",
                        "bgColor": "#f4f4f4",
                        "bgAlpha": "100",
                        "showBorder": "0",
                        "use3DLighting": "0",
                        "showShadow": "0",
                        "enableSmartLabels": "0",
                        "startingAngle": "0",
                        "showPercentValues": "1",
                        "showPercentInTooltip": "0",
                        "decimals": "1",
                        "captionFontSize": "14",
                        "subcaptionFontSize": "14",
                        "subcaptionFontBold": "0",
                        "toolTipColor": "#ffffff",
                        "toolTipBorderThickness": "0",
                        "toolTipBgColor": "#000000",
                        "toolTipBgAlpha": "80",
                        "toolTipBorderRadius": "2",
                        "toolTipPadding": "5",
                        "showHoverEffect": "1",
                        "showLegend": "1",
                        "legendBgColor": "#ffffff",
                        "legendBorderAlpha": "0",
                        "legendShadow": "0",
                        "legendItemFontSize": "10",
                        "legendItemFontColor": "#666666",
                        "useDataPlotColorForLabels": "1"
                    },
                    "data": [
                    ]
                };

            }])
})();