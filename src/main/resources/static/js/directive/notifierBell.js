(function(){
    angular.module("OfficeManagementSystem")
        .directive("notifier", function () {
            return{
                restrict: "E",
                templateUrl: "/static/page/directive/notifierBell.html"
            }
        });
})();