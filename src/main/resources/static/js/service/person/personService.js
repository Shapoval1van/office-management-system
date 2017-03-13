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

                return personService;
            }])
})();