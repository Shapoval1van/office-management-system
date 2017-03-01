/**
 * Created by Max on 27.02.2017.
 */
(function () {
    angular.module("OfficeManagementSystem")
        .controller("CommentController", ["$scope", "CommentService", "$routeParams", "$http",
            function ($scope, CommentService, $routeParams, $http) {
                $scope.comments = [];
                $scope.comment = "";

                var requestId = $routeParams.requestId;

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
                        author: 33,
                        request: requestId
                    }).then(function () {
                        $scope.comment = "";
                    }, function () {

                    })
                };

                CommentService.receive().then(null, null, function (comment) {
                    $scope.comments.push(comment);
                });


            }])
})();