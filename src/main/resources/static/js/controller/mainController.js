(function () {
    angular.module("OfficeManagementSystem")
        .controller("MainController", ["$scope", "$location", "$http", "$cookies", "SessionService",
            function ($scope, $location, $http, $cookies, SessionService) {

                $scope.Session = SessionService;
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));


                    $scope.Session.loadOldSession().then(function (callback) {
                        if (callback.isOk == true){
                            console.log("Session loaded.")
                        } else {
                            $location.path("/login");
                        }
                    });



                var anonymOnlyPages = ["resetPassword", "reset"];
                var redirectIfTokenExist = "/secured/requestListByEmployee";
                var loginPageUrl = "/login";

                //FIXME: Rewrite it. Check only URL.
                var isCurrentPageAnonymOnly = function () {
                    return anonymOnlyPages.some(function (anonymOnlyPage) {
                        //FIXME: Change it !!!
                        return (~window.location.href.indexOf(anonymOnlyPage) && !(~window.location.href.indexOf("/registrationAdmin")));
                    });
                };

                if (isCurrentPageAnonymOnly()) {
                    if (SessionService.isUserLoggedIn())
                        window.location.href = redirectIfTokenExist;
                } else {
                    if (SessionService.isUserLoggedIn())
                        $http.defaults.headers.common.Authorization =
                            'Bearer ' + SessionService.getAccessToken();

                }

                $scope.logout = function () {
                    SessionService.destroySession();
                    window.location.href = '/login';
                };

                $scope.goToUrl = function (url) {
                    $location.path(url);
                };

                $scope.getReportUrl = function () {
                    return "/secured/report/" + currentUser.id;
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