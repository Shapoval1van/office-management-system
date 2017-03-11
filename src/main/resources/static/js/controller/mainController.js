/**
 * Created by Max on 22.02.2017.
 */
(function () {
    angular.module("OfficeManagementSystem")
        .controller("MainController", ["$scope", "$http", "$cookies", "SessionService",
            function ($scope, $http, $cookies, SessionService) {

                $scope.Session = SessionService;

                SessionService.loadSession();
                var anonymOnlyPages = ["login", "resetPassword", "registration", "reset"];
                var redirectIfTokenExist = "/requestListByEmployee";
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
                    else
                        window.location = loginPageUrl;
                }

                $scope.logout = function () {
                    SessionService.destroySession();
                    window.location.href = '/login';
                }
            }])
})();