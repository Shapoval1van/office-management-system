(function () {
    angular.module("OfficeManagementSystem")
        .service("SubService", ["$http",
            function ($http) {
                var service = {};

                var subs = [];

                service.getSubRequests = function () {

                };

                service.deleteSubRequest = function (id) {

                };

                service.updateSubRequest = function (id, sub) {

                };

                service.finishSubRequest = function (id) {

                };

                return service;
            }])
})();