(function () {
    angular.module("OfficeManagementSystem")
        .service("PersonService", ["$http",
            function ($http) {
                var personService = {};

                personService.getPersonById = function (id) {
                    return $http.get("/api/person/" + id)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                personService.searchManagerByName = function (namePattern, pageNumber, pageSize) {
                    if (namePattern.length > 2)
                        return $http.get("/api/person/managers/" + namePattern + "?page=" + pageNumber + "&size=" + pageSize)
                            .then(function (callback) {
                                return callback;
                            }, function (callback) {
                                return callback;
                            })
                };

                personService.assignToMe = function (requestId) {
                    return $http.post("/api/request/assign/request/" + requestId, {
                        requestId: requestId
                    })
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                personService.assign = function (requestId, personId) {
                    return $http.post("/api/request/assign/request", {
                        requestId: requestId,
                        personId: personId
                    })
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                personService.subscribe = function (requestId) {
                    return $http.put("/api/person/subscribe", {requestId: requestId})
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                personService.unsubscribe = function (requestId) {
                    return $http.put("/api/person/unsubscribe", {requestId: requestId})
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                personService.getSubscribers = function (requestId) {
                    return $http.get("/api/person/subscribers/request/" + requestId);
                };

                personService.isPersonSubscribing = function (personList, personId) {
                    return personList.some(function (person) {
                        return person.id === personId;
                    })
                };

                return personService;
            }])
})();