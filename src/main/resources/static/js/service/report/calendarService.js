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
                            element.start = new Date(element.end).toISOString();
                            element.end = new Date(element.end).toISOString();
                            if (element.status.id == 4){
                                element.color = "rgba(255, 0, 8, 0.11)";
                            }
                        });
                        callback(data.data);
                    }, function (data) {
                        return data;
                    })
                };

                return calendarService;
            }])
})();