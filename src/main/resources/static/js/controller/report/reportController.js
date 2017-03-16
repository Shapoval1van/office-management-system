(function () {
    angular.module("OfficeManagementSystem")
        .controller("ReportController", ['$scope', '$routeParams', '$location', '$http',
            function ($scope, $routeParams, $location, $http) {

                var that = this;
                var personId = $routeParams.personId;
                var period;
                if($location.search().period == undefined){
                    period = 'month'
                }else{
                    period = $location.search().period;
                }
                $scope.title = 'Report by '+period;
                $http({
                    method: 'GET',
                    url: 'api/person/'+personId
                }).then(function successCallback(response){
                    that.person = response.data;
                        if(that.person.role==3){
                            $http({
                                method: 'GET',
                                url: 'api/report/chartsForEmployee/' + personId + '?period=' + period
                            }).then(function successCallback(response) {
                                $scope.areaChart.data = response.data;
                            }, function errorCallback(response) {

                            });
                            $http({
                                method: 'GET',
                                url: 'api/report/chartsForEmployee/' + personId + '?type=pie' + '&period=' + period
                            }).then(function successCallback(response) {
                                $scope.pieChart.data = response.data;
                            }, function errorCallback(response) {

                            });
                        } else if (that.person.role == 2) {
                            $http({
                                method: 'GET',
                                url: 'api/report/chartsForManager/' + personId + '?period=' + period
                            }).then(function successCallback(response) {
                                $scope.areaChart.data = response.data;
                            }, function errorCallback(response) {
                            });

                            $http({
                                method: 'GET',
                                url: 'api/report/chartsForManager/' + personId + '?type=pie' + '&period=' + period
                            }).then(function successCallback(response) {
                                $scope.pieChart.data = response.data;
                            }, function errorCallback(response) {

                            });
                        } else if(that.person.role==1){
                            $scope.administratorMessage="You do not have reports! You are administrator!";
                        } else {
                            $scope.undefinedUserMessage="You are not login!";
                        }
                }, function errorCallback(response) {

                });

                $scope.areaChart  = {
                    "chart": {
                        "caption": "The number of requests for a period of time",
                        "xAxisName": "Data",
                        "yAxisName": "Count of requests",
                        "paletteColors": "#0075c2",
                        "bgColor": "#c7c7c7",
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
                        "bgColor": "#c7c7c7",
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

                $http({
                    method: 'GET',
                    url: 'api/report/allRequest/'+personId
                }).then(function successCallback(response) {
                    $scope.requestList = response.data;
                }, function errorCallback(response) {
                    $scope.requestList = [];
                });
            }])
})();