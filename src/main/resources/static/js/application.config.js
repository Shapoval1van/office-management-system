 (function () {
     angular.module("OfficeManagementSystem")
        .run(["$rootScope", "$location", "$injector", "SessionService",
            function($rootScope, $location, $injector, SessionService) {
                $rootScope.$on('$routeChangeStart', function (event, next) {
                    if(!SessionService.isUserLoggedIn() && next.$$route.templateUrl.includes('/secured')) {
                        window.location.href = '/login';
                    }
                });
                $rootScope.$on( "$locationChangeStart", function(angularEvent, next, current) {
                    if(!SessionService.isUserLoggedIn() && (next.includes('/secured') || current.includes('/secured'))) {
                        window.location.href = '/login';
                    }
                });
                $injector.get('$route');
     }])
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
                    // registration by admin
                    .when("/secured/admin/registration", {
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
                    // .when("/secured/demo", {
                    //     templateUrl: "/static/page/demo/secured.html",
                    //     controller: "DemoController"
                    // })
                    .when("/secured/dashboard", {
                        templateUrl: "/static/page/dashboard/dashboard.html",
                        controller: "DashboardController"
                    })
                    .when("/secured/comment/:requestId", {
                        templateUrl: "/static/page/test/test-comment.html",
                        controller: "CommentController"
                    })
                    .when("/secured/newRequest", {
                        templateUrl: "/static/page/request/new-request-page.html",
                        controller: "NewRequestController"
                    })
                    .when("/secured/request/:requestId/update", {
                        templateUrl: "/static/page/request/new-request-page.html",
                        controller: "NewRequestController"
                    })
                    .when("/secured/request/:requestId/details", {
                        templateUrl: "/static/page/request/request-details.html",
                        controller: "RequestDetailsController"
                    })
                    .when("/secured/request/free", {
                        templateUrl: "/static/page/request/free-request-page.html",
                        controller: "RequestListController"
                    })
                    .when("/secured/request-group", {
                        templateUrl: "/static/page/request-group/request-group.html",
                        controller: "RequestGroupController"
                    })
                    .when("/secured/request-group/:requestGroupId/requests", {
                        templateUrl: "/static/page/request-group/request-by-request-group.html",
                        controller: "RequestGroupDetailsController"
                    })
                    .when("/secured/requestGroups", {
                        templateUrl: "/static/page/request/requestGroups.html",
                        controller: "RequestGroupController"
                    })
                    .when("/secured/users", {
                        templateUrl: "/static/page/person/person-list.html",
                        controller: "PersonListController"
                    })
                    .when("/secured/deleted-users", {
                        templateUrl: "/static/page/person/deleted-person-list.html",
                        controller: "DeletedPersonListController"
                    })
                    .when("/secured/request/my", {
                        templateUrl: "/static/page/request/free-request-page.html",
                        controller: "RequestListController"
                    })
                    .when("/secured/request/my/closed", {
                        templateUrl: "/static/page/request/closed-request-page.html",
                        controller: "ClosedRequestListController"
                    })
                    .when("/secured/request/my/assigned", {
                        templateUrl: "/static/page/request/assigned-request-page.html",
                        controller: "AssignedRequestListController"
                    })
                    .when("/secured/calendar", {
                        templateUrl: "/static/page/report/calendar.html",
                        controller: "CalendarController"
                    })
                    .when("/secured/request/user", {
                        templateUrl: "/static/page/request/request-list-by-user.html",
                        controller: "RequestListByUserController"
                    })
                    .when("/secured/request/assigned", {
                        templateUrl: "/static/page/request/request-list-by-user.html",
                        controller: "RequestListByUserController"
                    })
                    .when("/secured/person/:personId/update", {
                        templateUrl: "/static/page/person/person-update.html",
                        controller: "UpdatePersonController"
                    })
                    .when("/secured/person/:personId/details", {
                        templateUrl: "/static/page/person/person-info.html",
                        controller: "PersonDetailController"
                    })
                    .when("/secured/report/:personId", {
                        templateUrl: "/static/page/report/report.html",
                        controller: "ReportController"
                    })
                    .when("/secured/test/notification/request/expiring", {
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
            }])
 })();