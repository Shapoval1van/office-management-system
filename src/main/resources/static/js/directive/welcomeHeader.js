(function () {
    angular.module("OfficeManagementSystem")
        .directive("welcomeHeader", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/welcome-header.html"
            }
        });
})();