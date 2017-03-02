(function () {
    angular.module("OfficeManagementSystem")
        .controller("DatepickerPopupCtrl", ["$scope", "$http",
            function ($scope) {

            var nowDate = new Date();
            var today = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), 0, 0, 0, 0)

            $('#datetimepicker1').datetimepicker({
                minDate: new Date()
            });

        }]);
})();

