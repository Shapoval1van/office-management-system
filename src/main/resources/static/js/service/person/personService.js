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

                personService.assign = function (requestId, personId) {
                  return $http.post("/api/request/assignRequest", {
                      requestId: requestId,
                      personId: personId
                  })
                      .then(function (callback) {
                          return callback;
                      }, function (callback) {
                          return callback;
                      })
                };

                return personService;
            }])
})();