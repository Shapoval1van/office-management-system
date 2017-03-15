(function(){
    angular.module("OfficeManagementSystem")
        .directive("sideBar", function () {
            return{
                restrict: "E",
                scope: true,
                templateUrl: "/static/page/directive/side-bar.html",
                replace:true
            }
        });
})();
