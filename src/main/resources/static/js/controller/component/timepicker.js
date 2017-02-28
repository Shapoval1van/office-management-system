(function () {
    angular.module("OfficeManagementSystem")
        .controller('TimepickerCtrl', function ($scope) {
            $scope.mytime = new Date();

            $scope.hstep = 1;
            $scope.mstep = 1;

            $scope.ismeridian = false;
            $scope.isspinner = false;

        });
})();