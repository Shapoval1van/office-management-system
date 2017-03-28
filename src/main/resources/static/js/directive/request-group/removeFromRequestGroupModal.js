(function () {
    angular.module("OfficeManagementSystem")
        .directive("removeFromRequestGroupModal", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/request-group/remove-from-request-group-modal.html"
            }
        })
})();