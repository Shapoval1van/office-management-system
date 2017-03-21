(function () {
    angular.module("OfficeManagementSystem").
    controller("CalendarController", ["$scope", "$http", "CalendarService",
        function CalendarCtrl($scope, $http, CalendarService) {

            $scope.$on('$viewContentLoaded', function () {
                $('#calendar').fullCalendar({
                    events: function(start, end, timezone, callback) {
                        CalendarService.loadData(start, end, timezone, callback);
                    },

                    eventClick: function(calEvent, jsEvent, view) {
                        window.location.href = "/request/" + calEvent.id + "/details";
                    },

                    nowIndicator: true,
                    firstDay: 1, // Monday

                    header: {
                        left: 'month,agendaWeek,agendaDay',
                        center: 'title',
                        right: 'prevYear,prev,today,next,nextYear'
                    }

                });
            });

        }]);
})();
