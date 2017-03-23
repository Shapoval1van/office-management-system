(function () {
    angular.module("OfficeManagementSystem")
        .controller("SideBarController", ["$scope", "$rootScope",
            function ($scope, $rootScope) {

                $scope.sideBarItemClick = function (){
                    var JQSelector = '.side-bar-' + $rootScope.sideBarActiveElem;
                    $(JQSelector).addClass("active");
                }

                $scope.sideBarItemClick();


            }])
})();