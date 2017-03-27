(function () {
    angular.module("OfficeManagementSystem")
        .service("SubService", ["$http",
            function ($http) {

                var service = {};

                service.getSubRequests = function (id) {
                    return $http.get("/api/request/"+id+"/subrequests")
                        .then(function (callback) {
                            callback.isError = false;
                            angular.forEach(callback.data, function (subrequest) {
                                subrequest.showEdit = false;
                                subrequest.estimate = _transformEstimate(subrequest.estimate);
                                subrequest.shownStatus = subrequest.status;
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
                            callback.data = _transformStatuses(callback.data);
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
                            callback.data = _transformPriorities(callback.data);
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        })
                };

                service.addSubRequest = function (sub, parent) {
                    return $http.post("/api/request/"+parent+"/subrequests", sub)
                        .then(function (callback) {
                            callback.isError = false;
                            callback.data.showEdit = false;
                            callback.data.estimate = _transformEstimate(callback.data.estimate);
                            callback.data.shownStatus = callback.data.status;
                            callback.sub = callback.data;
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        })
                };

                service.updateSubRequest = function (id, sub, parent) {
                    return $http.put("/api/request/"+parent+"/subrequests/"+id, sub)
                        .then(function (callback) {
                            callback.isError = false;
                            callback.data.showEdit = false;
                            callback.data.estimate = _transformEstimate(callback.data.estimate);
                            callback.sub = callback.data;
                            callback.sub.shownStatus = callback.sub.status;
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        })
                };

                service.deleteSubRequest = function (id, parent) {
                    return $http.delete("/api/request/"+parent+"/subrequests/"+id)
                        .then(function (callback) {
                            callback.isError = false;
                            return callback;
                        }, function (callback) {
                            callback.isError = true;
                            return callback;
                        })
                };

                var _transformEstimate = function (timestamp) {
                    if (timestamp == null){
                        return '';
                    }
                    return new Date(timestamp);
                };

                var _transformStatuses = function (statuses) {
                    angular.forEach(statuses, function (obj) {
                        if (obj.name == "FREE"){
                            obj.name = "New";
                        }
                        if (obj.name == "IN PROGRESS"){
                            obj.name = "In progress";
                        }
                        if (obj.name == "CLOSED"){
                            obj.name = "Finished";
                        }
                        if (obj.name == "CANCELED"){
                            var i = statuses.indexOf(obj);
                            if(i != -1) {
                                statuses.splice(i, 1);
                            }
                        }
                    });
                    return statuses;
                };

                var _transformPriorities = function (statuses) {
                    angular.forEach(statuses, function (obj) {
                        if (obj.name == "HIGH"){
                            obj.name = "High";
                        }
                        if (obj.name == "NORMAL"){
                            obj.name = "Normal";
                        }
                        if (obj.name == "LOW"){
                            obj.name = "Low";
                        }
                    });
                    return statuses;
                };

                return service;
            }])
})();