/**
 * Created by Max on 21.02.2017.
 */
(function () {
    angular.module("OfficeManagementSystem")
        .directive("welcomeHeader", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/welcome-header.html"
            }
        });
})();