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
                        }, function (error) {
                            swal("Cancel Failure!", error.data.errors[0].detail, "error");
                            return callback;
                        })
                };

                requestService.getAvailableRequest = function (pageNumber, pageSize, order) {
                    var requestUrl = "/api/request/available?page=" + pageNumber + "&size=" + pageSize;
                    if (!!order)
                        requestUrl += "&sort=" + order;

                    return $http.get(requestUrl)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };


                requestService.getAssignedRequestList = function (pageNumber, pageSize, order) {
                    var requestUrl = "/api/request/list/assigned?page=" + pageNumber + "&size=" + pageSize;
                    if (!!order)
                        requestUrl += "&sort=" + order;

                    return $http.get(requestUrl)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };
                requestService.getAvailableRequestByPriority = function (priority, pageNumber, pageSize, order) {
                    var requestUrl = "/api/request/available/" + priority + "?page=" + pageNumber + "&size=" + pageSize;
                    if (!!order)
                        requestUrl += "&sort=" + order;

                    return $http.get(requestUrl)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.getAllRequestByEmployee = function (pageNumber, pageSize, order) {
                    var requestUrl = "/api/request/list/my?page=" + pageNumber + "&size=" + pageSize;
                    if (!!order)
                        requestUrl += "&sort=" + order;

                    return $http.get(requestUrl)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.getAllClosedRequestByEmployee = function (pageNumber, pageSize, sort) {
                    var url = "/api/request/list/my/closed?page=" + pageNumber + "&size=" + pageSize;
                    if(!!sort){
                        url= url + "&sort="+sort;
                    }
                    return $http.get(url)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.getRequestsByRequestGroup = function (requestGroupId, pageNumber, pageSize, order) {
                   var requestUrl = "/api/request/request-group/" + requestGroupId + "?page=" + pageNumber
                       + "&size=" + pageSize;

                   if (!!order)
                       requestUrl += "&sort=" + order;

                    return $http.get(requestUrl)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestService.unassign = function (requestId) {
                    return $http.put("/api/request/" + requestId + "/unassig/")
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
                    return $http.put("/api/request/" + requestId + "/status/" + statusId, request)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };


                requestService.isCanceled = function (request) {
                    return !!request.status && request.status.name === "CANCELED";
                };

                requestService.isAssigned = function (request) {
                    return request.manager !== null;
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

                requestService.removeFromRequestGroup = function (requestId) {
                    return $http.delete("/api/request/" + requestId + "/group");
                };

                return requestService;
            }])
})();