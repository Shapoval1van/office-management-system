(function () {
    angular.module("OfficeManagementSystem")
        .service("CalendarService", ["$http",
            function ($http) {
                var calendarService = {};

                calendarService.loadData = function (start, end, timezone, callback) {
                    $http.get("/api/report/calendar", {
                        params: {
                            start: start.unix(),
                            end: end.unix()
                        }
                    })
                    .then(function (data) {
                        data.data.forEach(function(element) {
                            element.start = new Date(element.start).toISOString();
                            element.end = new Date(element.end).toISOString();
                        });
                        callback(data.data);
                    }, function (data) {
                        return data;
                    })
                };

                return calendarService;
            }])
})();