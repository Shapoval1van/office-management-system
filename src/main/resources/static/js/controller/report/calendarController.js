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
                        window.open("/secured/employee/request/" + calEvent.id + "/details", '_blank');
                    },

                    nowIndicator: true,
                    firstDay: 1, // Monday
                    timeFormat: 'HH(:mm)',

                    buttonText:{
                        today: 'Today',
                        month: 'Month',
                        listWeek: 'Week',
                        listDay: 'Day'
                    },

                    header: {
                        left: 'month,listWeek,listDay',
                        center: 'title',
                        right: 'prevYear,prev,today,next,nextYear'
                    }

                });
            });

        }]);
})();
