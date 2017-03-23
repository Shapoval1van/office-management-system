(function () {
    angular.module("OfficeManagementSystem")
        .controller("ReportController", ["$scope", "$routeParams", "$location", "$http", "ReportService",
            function ($scope, $routeParams, $location, $http, ReportService) {
                $scope.currentPage = 1;
                $scope.pageSize = 10;
                $scope.totalItems = 0;
                $scope.maxSize = 5;
                var that = this;
                var personId = $routeParams.personId;

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


                //chose type of first charts
                if($scope.period=='year'){
                    $scope.type = 'column2d';
                }else {
                    $scope.type = 'area2d';
                }


                $scope.getTotalPage = function () {
                    return ReportService.getTotalPage(personId, $scope.period.toLowerCase())
                        .then(function (response) {
                            $scope.totalItems = response.data;
                        }, function (response) {
                        })
                };


                $scope.pageChanged = function(currentPage) {
                    return ReportService.pageChanged(personId , currentPage ,$scope.period.toLowerCase(), $scope.pageSize)
                        .then(function (response) {
                            $scope.requestList =  response.data;
                        }, function (response) {
                            $scope.requestList = [];
                        })
                };

                $scope.SimpleChartForManager = function () {
                    return ReportService.getSimpleChartForManager(personId, $scope.period.toLowerCase())
                        .then(function (response) {
                            $scope.reportForTime.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.PieChartForManager = function () {
                    return ReportService.getPieChartForManager(personId, $scope.period.toLowerCase())
                        .then(function (response) {
                            $scope.pieChart.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.SimpleChartForEmployee = function () {
                    return ReportService.getSimpleChartEmployee(personId, $scope.period.toLowerCase())
                        .then(function (response) {
                            $scope.reportForTime.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.PieChartForEmployee = function () {
                    return ReportService.getPieChartEmployee(personId, $scope.period.toLowerCase())
                        .then(function (response) {
                            $scope.pieChart.data = response.data;
                        }, function (callback) {
                        })
                };

                $scope.getAllData = function () {
                    $http({
                        method: 'GET',
                        url: 'api/person/' + personId
                    }).then(function successCallback(response) {
                        that.person = response.data;
                        if (that.person.role.id == 3) {
                            $scope.SimpleChartForEmployee();
                            $scope.PieChartForEmployee();
                        } else if (that.person.role.id == 2) {
                            $scope.SimpleChartForManager();
                            $scope.PieChartForManager();
                        } else if (that.person.role.id == 1) {
                            $scope.SimpleChartForManager();
                            $scope.PieChartForManager();
                            //window.location = "javascript:history.back()";
                        } else {
                            window.location = "javascript:history.back()";
                        }
                    }, function errorCallback(response) {

                    });
                };

                $scope.getAllData();

                $scope.getPeriodData = function (periodItem) {
                    $scope.period=periodItem.toLowerCase();
                    window.location = "/secured/report/"+ personId +"?period=" + periodItem.toLowerCase();
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

                $scope.pageChanged(1);

                $scope.getTotalPage(personId, $scope.period.toLowerCase()); //
            }])
})();