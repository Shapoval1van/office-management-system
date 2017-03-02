(function () {
    angular.module("OfficeManagementSystem")
        .controller("DatepickerPopupCtrl", ["$scope", "$http",
            function ($scope) {

            $('#datetimepicker1').datetimepicker();

        }]);
})();

