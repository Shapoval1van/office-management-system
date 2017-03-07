(function(){
    angular.module("OfficeManagementSystem")
        .directive("exitPopup", function () {
            return{
                restrict: "E",
                templateUrl: "/static/page/directive/exit-popup.html"
            }

        });
})();