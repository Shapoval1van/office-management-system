(function () {
    angular.module("OfficeManagementSystem")
        .service("RequestService", ["$http",
            function ($http) {
                var requestService = {};

                requestService.getRequestById = function (id) {
                    return $http.get("/api/request/" + id)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        });
                };

                requestService.getRequestHistory = function (requestId, period, pageSize, pageNumber) {
                    return $http.get("/api/request/history/" + requestId + "?period=" + period +
                        "&page=" + pageNumber + "&size=" + pageSize)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        });
                };

                return requestService;
            }])
})();