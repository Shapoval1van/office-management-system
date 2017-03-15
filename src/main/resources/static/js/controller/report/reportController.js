// (function () {
//     angular.module("OfficeManagementSystem")
//         .controller("ReportController", ['$scope','$routeParams', '$location', '$http',
//             function ($scope, $routeParams, $location, $http) {
//
//                 var that = this;
//
//                 that.userId = $routeParams.personId;
//
//                 var person;
//
//                 $http.get("/api/person/" + userId)
//                     .success(function (callback) {
//
//                         $scope.personTest = callback.data;
//                         //person = callback.data;
//                         console.log(person.role);
//                         console.log(person.id);
//                     }, function (callback) {
//
//                     });
//
//                 // $http({
//                 //             method: 'GET',
//                 //             url: '/api/person/' + that.userId
//                 //         }).then(function successCallback(response) {
//                 //             person = response.data;
//                 //             console.log(response.data);
//                 //         }, function errorCallback(response) {
//                 //              console.log(response.data);
//                 //         });
//                 $http.get('/api/person/' + that.userId).
//                 success(function (response) {
//                     that.data = response;
//                     console.log(response,'harray');
//                 }).
//                     error(function(response) {
//                     that.data = response;
//                     console.log(response,'error');
//                 });
//
//                 // $http.get('/api/person/' + userId)
//                 //     .success(function(data) {
//                 //         person = data;
//                 //         console.log(person);
//                 //     })
//                 //     .error(function(data, status) {
//                 //         console.error('Repos error');
//                 //     })
//
//
//                 // var personRole = person.role;
//                 // var personId = person.id;
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
//                 // $scope.getDataForManager = function () {
//                 //     $http({
//                 //         method: 'GET',
//                 //         url: 'api/report/chartsForManager/' + personId
//                 //     }).then(function successCallback(response) {
//                 //         $scope.areaChart.data = response.data;
//                 //     }, function errorCallback(response) {
//                 //
//                 //     });
//                 //
//                 //     $http({
//                 //         method: 'GET',
//                 //         url: 'api/report/chartsForManager/' + personId + '?type=pie'
//                 //     }).then(function successCallback(response) {
//                 //         $scope.pieChart.data = response.data;
//                 //     }, function errorCallback(response) {
//                 //
//                 //     });
//                 // }
//                 //
//                 // $scope.getDataForEmployee = function () {
//                 //     $http({
//                 //         method: 'GET',
//                 //         url: 'api/report/chartsForEmployee/' + person.id
//                 //     }).then(function successCallback(response) {
//                 //         $scope.areaChart.data = response.data;
//                 //         console.log(response.data);
//                 //     }, function errorCallback(response) {
//                 //
//                 //     });
//                 //
//                 //     $http({
//                 //         method: 'GET',
//                 //         url: 'api/report/chartsForEmployee/' + person.id + '?type=pie'
//                 //     }).then(function successCallback(response) {
//                 //         $scope.pieChart.data = response.data;
//                 //         console.log(response.data);
//                 //     }, function errorCallback(response) {
//                 //
//                 //     });
//                 // }
//
//                 // $scope.determiningTheRoleOfUser = function () {
//                 //     if(person.role==3){
//                 //         $scope.getDataForEmployee();
//                 //     } else if(person.role==2){
//                 //         $scope.getDataForManager();
//                 //     } else if(person.role==1){
//                 //         $scope.administratorMessage="You do not have reports! You are administrator!";
//                 //     } else {
//                 //         $scope.undefinedUserMessage="You are not login!";
//                 //     }
//                 // };
//                 //
//                 // $scope.determiningTheRoleOfUser();
//
//             }])
// })();
(function () {
    angular.module("OfficeManagementSystem")
        .controller("ReportController", ['$scope', '$routeParams', '$location', '$http',
            function ($scope, $routeParams, $location, $http) {
                var period1 = "Last Week";
                var period2 = "Last Month";
                var period3 = "Last Year";
                $scope.areaChart = {
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
                    data: []
                };

                $scope.pieChart = {
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
                    "data": []
                };
                var that = this;

                that.userId = $routeParams.personId;

                var person;


                $http({
                        method: 'GET',
                        url: 'api/report/chartsForManager/' + that.userId
                    }).then(function successCallback(response) {
                        $scope.areaChart.data = response.data;
                    }, function errorCallback(response) {
                        $scope.areaChart.data = response.data;
                    });

                $http({
                    method: 'GET',
                    url: '/api/person/' + that.userId
                }).then(function successCallback(response) {
                    $scope.personTest = response.data;
                    //person = callback.data;
                    console.log(person.role);
                    console.log(person.id);
                }, function errorCallback(response) {
                    console.log('dfg');
                });
            }])


})();



