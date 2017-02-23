(function () {
    angular.module("OfficeManagementSystem").controller("welcomeHeadController", ["$scope", function($scope){

        $scope.scrollToFunc= function(){
            var top = $(".main-form").offset().top;
            $("html, body").animate({
                scrollTop: top
            },1000);
        }

    }]);
})();
