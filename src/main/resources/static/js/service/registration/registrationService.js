(function () {
    angular.module("OfficeManagementSystem")
        .service("RegistrationService", ["$http", "$httpParamSerializer",
    function ($http, $httpParamSerializer) {
        var service = {};

        service.activateUser = function (token) {
            return $http.get("/api/v1/registration/" + token);
        };

        return service;
    }])
})();
