/**
 * Created by Max on 22.02.2017.
 */
(function () {
    angular.module("OfficeManagementSystem")
        .controller("MainController", ["$scope", "$http", "$cookies",
            function ($scope, $http, $cookies) {

                var anonymOnlyPages = ["login", "resetPassword", "registration", "reset"];
                var redirectIfTokenExist = "/demo";
                var loginPageUrl = "/login";

                var isCurrentPageAnonymOnly = function () {
                    // return anonymOnlyPages.some(function (anonymOnlyPage) {
                    //     return (~window.location.href.indexOf(anonymOnlyPage))
                    // });
                    return true;
                };

                if (isCurrentPageAnonymOnly()) {
                    if ($cookies.get("access_token"))
                        window.location.href = redirectIfTokenExist;
                } else {
                    if ($cookies.get("access_token"))
                        $http.defaults.headers.common.Authorization =
                            'Bearer ' + $cookies.get("access_token");
                    else
                        window.location = loginPageUrl;
                }

                $scope.logout = function () {
                    $cookies.remove("access_token");
                    localStorage.removeItem("currentUser");
                    window.location.reload();
                }
            }])
})();