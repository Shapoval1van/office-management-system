(function () {
    angular.module("OfficeManagementSystem")
        .service("NotificationWebSocketService", ["$q", "$timeout",
            function ($q, $timeout) {
                var service = {}, listener = $q.defer(), socket = {
                    client: null,
                    stomp: null
                };

                userName=JSON.parse(localStorage.getItem("currentUser")).email;

                service.RECONNECT_TIMEOUT = 30000;
                service.SOCKET_URL = "/notification";
                service.NOTIFICATION_QUEUE = "/user/"+userName+"/queue/notification";

                service.receive = function () {
                    return listener.promise;
                };

                var reconnect = function () {
                    $timeout(function () {
                        initialize();
                    }, this.RECONNECT_TIMEOUT);
                };

                var getMessage = function (data) {
                    return JSON.parse(data);
                };

                var startListener = function () {
                    socket.stomp.subscribe(service.NOTIFICATION_QUEUE, function (data) {
                        listener.notify(getMessage(data.body));
                    });
                };

                var initialize = function () {
                    socket.client = new SockJS(service.SOCKET_URL);
                    socket.stomp = Stomp.over(socket.client);
                    socket.stomp.connect({}, startListener);
                    socket.stomp.onclose = reconnect;
                };

                service.initialize = function () {
                    initialize();
                };

                return service;
            }])
})();
