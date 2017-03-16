(function () {
    angular.module("OfficeManagementSystem")
        .directive("cancelRequestBtn", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/button/cancel-request-btn.html"
            }
        });
})();