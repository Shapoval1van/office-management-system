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
                        templateUrl: "/static/page/request/new-request-page.html",
                        controller: "NewRequestController"
                    })
                    // .when("/request/:requestId/",{
                    //      templateUrl: "/static/page/request/details.html",
                    //      controller: "RequestDetailsController"
                    //  })
                    .when("/request/:requestId/details", {
                        templateUrl: "/static/page/request/request-details.html",
                        controller: "RequestDetailsController"
                    })
                    .when("/request/free", {
                        templateUrl: "/static/page/request/free-request-page.html",
                        controller: "RequestListController"
                    })
                    .when("/requestList", {
                        templateUrl: "/static/page/request/list.html",
                        controller: "RequestListController"
                    })
                    .when("/request-group", {
                        templateUrl: "/static/page/request-group/request-group.html",
                        controller: "RequestGroupController"
                    })
                    .when("/request-group/:requestGroupId/requests", {
                        templateUrl: "/static/page/request-group/request-by-request-group.html",
                        controller: "RequestGroupDetailsController"
                    })
                    .when("/requestGroups", {
                        templateUrl: "/static/page/request/requestGroups.html",
                        controller: "RequestGroupController"
                    })
                    // .when("/requestListByEmployee", {
                    //     templateUrl: "/static/page/request/request-list-by-employee.html",
                    //     controller: "RequestListByEmployeeController"
                    // })
                    .when("/users", {
                        templateUrl: "/static/page/person/person-list.html",
                        controller: "PersonListController"
                    })
                    .when("/request/my", {
                        templateUrl: "/static/page/request/request-list-by-employee.html",
                        controller: "RequestListByEmployeeController"
                    })
                    .when("/calendar", {
                        templateUrl: "/static/page/report/calendar.html",
                        controller: "CalendarController"
                    })
                    .when("/request/user", {
                        templateUrl: "/static/page/request/request-list-by-user.html",
                        controller: "RequestListByUserController"
                    })
                    .when("/request/assigned", {
                        templateUrl: "/static/page/request/request-list-by-user.html",
                        controller: "RequestListByUserController"
                    })
                    .when("/person/:personId/update", {
                        templateUrl: "/static/page/person/person-update.html",
                        controller: "UpdatePersonController"
                    })
                    .when("/report/:personId", {
                        templateUrl: "/static/page/report/report.html",
                        controller: "ReportController"
                    })
                    .when("/test/notification/request/expiring", {
                        templateUrl: "/static/page/request/expiry-request-estimate-notification-page.html",
                        controller: "RequestListController"
                    })
                    .otherwise({
                        templateUrl: "/static/page/404.html"
                    });

                $locationProvider.html5Mode({
                    enabled: true,
                    requireBase: false
                }).hashPrefix("!");
            }]);
})();