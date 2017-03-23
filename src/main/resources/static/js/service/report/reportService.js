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


                reportService.pageChanged = function(personId, currentPage, period, pageSize) {
                    return $http({
                        method: 'GET',
                        url: 'api/report/allRequest/'+personId+'?period='+ period
                        +'&page=' +  currentPage + '&size=' + pageSize
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