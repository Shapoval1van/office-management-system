(function () {
    angular.module("OfficeManagementSystem")
        .service("RequestGroupService", ["$http",
            function ($http) {
                var requestGroupService = {};

                requestGroupService.findGroupByNamePattern = function (authorId, namePattern) {
                    if (namePattern.length > 2)
                        return $http.get("/api/request-group/author/" + authorId + "/search/" + namePattern)
                            .then(function (callback) {
                                return callback;
                            }, function (callback) {
                                return callback;
                            })
                };

                requestGroupService.getGroupByAuthor = function (authorId, pageNumber, pageSize, order) {
                    var requestUrl = "/api/request-group/author/" + authorId + "?page=" + pageNumber +
                        "&size=" + pageSize;
                    if (!!order)
                        requestUrl += "&sort=" + order;

                    return $http.get(requestUrl)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestGroupService.createGroup = function (groupName) {
                    return $http.post("/api/request-group/", {name: groupName})
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestGroupService.getGroupCountByAuthor = function (authorId) {
                    return $http.get("/api/request-group/count/author/" + authorId)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestGroupService.setGroupStatus = function (requestGroupId, statusId) {
                    return $http.put("/api/request-group/" + requestGroupId + "/status", {id: statusId})
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestGroupService.deleteRequestGroup = function (requestGroupId) {
                    return $http.delete("/api/request-group/" + requestGroupId)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                requestGroupService.getRequestGroupById = function (requestGroupId) {
                    return $http.get("/api/request-group/" + requestGroupId);
                };

                return requestGroupService;
            }])
})();