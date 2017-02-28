/**
 * Created by Max on 27.02.2017.
 */
(function () {
    angular.module("OfficeManagementSystem")
        .controller("CommentController", ["$scope", "CommentService", "$routeParams", "$http",
            function ($scope, CommentService, $routeParams, $http) {
                $scope.messages = [];
                $scope.message = "";
                $scope.max = 140;

                var requestId = $routeParams.requestId;

                CommentService.initialize(requestId);

                $scope.sendComment = function () {
                    $http.post("/api/comment/", {
                        body: $scope.message,
                        author: 33,
                        request: requestId
                    }).then(function () {
                        $scope.message = "";
                    }, function () {

                    })
                };

                CommentService.receive().then(null, null, function (message) {
                    $scope.messages.push(message);
                });


            }])
})();