(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestDetailsController", ['$scope', '$routeParams', '$location', '$http', "CommentService",
            function ($scope, $routeParams, $location, $http, CommentService) {

                $scope.comments = [];
                $scope.comment = "";
                var requestId = $routeParams.requestId;
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));

                var host = $location.host;
                $http({
                    method: 'GET',
                    url: '/api/request/' + $routeParams.requestId
                }).then(function successCallback(response) {
                    $scope.request = response.data;
                    console.log(response.data);
                    $scope.creationTime = new Date(response.data.creationTime).toLocaleDateString("nl", {
                        year: "2-digit",
                        month: "2-digit",
                        day: "2-digit"
                    });
                }, function errorCallback(response) {

                });
                $http({
                    method: 'GET',
                    url: '/api/request/sub/' + $routeParams.requestId
                }).then(function successCallback(response) {
                    $scope.subRequest = response.data;
                }, function errorCallback(response) {

                });

                CommentService.initialize(requestId);

                $scope.sendOnEnterKey = function (event) {
                    if (event.keyCode === 13 && $scope.comment.length > 0)
                        $scope.sendComment();
                };

                $scope.getCommentOfRequest = function () {
                    return $http.get("/api/comment/request/" + requestId)
                        .then(function (callback) {
                            $scope.comments = callback.data;
                        }, function (callback) {
                            console.log(callback)
                        })
                };

                $scope.getCommentOfRequest();

                $scope.sendComment = function () {
                    $http.post("/api/comment/", {
                        body: $scope.comment,
                        request: requestId
                    }).then(function () {
                        $scope.comment = "";
                    }, function () {

                    })
                };

                //FIXME
                $scope.getUserName = function (userId) {

                    if (userId == currentUser.id)
                        return currentUser.lastName + " " +  currentUser.firstName;
                    else
                        return "User id: " + userId;

                };

                CommentService.receive().then(null, null, function (comment) {
                    $scope.comments.push(comment);
                });


            }])
})();

