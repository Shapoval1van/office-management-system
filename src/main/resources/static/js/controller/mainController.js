(function () {
    angular.module("OfficeManagementSystem")
        .controller("MainController", ["$scope", "$location", "$http", "$cookies", "SessionService",
            function ($scope, $location, $http, $cookies, SessionService) {

                $scope.Session = SessionService;

                SessionService.loadSession();
                var anonymOnlyPages = ["resetPassword", "reset"];
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

                }

                $scope.logout = function () {
                    SessionService.destroySession();
                    window.location.href = '/login';
                };

                $scope.goToUrl = function (url) {
                    $location.path(url);
                }
            }])
})();