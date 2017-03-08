(function(){
    angular.module("OfficeManagementSystem")
        .directive("groupLinkPopup", function () {
            return{
                restrict: "E",
                templateUrl: "/static/page/directive/group-link-popup.html"
            }
        });
})();