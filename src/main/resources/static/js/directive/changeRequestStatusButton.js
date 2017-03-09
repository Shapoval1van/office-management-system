(function(){
    angular.module("OfficeManagementSystem")
        .directive("changeRequestStatusButton", function () {
            return{
                restrict: "E",
                templateUrl: "/static/page/directive/change-request-status-button.html"
            }
        });
})();