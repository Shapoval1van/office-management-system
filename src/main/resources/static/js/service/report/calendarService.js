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
                            var tzoffset = (new Date()).getTimezoneOffset() * 60000;

                            element.start = (new Date(element.end - tzoffset)).toISOString();
                            element.end = (new Date(element.end - tzoffset)).toISOString();
                            switch (element.priority.id) {
                                case 1 :
                                    element.color = "rgba(215, 40, 40, 0.9)";
                                    break;
                                case 2 :
                                    element.color = "rgba(255, 158, 0, 0.9)";
                                    break;
                                case 3 :
                                    element.color = "rgba(0, 182, 44, 0.9)";
                                    break;
                            }
                            if (element.status.id === 3 || element.status.id === 4){
                                element.color = "rgba(0, 0, 0, 0.3)";
                            }
                        });
                        data.data.sort(function(a, b) {
                            return a.priority.id - b.priority.id;
                        });
                        callback(data.data);
                    }, function (data) {
                        return data;
                    })
                };

                return calendarService;
            }])
})();