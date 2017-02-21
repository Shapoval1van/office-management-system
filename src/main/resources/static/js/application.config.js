(function () {
    angular.module("OfficeManagementSystem")
        .config(["$routeProvider", "$locationProvider",
            function ($routeProvider, $locationProvider) {
                $routeProvider
                // home page:
                    .when("/", {
                        templateUrl: "/static/page/login/login-page.html"
                    })
                    // login page:
                    .when("/login", {
                        templateUrl: "/static/page/login/login-page.html",
                        controller: "LoginController"
                    })
                    // registration page:
                    .when("/registration", {
                        templateUrl: "/static/page/registration/registration-page.html",
                        controller: "RegistrationController"
                    })
                    .otherwise({
                        templateUrl: "/static/error/404.html"
                    });

                $locationProvider.html5Mode(true);
            }]);
})();