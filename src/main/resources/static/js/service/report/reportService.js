(function () {
    angular.module("OfficeManagementSystem")
        .service("ReportService", ["$http",
            function ($http) {
                var reportService = {};

                reportService.getTotalPage = function (personId, period) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/count/allRequest/' + personId + '?period=' + period
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {
                    });
                };


                reportService.pageChanged = function(personId, currentPage, period, pageSize, order) {
                    var requestUrl = "api/report/allRequest/"+personId+"?period="+ period
                        +"&page=" +  currentPage + "&size=" + pageSize;
                    if (!!order)
                        requestUrl += "&sort=" + order;

                    return $http({
                        method: 'GET',
                        url: requestUrl
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {
                    });
                };

                reportService.getTotalPageAdmin = function (personId, period , functionality) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/admin/count/allRequest/' + personId + '?period=' + period + '&role=' + functionality
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {
                    });
                };


                reportService.pageChangedAdmin = function(personId, currentPage, period, pageSize, functionality, order) {
                    var requestUrl = "api/report/admin/allRequest/"+personId+"?period="+ period
                        +"&page=" +  currentPage + "&size=" + pageSize + "&role=" + functionality;
                    if (!!order)
                        requestUrl += "&sort=" + order;

                    return $http({
                        method: 'GET',
                        url: requestUrl
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {
                    });
                };

                reportService.getSimpleChartForManager = function (personId, period) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/chartsForManager/' + personId + '?period=' + period
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {
                    });

                };

                reportService.getPieChartForManager = function (personId, period) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/chartsForManager/' + personId + '?type=pie' + '&period=' + period
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {

                    });
                };

                reportService.getSimpleChartForAdmin = function (personId, period, functionality) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/chartsForAdmin/' + personId + '?period=' + period + '&role=' + functionality
                    }).then(function successCallback(response) {
                        console.log(functionality);
                        return response;
                    }, function errorCallback(response) {
                    });

                };

                reportService.getPieChartForAdmin = function (personId, period, functionality) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/chartsForAdmin/' + personId + '?type=pie' + '&period=' + period + '&role=' + functionality
                    }).then(function successCallback(response) {
                        console.log(functionality);
                        return response;
                    }, function errorCallback(response) {

                    });
                };

                reportService.getSimpleChartEmployee = function (personId, period) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/chartsForEmployee/' + personId + '?period=' + period
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {

                    });

                };

                reportService.getPieChartEmployee = function (personId , period) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/chartsForEmployee/' + personId + '?type=pie' + '&period=' + period
                    }).then(function successCallback(response) {
                        return response;
                    }, function errorCallback(response) {

                    });

                };


                return reportService;
            }])
})();