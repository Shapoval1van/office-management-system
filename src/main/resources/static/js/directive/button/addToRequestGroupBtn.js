(function () {
    angular.module("OfficeManagementSystem")
        .directive("addToRequestGroupBtn", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/button/add-to-request-group-btn.html"
            }
        })
})();