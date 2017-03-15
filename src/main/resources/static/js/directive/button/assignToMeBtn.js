(function () {
    angular.module("OfficeManagementSystem")
        .directive("assignToMeBtn", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/button/assign-to-me-btn.html"
            }
        })
})();