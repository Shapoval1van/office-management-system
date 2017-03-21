(function () {
    angular.module("OfficeManagementSystem")
        .service("SubService", ["$http",
            function ($http) {
                var service = {};

                service.getSubRequests = function (id) {
                    return $http.get("/api/request/"+id+"/subrequests")
                        .then(function (callback) {
                            callback.isError = false;
                            angular.forEach(callback.data, function (obj) {
                                obj["showEdit"] = false;
                                obj["estimate"] = transformEstimate(obj["estimate"]);
                            });
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        })
                };

                service.getStatuses = function () {
                    return $http.get("/api/statuses")
                        .then(function (callback) {
                            callback.isError = false;
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        })
                };

                service.getPriorities = function () {
                    return $http.get("/api/priorities")
                        .then(function (callback) {
                            callback.isError = false;
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        })
                };

                service.deleteSubRequest = function (id) {

                };

                service.updateSubRequest = function (id, sub) {

                };

                service.finishSubRequest = function (id) {

                };

                var transformEstimate = function (timestamp) {
                    return new Date(timestamp);
                };

                return service;
            }])
})();