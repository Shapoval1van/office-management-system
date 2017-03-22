(function () {
    angular.module("OfficeManagementSystem")
        .controller("NotifierBellController", ['$scope', '$location', '$http', 'NotificationWebSocketService',
            function ($scope, $location, $http, NotificationWebSocketService) {
                userId=JSON.parse(localStorage.getItem("currentUser")).id;
                $scope.getNotification = function(currentPage) {
                    $http({
                        method: 'GET',
                        url: '/api/notification/'+userId
                    }).then(function successCallback(response) {
                        $scope.notifications = response.data;
                    }, function errorCallback(response) {
                    });
                };

                $scope.getNotification();

                //Subscribe to topic /topic/request/{requestId}
                NotificationWebSocketService.initialize();
                //Receive message from web socket
                NotificationWebSocketService.receive().then(null, null, function (comment) {
                    console.log(comment)
                });
            }])
})();