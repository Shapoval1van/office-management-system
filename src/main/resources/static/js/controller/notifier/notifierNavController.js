(function () {
    angular.module("OfficeManagementSystem")
        .controller("NotifierNavController", ['$scope', '$location', '$http', 'NotificationWebSocketService',
            function ($scope, $location, $http, NotificationWebSocketService) {
                console.log("Notifer.");
                userId=JSON.parse(localStorage.getItem("currentUser")).id;

                $scope.getNotification = function() {
                    $http({
                        method: 'GET',
                        url: '/api/notification/'+userId
                    }).then(function successCallback(response) {
                        $scope.notifications = response.data;
                    }, function errorCallback(response) {
                    });
                };

                $scope.deleteAllNotification = function() {
                    $http({
                        method: 'DELETE',
                        url: '/api/allNotification/delete',
                        params: {id: userId}
                    }).then(function successCallback(response) {
                        $scope.notifications = [];
                    }, function errorCallback(response) {
                    });
                };

                $scope.deleteNotification = function(itemId) {
                    $http({
                        method: 'DELETE',
                        url: '/api/notification/delete',
                        params: {id: itemId}
                    }).then(function successCallback(response) {
                        var index = $scope.notifications.findIndex(function(o){
                            return o.id === itemId;
                        });
                        $scope.notifications.splice(index,1);
                    }, function errorCallback(response) {
                    });
                };

                $scope.toRequest = function(itemId) {
                    window.location = "/secured/employee/request/"+itemId+"/details";
                };
                if ($scope.Session.isUserLoggedIn()) {
                    $scope.getNotification();
                }
                //Subscribe to topic /topic/request/{requestId}
                NotificationWebSocketService.initialize();
                //Receive message from web socket
                NotificationWebSocketService.receive().then(null, null, function (comment) {
                    $scope.notifications.unshift(comment);
                });
            }])
})();