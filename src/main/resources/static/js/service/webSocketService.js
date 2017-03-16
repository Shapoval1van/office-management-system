(function () {
    angular.module("OfficeManagementSystem")
        .service("WebSocketService", ["$q", "$timeout",
            function ($q, $timeout) {
                var service = {}, listener = $q.defer(), socket = {
                    client: null,
                    stomp: null
                };

                service.RECONNECT_TIMEOUT = 30000;
                service.SOCKET_URL = "/chat";
                service.CHAT_TOPIC = "/topic/request/";
                service.CHAT_BROKER = "/app/chat";

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
                    socket.stomp.subscribe(service.CHAT_TOPIC, function (data) {
                        listener.notify(getMessage(data.body));
                    });
                };

                var initialize = function () {
                    socket.client = new SockJS(service.SOCKET_URL);
                    socket.stomp = Stomp.over(socket.client);
                    socket.stomp.connect({}, startListener);
                    socket.stomp.onclose = reconnect;
                };

                service.initialize = function (requestId) {
                    service.CHAT_TOPIC = service.CHAT_TOPIC + requestId;
                    initialize();
                };

                return service;
            }])
})();