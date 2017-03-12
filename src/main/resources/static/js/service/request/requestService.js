(function () {
    angular.module("OfficeManagementSystem")
        .service("RequestService", ["$http",
            function ($http) {
                var requestService = {};

                requestService.getRequestById = function (id) {
                    return $http.get("/api/request/" + id);
                };

                // requestService.getRequests = function (pageSize, pageNumber) {
                //     return $http.get("/api/request/")
                // }
                requestService.getRequestHistory = function (requestId, period, pageSize, pageNumber) {
                    return $http.get("/api/request/history/" + requestId + "?period=" + period +
                        "&page=" + pageNumber + "&size=" + pageSize);
                };

                return requestService;
            }])
})();