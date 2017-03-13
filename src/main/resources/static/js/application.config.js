(function () {
    angular.module("OfficeManagementSystem")
        .config(["$routeProvider", "$locationProvider",
            function ($routeProvider, $locationProvider) {
                $routeProvider
                // home page:
                    .when("/", {
                        redirectTo: "/login"
                    })
                    // login page:
                    .when("/login", {
                        templateUrl: "/static/page/login/login-page.html",
                        controller: "LoginController"
                    })
                    .when("/login/:registrationToken/", {
                        templateUrl: "/static/page/login/login-page.html",
                        controller: "LoginController"
                    })
                    // registration page:
                    .when("/registration", {
                        templateUrl: "/static/page/registration/registration-page.html",
                        controller: "RegistrationController"
                    })
                    .when("/reset", {
                        templateUrl: "/static/page/reset/recover-page.html",
                        controller: "RecoverPasswordController"
                    })
                    .when("/resetPassword/:token", {
                        templateUrl: "/static/page/reset/new-password-page.html",
                        controller: "RecoverPasswordController"
                    })
                    .when("/demo", {
                        templateUrl: "/static/page/demo/secured.html",
                        controller: "DemoController"
                    })
                    .when("/comment/:requestId", {
                        templateUrl: "/static/page/test/test-comment.html",
                        controller: "CommentController"
                    })
                    .when("/newRequest", {
                        templateUrl: "/static/page/request/new-request-page.html",
                        controller: "NewRequestController"
                    })
                    .when("/request/:requestId/update", {
                        templateUrl: "/static/page/request/update-request-page.html",
                        controller: "UpdateRequestController"
                            ///request/:requestId/
                    })
                    .when("/request/:requestId/",{
                         templateUrl: "/static/page/request/details.html",
                         controller: "RequestDetailsController"
                     })
                    .when("/request/:requestId/details", {
                        templateUrl: "/static/page/request/request-details.html",
                        controller: "RequestDetailsController"
                    })
                    .when("/requestList",{
                         templateUrl: "/static/page/request/list.html",
                         controller: "RequestListController"
                     })
                    .when("/requestGroups",{
                        templateUrl: "/static/page/request/requestGroups.html",
                        controller: "RequestGroupController"
                    })
                    .when("/requestListByEmployee", {
                        templateUrl: "/static/page/request/request-list-by-employee.html",
                        controller: "RequestListByEmployeeController"
                    })
                    .otherwise({
                        templateUrl: "/static/error/404.html"
                    });

                $locationProvider.html5Mode({
                    enabled: true,
                    requireBase: false
                }).hashPrefix("!");
            }]);
})();