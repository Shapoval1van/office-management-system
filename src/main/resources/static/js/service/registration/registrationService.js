(function () {
    angular.module("OfficeManagementSystem")
        .service("RegistrationService", ["$http", "$httpParamSerializer",
    function ($http, $httpParamSerializer) {
        var service = {};

        service.registerEmployee = function (employee) {
            var promise = $http.post("/api/v1/registration", employee)
                .then(function (response) {
                    response.isError = false;
                    return response;
                }, function (response) {
                    response.isError = true;
                    return response;
                });
            return promise;
        };

        service.registerAnyUser = function (user) {
            var promise =  $http.post("/api/v1/registration/adminRegistrationPerson", user)
                .then(function (response) {
                    response.isError = false;
                    return response;
                }, function (response) {
                    response.isError = true;
                    return response;
                });
            return promise;
        };

        service.activateUser = function (token) {
            return $http.get("/api/v1/registration/" + token);
        };

        return service;
    }])
})();
