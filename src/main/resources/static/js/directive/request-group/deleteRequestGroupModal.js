(function () {
    angular.module("OfficeManagementSystem")
        .directive("deleteRequestGroupModal", function () {
            return {
                restrict: "E",
                templateUrl: "/static/page/directive/request-group/delete-request-group-modal.html"
            }
        })
})();