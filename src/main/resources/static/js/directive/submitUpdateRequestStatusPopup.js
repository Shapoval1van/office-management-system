(function(){
    angular.module("OfficeManagementSystem")
        .directive("submitUpdateRequestStatusPopup", function () {
            return{
                restrict: "E",
                templateUrl: "/static/page/directive/submit-update-request-status-popup.html",
            }

        });
})();