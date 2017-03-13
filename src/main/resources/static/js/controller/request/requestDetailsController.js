(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestDetailsController", ['$scope', '$routeParams', "WebSocketService", "RequestService", "CommentService", "PersonService",
            function ($scope, $routeParams, WebSocketService, RequestService, CommentService, PersonService) {

                var PAGE_SIZE = 10;

                var authorsList = [];
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                authorsList.push({
                    id: currentUser.id,
                    name: currentUser.firstName + ' ' + currentUser.lastName
                });

                $scope.historyPageNumber = 1;
                $scope.commentPageNumber = 1;

                $scope.request = {};
                $scope.historyList = [];

                $scope.comments = [];
                $scope.comment = "";

                $scope.periodList = {
                    type: "select",
                    value: "Day",
                    values: ["Day", "Month", "All"]
                };
                var requestId = $routeParams.requestId;

                $scope.getRequest = function () {
                    RequestService.getRequestById(requestId)
                        .then(function (callback) {
                            $scope.request = callback.data;
                        }, function (callback) {
                            console.log("Error");
                            console.log(callback);
                        });
                };

                $scope.getRequest();

                $scope.getHistoryPage = function (period, pageNumber) {
                    return RequestService.getRequestHistory(requestId, period, PAGE_SIZE, pageNumber)
                        .then(function (callback) {
                            callback.data.forEach(function (historyItem) {
                                historyItem.changeItems.forEach(function (changeItem) {
                                    changeItem.author = historyItem.author;
                                    changeItem.createDate = historyItem.createDate;
                                    $scope.historyList.push(changeItem);
                                });
                            });

                        }, function (callback) {
                            console.log("Error");
                            console.log(callback);
                        });
                };

                $scope.getHistoryPage("month", $scope.historyPageNumber);

                $scope.getNextHistoryPage = function (period) {
                    $scope.historyPageNumber++;
                    $scope.getHistoryPage(period, $scope.historyPageNumber);
                };

                $scope.getPageSize = function () {
                    return PAGE_SIZE;
                };

                //Subscribe to topic /topic/request/{requestId}
                WebSocketService.initialize(requestId);
                //Receive message from web socket
                WebSocketService.receive().then(null, null, function (comment) {
                    $scope.comments.push(comment);
                });

                $scope.sendComment = function () {
                    return CommentService.addComment($scope.comment, requestId)
                        .then(function (callback) {
                            console.log("Success");
                            $scope.comment = "";
                        }, function (callback) {
                            console.log("Failure");
                        })
                };

                $scope.getCommentsOfRequest = function (pageNumber, pageSize) {
                    return CommentService.getCommentsByRequestId(requestId, pageNumber, pageSize)
                        .then(function (callback) {
                            callback.data.forEach(function (comment) {
                                $scope.comments.push(comment);
                            })
                        }, function (callback) {
                            console.log("Failure");
                        })
                };

                $scope.getCommentsOfRequest($scope.commentPageNumber, PAGE_SIZE);

                $scope.getNextCommentPage = function () {
                    $scope.commentPageNumber++;
                    $scope.getCommentsOfRequest($scope.commentPageNumber, PAGE_SIZE);
                };

                $scope.getAuthorName = function (id) {
                    var authorName = "";
                    authorsList.forEach(function (author) {
                        if (author.id === id)
                            authorName = author.name;
                    });

                    if (authorName.length < 1)
                        PersonService.getPersonById(id)
                            .then(function (callback) {
                                var author = {
                                    id: id,
                                    name: callback.data.firstName + ' ' + callback.data.lastName
                                };
                                authorsList.push(author);
                                authorName = author.name;
                            });

                    return authorName;
                };
                //
                // $http({
                //     method: 'GET',
                //     url: '/api/request/' + $routeParams.requestId
                // }).then(function successCallback(response) {
                //     $scope.request = response.data;
                //     $scope.priorityList = {
                //         "type": "select",
                //         "value": response.data.priority.name.substr(0, 1).toUpperCase() + response.data.priority.name.substr(1).toLocaleLowerCase(),
                //         "values": ["High", "Normal", "Low"]
                //     };
                //     $scope.creationTime = new Date(response.data.creationTime).toLocaleDateString("nl", {
                //         year: "2-digit",
                //         month: "2-digit",
                //         day: "2-digit"
                //     });
                // }, function errorCallback(response) {
                //
                // });

                // $http({
                //     method: 'GET',
                //     url: '/api/request/history/' + $routeParams.requestId + '?period=day'
                // }).then(function successCallback(response) {
                //     $scope.historyList = buildHistoryList(response.data);
                // }, function errorCallback(response) {
                //
                // });
                //
                // $scope.historyForPeriod = function (item_selected) {
                //     var period = item_selected.toLowerCase();
                //     $http({
                //         method: 'GET',
                //         url: '/api/request/history/' + $routeParams.requestId + '?period=' + period
                //     }).then(function successCallback(response) {
                //         $scope.historyList = buildHistoryList(response.data);
                //     }, function errorCallback(response) {
                //
                //     });
                // };

                // $scope.prioritySelect = function (item_selected1) {
                //     var priority = item_selected1.toLowerCase();
                //     var period = $('#historySelector').find(':selected').text().toLowerCase();
                //     $http({
                //         method: 'POST',
                //         url: '/api/request/updatePriority/' + $routeParams.requestId,
                //         headers: {
                //             "Content-type": "application/x-www-form-urlencoded;"
                //         },
                //         transformRequest: function (obj) {
                //             var str = [];
                //             for (var p in obj)
                //                 str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                //             return str.join("&");
                //         },
                //         data: {
                //             priority: priority
                //         }
                //     }).then(function successCallback(response) {
                //         $http({
                //             method: 'GET',
                //             url: '/api/request/history/' + $routeParams.requestId + '?period=' + period
                //         }).then(function successCallback(response) {
                //             $scope.historyList = buildHistoryList(response.data);
                //         }, function errorCallback(response) {
                //
                //         });
                //     }, function errorCallback(response) {
                //     });
                // };
                //
                // function buildHistoryList(сhangeGroup) {
                //     var historyResult = [];
                //     сhangeGroup.forEach(function (item, arr) {
                //         item.changeItems.forEach(function (item1, arr1) {
                //             var historyItem = {};
                //             historyItem.property = item1.field.name.substr(0, 1).toUpperCase() + item1.field.name.substr(1).toLowerCase();
                //             historyItem.newValue = item1.newVal;
                //             historyItem.oldValue = item1.oldVal;
                //             historyItem.createTime = item.createDate;
                //             historyItem.author = item.author.firstName + ' ' + item.author.lastName;
                //             historyItem.authorId = item.author.id;
                //             historyResult.push(historyItem);
                //         });
                //     });
                //     return historyResult;
                // }
                //
                // $http({
                //     method: 'GET',
                //     url: '/api/request/sub/' + $routeParams.requestId
                // }).then(function successCallback(response) {
                //     $scope.subRequest = response.data;
                // }, function errorCallback(response) {
                //
                // });
                //
                // CommentService.initialize(requestId);
                //
                // $scope.sendOnEnterKey = function (event) {
                //     if (event.keyCode === 13 && $scope.comment.length > 0)
                //         $scope.sendComment();
                // };
                //
                // $scope.getCommentOfRequest = function () {
                //     return $http.get("/api/comment/request/" + requestId)
                //         .then(function (callback) {
                //             $scope.comments = callback.data;
                //         }, function (callback) {
                //             console.log(callback)
                //         })
                // };
                //
                // $scope.getCommentOfRequest();
                //
                // $scope.sendComment = function () {
                //     $http.post("/api/comment/", {
                //         body: $scope.comment,
                //         request: requestId
                //     }).then(function () {
                //         $scope.comment = "";
                //     }, function () {
                //
                //     })
                // };
                // //FIXME
                // $scope.getUserName = function (userId) {
                //
                //     if (userId == currentUser.id)
                //         return currentUser.lastName + " " + currentUser.firstName;
                //     else
                //         return "User id: " + userId;
                //
                // };
                //
                // CommentService.receive().then(null, null, function (comment) {
                //     $scope.comments.push(comment);
                // });
                //
                // $scope.requestStatusButtonsHide = function (request) {
                //     return request.manager == null;
                //     // TODO hide for employee
                // };

            }])
})();

