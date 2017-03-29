 (function () {
     angular.module("OfficeManagementSystem")
        .run(["$rootScope", "$location", "$injector", "SessionService",
            function($rootScope, $location, $injector, SessionService) {
                $rootScope.$on('$routeChangeStart', function (event, next) {
                    if(!SessionService.isUserLoggedIn() && next.$$route.templateUrl.includes('/secured')) {
                        window.location.href = '/login';
                    } else if(next.$$route.templateUrl.includes('/secured/manager') &&
                            (SessionService.getUserRole() !== 'ROLE_ADMINISTRATOR' &&
                            SessionService.getUserRole() !== 'ROLE_OFFICE MANAGER')) {
                        window.location.href = '/denied';
                    } else if(next.$$route.templateUrl.includes('/secured/admin') &&
                        SessionService.getUserRole() !== 'ROLE_ADMINISTRATOR') {
                        window.location.href = '/denied';
                    }
                });
                $rootScope.$on( "$locationChangeStart", function(angularEvent, next, current) {
                    if(!SessionService.isUserLoggedIn() && (next.includes('/secured') || current.includes('/secured'))) {
                        window.location.href = '/login';
                    } else if((next.includes('/secured/manager') || current.includes('/secured/admin') )&&
                        (SessionService.getUserRole() !== 'ROLE_ADMINISTRATOR' &&
                        SessionService.getUserRole() !== 'ROLE_OFFICE MANAGER')) {
                        window.location.href = '/denied';
                    } else if((next.includes('/secured/admin')  || current.includes('/secured/admin')) &&
                        SessionService.getUserRole() !== 'ROLE_ADMINISTRATOR') {
                        window.location.href = '/denied';
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
                    .when("/secured/employee/dashboard", {
                        templateUrl: "/static/page/dashboard/dashboard.html",
                        controller: "DashboardController"
                    })
                    .when("/secured/employee/comment/:requestId", {
                        templateUrl: "/static/page/test/test-comment.html",
                        controller: "CommentController"
                    })
                    .when("/secured/employee/newRequest", {
                        templateUrl: "/static/page/request/new-request-page.html",
                        controller: "NewRequestController"
                    })
                    .when("/secured/employee/request/:requestId/update", {
                        templateUrl: "/static/page/request/new-request-page.html",
                        controller: "NewRequestController"
                    })
                    .when("/secured/employee/request/:requestId/details", {
                        templateUrl: "/static/page/request/request-details.html",
                        controller: "RequestDetailsController"
                    })
                    .when("/secured/employee/request/free", {
                        templateUrl: "/static/page/request/free-request-page.html",
                        controller: "RequestListController"
                    })
                    .when("/secured/manager/request-group", {
                        templateUrl: "/static/page/request-group/request-group.html",
                        controller: "RequestGroupController"
                    })
                    .when("/secured/manager/request-group/:requestGroupId/requests", {
                        templateUrl: "/static/page/request-group/request-by-request-group.html",
                        controller: "RequestGroupDetailsController"
                    })
                    .when("/secured/admin/users", {
                        templateUrl: "/static/page/person/person-list.html",
                        controller: "PersonListController"
                    })
                    .when("/secured/admin/deleted-users", {
                        templateUrl: "/static/page/person/deleted-person-list.html",
                        controller: "DeletedPersonListController"
                    })
                    .when("/secured/employee/request/my", {
                        templateUrl: "/static/page/request/free-request-page.html",
                        controller: "RequestListController"
                    })
                    .when("/secured/employee/request/closed", {
                        templateUrl: "/static/page/request/closed-request-page.html",
                        controller: "ClosedRequestListController"
                    })
                    .when("/secured/manager/request/my/assigned", {
                        templateUrl: "/static/page/request/assigned-request-page.html",
                        controller: "AssignedRequestListController"
                    })
                    .when("/secured/manager/calendar", {
                        templateUrl: "/static/page/report/calendar.html",
                        controller: "CalendarController"
                    })
                    .when("/secured/admin/request/user", {
                        templateUrl: "/static/page/request/request-list-by-user.html",
                        controller: "RequestListByUserController"
                    })
                    .when("/secured/admin/request/assigned", {
                        templateUrl: "/static/page/request/request-list-by-user.html",
                        controller: "RequestListByUserController"
                    })
                    .when("/secured/admin/person/:personId/update", {
                        templateUrl: "/static/page/person/person-update.html",
                        controller: "UpdatePersonController"
                    })
                    .when("/secured/employee/person/:personId/details", {
                        templateUrl: "/static/page/person/person-info.html",
                        controller: "PersonDetailController"
                    })
                    .when("/secured/employee/report/:personId", {
                        templateUrl: "/static/page/report/report.html",
                        controller: "ReportController"
                    })
                    .when("/secured/test/notification/request/expiring", {
                        templateUrl: "/static/page/request/expiry-request-estimate-notification-page.html",
                        controller: "RequestListController"
                    })
                    .when("/denied", {
                        templateUrl: "/static/page/550.html"
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