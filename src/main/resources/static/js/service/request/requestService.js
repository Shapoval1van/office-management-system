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
                    return $http.get("/api/request/history/" + requestId + "?period=" + period.toLowerCase() +
                        "&page=" + pageNumber + "&size=" + pageSize)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        });
                };

                requestService.cancelRequest = function (requestId) {
                    return $http.delete("/api/request/" + requestId)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.getAvailableRequest = function (priority, pageNumber, pageSize) {
                    return $http.get("/api/request/available/" + priority + "?page=" + pageNumber + "&size=" + pageSize)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.getPageCountByPriority = function (priority) {
                    return $http.get("/api/request/count/" + priority)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.getRequestsByRequestGroup = function (requestGroupId, pageNumber, pageSize) {
                    return $http.get("/api/request/request-group/" + requestGroupId + "?page=" + pageNumber + "&size=" + pageSize)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.updateRequest = function (requestId, request) {
                    return $http.put("/api/request/" + requestId, request)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.updateRequestStatus = function (requestId, statusId, request) {
                    return $http.put("/api/request/" + requestId + "/update/" + statusId, request)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };


                requestService.isCanceled = function (request) {
                    return !!request.status && request.status.name == "CANCELED";
                };

                requestService.isAssigned = function (request) {
                    return request.manager != null;
                };

                requestService.notifyAboutExpiringEstimateTime = function () {
                    return $http.get("/api/test/notification/request/expiring")
                        .then(function (callback) {
                            callback.isError = false;
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        });
                };

                return requestService;
            }])
})();