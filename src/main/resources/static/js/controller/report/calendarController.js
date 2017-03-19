(function () {
    angular.module("OfficeManagementSystem").
    controller("CalendarController", ["$scope", "$http", "CalendarService",
        function CalendarCtrl($scope, $http, CalendarService) {

            $scope.$on('$viewContentLoaded', function () {
                $('#calendar').fullCalendar({
                    events: function(start, end, timezone, callback) {
                        CalendarService.loadData(start, end, timezone, callback);
                    }
                });
            });

            $scope.nextMonth = function () {
                $('#calendar').fullCalendar('next');
            };

            $scope.prevMonth = function () {
                $('#calendar').fullCalendar('prev');
            };

            $scope.nextYear = function () {
                $('#calendar').fullCalendar('nextYear');
            };

            $scope.prevYear = function () {
                $('#calendar').fullCalendar('prevYear');
            };

        }]);
})();
