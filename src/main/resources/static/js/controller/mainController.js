(function () {
    angular.module("OfficeManagementSystem")
        .controller("MainController", ["$scope", "$location", "$http", "$cookies", "SessionService",
            function ($scope, $location, $http, $cookies, SessionService) {

                $scope.Session = SessionService;
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));


                    $scope.Session.loadOldSession().then(function (callback) {
                        if (callback.isOk == true){
                            console.log("Session loaded.");
                        } else {
                            console.log("Session not loaded.");
                        }
                    });

                var anonymOnlyPages = ["resetPassword", "reset"];
                var redirectIfTokenExist = "/secured/employee/requestListByEmployee";
                var loginPageUrl = "/login";


                $scope.logout = function () {
                    SessionService.destroySession();
                    window.location.href = '/login';
                };

                $scope.goToUrl = function (url) {
                    $location.path(url);
                };

                $scope.getReportUrl = function () {
                    return "/secured/employee/report/" + currentUser.id;
                };

                $scope.hasEmployeePermission = function (url) {
                    return SessionService.isUserLoggedIn();
                };

                $scope.hasManagerPermission = function (url) {
                    return SessionService.getUserRole() == 'ROLE_ADMINISTRATOR'
                        || SessionService.getUserRole() == 'ROLE_OFFICE MANAGER';
                };

                $scope.hasAdminPermission = function (url) {
                    return SessionService.getUserRole() == 'ROLE_ADMINISTRATOR';
                };

                $scope.isActive = function (path) {
                    return $location.path() === path;
                }
            }])
})();