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

        service.loadRoles = function () {
            var promise = $http.get("/api/v1/registration/roles")
                .then(function (response) {
                    response.isError = false;
                    response.roles = [];
                    response.data.roles.forEach(function (role){
                        if (role.id == 1){
                            role.name = "Administrator";
                        }
                        if (role.id == 2){
                            role.name = "Manager";
                        }
                        response.roles.push(role);
                        return response;
                    });
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
