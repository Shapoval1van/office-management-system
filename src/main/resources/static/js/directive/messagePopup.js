(function(){
    angular.module("OfficeManagementSystem")
        .directive("messagePopup", function () {
            return{
                restrict: "E",
                templateUrl: "/static/page/directive/message-popup.html"
            }
        });
})();