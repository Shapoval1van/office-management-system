(function () {
    angular.module("OfficeManagementSystem")
        .service("CommentService", ["$http",
            function ($http) {
                var commentService = {};

                commentService.addComment = function (body, requestId) {
                    var comment = {};
                    comment.body = body;
                    comment.request = requestId;

                    return $http.post("/api/comment/", comment)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                commentService.getCommentsByRequestId = function (requestId, pageNumber, pageSize) {
                    return $http.get("/api/comment/request/" + requestId + "?page=" + pageNumber + "&size=" + pageSize)
                        .then(function (callback) {
                            return callback;
                        }, function (callback) {
                            return callback;
                        })
                };

                return commentService;
            }])
})();