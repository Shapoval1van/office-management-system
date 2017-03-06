(function(){
    angular.module("OfficeManagementSystem")
        .directive("officeManagerSidebar", function () {
            return{
                restrict: "E",
                templateUrl: "/static/page/directive/office-manager-sidebar.html"
            }
        });
})();