(function () {
    angular.module("OfficeManagementSystem")
        .service("CommentService", ["$http",
            function ($http) {
                var commentService = {};

                commentService.addComment = function (comment) {
                    $http.post("/api/comment", comment)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                return commentService;
            }])
})();