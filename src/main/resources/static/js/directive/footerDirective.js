(function(){
    angular.module("OfficeManagementSystem")
        .directive("footerDirective", function () {
            return{
                restrict: "E",
                scope: true,
                templateUrl: "/static/page/directive/footer.html",
                replace:true
            }
        });
})();
