(function () {
    angular.module("OfficeManagementSystem")
        .controller("ReportController", ['$scope', '$routeParams', '$location', '$http',
            function ($scope, $routeParams, $location, $http) {

                $scope.currentUser = JSON.parse(localStorage.getItem("currentUser"));

                var period1 = "Last Week";
                var period2 = "Last Month";
                var period3 = "Last Year";

                $scope.myDataSource = {
                    "chart": {
                        "caption": "The number of requests for a period of time.",
                        "subCaption": period1,
                        "xAxisName": "Day",
                        "yAxisName": "Count of requests",
                        "numberPrefix": "r",
                        "paletteColors": "#0075c2",
                        "bgColor": "#ffffff",
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

                $scope.myDataSource1 = {
                    "chart": {
                        "caption": "Report on the status of the request",
                        "subCaption": period2,
                        "paletteColors": "#0075c2,#1aaf5d,#f2c500,#f45b00,#8e0000",
                        "bgColor": "#ffffff",
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

                $scope.getDataForManager = function () {
                    $http({
                        method: 'GET',
                        url: 'api/report/chartsForManager/'+$scope.currentUser.id
                    }).then(function successCallback(response) {
                        $scope.myDataSource.data = response.data;
                    }, function errorCallback(response) {

                    });

                    $http({
                        method: 'GET',
                        url: 'api/report/chartsForManager/'+$scope.currentUser.id+'?type=pie'
                    }).then(function successCallback(response) {
                        $scope.myDataSource1.data = response.data;
                    }, function errorCallback(response) {

                    });
                }

                $scope.getDataForEmployee = function () {
                    $http({
                        method: 'GET',
                        url: 'api/report/chartsForEmployee/'+$scope.currentUser.id
                    }).then(function successCallback(response) {
                        $scope.myDataSource.data = response.data;
                    }, function errorCallback(response) {

                    });

                    $http({
                        method: 'GET',
                        url: 'api/report/chartsForEmployee/'+$scope.currentUser.id+'?type=pie'
                    }).then(function successCallback(response) {
                        $scope.myDataSource1.data = response.data;
                    }, function errorCallback(response) {

                    });
                }

                $scope.determiningTheRoleOfUser = function () {
                    if($scope.currentUser.role=='ROLE_EMPLOYEE'){
                        $scope.getDataForEmployee();
                    } else if($scope.currentUser.role=='ROLE_OFFICE MANAGER'){
                        $scope.getDataForManager();
                    } else if($scope.currentUser.role=='ROLE_ADMINISTRATOR'){
                        $scope.administratorMessage="You do not have reports! You are administrator!";
                    } else {
                        $scope.undefinedUserMessage="You are not login!";
                    }
                };

                $scope.determiningTheRoleOfUser();

            }])
})();




// (function () {
//     angular.module("OfficeManagementSystem")
//         .controller("ReportController", ['$scope', '$routeParams', '$location', '$http',
//             function ($scope, $routeParams, $location, $http) {
//
//                 //$scope.person;
//
//                 var userId = $routeParams.personId;
//
//                 var personId = 0;
//                 var personRole = 0;
//
//                 $scope.getPerson = function () {
//                     $http.get('api/person/' + userId)
//                         .then(function (callback) {
//                             //$scope.personTest = callback.data;
//                             $scope.person = callback.data;
//                             personRole = $scope.person.role;
//                             personId = $scope.person.id;
//                             console.log(personRole);
//                             console.log(personId);
//                         }, function (callback) {
//                             console.log(callback);
//                         });
//                 }
//                 $scope.getPerson();
//
//                 // var personRole = $scope.person.role;
//                 // var personId = $scope.person.id;
//
//                 var period1 = "Last Week";
//                 var period2 = "Last Month";
//                 var period3 = "Last Year";
//
//
//                 $scope.areaChart = {
//                     "chart": {
//                         "caption": "The number of requests for a period of time.",
//                         "subCaption": period1,
//                         "xAxisName": "Day",
//                         "yAxisName": "Count of requests",
//                         "numberPrefix": "r",
//                         "paletteColors": "#0075c2",
//                         "bgColor": "#ffffff",
//                         "showBorder": "0",
//                         "showCanvasBorder": "0",
//                         "plotBorderAlpha": "10",
//                         "usePlotGradientColor": "0",
//                         "plotFillAlpha": "50",
//                         "showXAxisLine": "1",
//                         "axisLineAlpha": "25",
//                         "divLineAlpha": "10",
//                         "showValues": "1",
//                         "showAlternateHGridColor": "0",
//                         "captionFontSize": "14",
//                         "subcaptionFontSize": "14",
//                         "subcaptionFontBold": "0",
//                         "toolTipColor": "#ffffff",
//                         "toolTipBorderThickness": "0",
//                         "toolTipBgColor": "#000000",
//                         "toolTipBgAlpha": "80",
//                         "toolTipBorderRadius": "2",
//                         "toolTipPadding": "5"
//                     },
//                     data: []
//                 };
//
//                 $scope.pieChart = {
//                     "chart": {
//                         "caption": "Report on the status of the request",
//                         "subCaption": period2,
//                         "paletteColors": "#0075c2,#1aaf5d,#f2c500,#f45b00,#8e0000",
//                         "bgColor": "#ffffff",
//                         "showBorder": "0",
//                         "use3DLighting": "0",
//                         "showShadow": "0",
//                         "enableSmartLabels": "0",
//                         "startingAngle": "0",
//                         "showPercentValues": "1",
//                         "showPercentInTooltip": "0",
//                         "decimals": "1",
//                         "captionFontSize": "14",
//                         "subcaptionFontSize": "14",
//                         "subcaptionFontBold": "0",
//                         "toolTipColor": "#ffffff",
//                         "toolTipBorderThickness": "0",
//                         "toolTipBgColor": "#000000",
//                         "toolTipBgAlpha": "80",
//                         "toolTipBorderRadius": "2",
//                         "toolTipPadding": "5",
//                         "showHoverEffect": "1",
//                         "showLegend": "1",
//                         "legendBgColor": "#ffffff",
//                         "legendBorderAlpha": "0",
//                         "legendShadow": "0",
//                         "legendItemFontSize": "10",
//                         "legendItemFontColor": "#666666",
//                         "useDataPlotColorForLabels": "1"
//                     },
//                     "data": []
//                 };
//
//
//                 $scope.determiningTheRoleOfUser = function () {
//                     if (personRole == '3') {
//                         console.log(personRole);
//                         $scope.getDataForEmployee();
//                     } else if (personRole == 2) {
//                         $scope.getDataForManager();
//                     } else if (personRole== 1) {
//                         $scope.administratorMessage = "You do not have reports! You are administrator!";
//                     } else {
//                         $scope.undefinedUserMessage = "You are not login!";
//                     }
//                 };
//
//                 $scope.determiningTheRoleOfUser();
//
//                 $scope.getDataForManager = function () {
//                     $http({
//                         method: 'GET',
//                         url: 'api/report/chartsForManager/' + personId
//                     }).then(function successCallback(response) {
//                         $scope.areaChart.data = response.data;
//                     }, function errorCallback(response) {
//
//                     });
//
//                     $http({
//                         method: 'GET',
//                         url: 'api/report/chartsForManager/' + personId + '?type=pie'
//                     }).then(function successCallback(response) {
//                         $scope.pieChart.data = response.data;
//                     }, function errorCallback(response) {
//
//                     });
//                 }
//
//                 $scope.getDataForEmployee = function () {
//
//                     $http({
//                         method: 'GET',
//                         url: 'api/report/chartsForEmployee/' + personId
//                     }).then(function successCallback(response) {
//                         $scope.areaChart.data = response.data;
//                         console.log(response.data);
//                     }, function errorCallback(response) {
//
//                     });
//
//                     $http({
//                         method: 'GET',
//                         url: 'api/report/chartsForEmployee/' + personId + '?type=pie'
//                     }).then(function successCallback(response) {
//                         $scope.pieChart.data = response.data;
//                         console.log(response.data);
//                     }, function errorCallback(response) {
//
//                     });
//                 }
//
//
//             }])
// })();
//
