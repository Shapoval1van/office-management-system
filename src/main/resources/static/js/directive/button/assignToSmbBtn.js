(function () {
    angular.module("OfficeManagementSystem")
        .directive("assignToSmbBtn", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/button/assign-to-smb-btn.html"
            }
        })
})();