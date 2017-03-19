(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestDetailsController", ['$scope', '$routeParams', "$http", "WebSocketService", "RequestService", "CommentService", "PersonService",
            function ($scope, $routeParams, $http, WebSocketService, RequestService, CommentService, PersonService) {

                var PAGE_SIZE = 10;
                $scope.selectedManager;
                $scope.assignedMessage = "";
                $scope.authorsList = [];
                var currentUser = JSON.parse(localStorage.getItem("currentUser"));
                $scope.authorsList.push({
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
                $scope.requestId = $routeParams.requestId;

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

                var isGetAuthorRequestPending = false;

                $scope.getAuthorName = function (id) {
                    var authorName = "";
                    for (var i = 0; i < $scope.authorsList.length; i++) {
                        if ($scope.authorsList[i].id === id) {
                            return $scope.authorsList[i].name;
                        }
                    }

                    if (authorName.length < 1 && !isGetAuthorRequestPending) {
                        isGetAuthorRequestPending = true;
                        PersonService.getPersonById(id)
                            .then(function (callback) {
                                isGetAuthorRequestPending = false;
                                var author = {};
                                author.id = callback.data.id;
                                author.name = callback.data.firstName + ' ' + callback.data.lastName;
                                $scope.authorsList.push(author);
                                return author.name;
                            }, function (callback) {
                                isGetAuthorRequestPending = false;
                                console.log("Failure");
                            });
                    }

                };

                $scope.requestDelete = function() {
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to cancel this request",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, cancel it!",
                            closeOnConfirm: false},
                        function(){
                            $http({
                                method: 'DELETE',
                                url: '/api/request/' + String($scope.request.id)
                            }).then(function successCallback(response) {
                                $scope.request = response.data;
                                window.location = "javascript:history.back()";
                            }, function errorCallback(error) {
                                swal("Cancel Failure!", error.data.errors[0].detail, "error");
                                console.log(error);
                            });

                            swal("Request canceled!", "", "success");
                            // window.setTimeout(function(){
                            //     location.reload()}, 1000)
                        });

                };

                $scope.update = function () {
                    //TODO: Change page number and page size
                    return PersonService.searchManagerByName($scope.selectedManager, 1, 20)
                        .then(function (callback) {
                            $scope.managers = callback.data;
                        }, function () {
                            console.log("Failure");
                        })
                };

                $scope.assignToMe = function (requestId) {
                    return PersonService.assign(requestId, currentUser.id)
                        .then(function (response) {
                            $scope.assignedMessage = response.data.message;
                        }, function (response) {
                            $scope.assignedMessage = response.data.errors
                                .map(function (e) {
                                    return e.detail
                                })
                                .join('. ');
                        });
                };

                $scope.assignToSmb = function () {
                    return PersonService.assign($scope.request.id, $scope.selectedManager.id)
                        .then(function (response) {
                            $scope.assignedMessage = response.data.message;
                        }, function (response) {
                            $scope.assignedMessage = response.data.errors
                                .map(function (e) {
                                    return e.detail
                                })
                                .join('. ');
                        });
                };

                $scope.updateRequestStatus = function (statusId) {
                    $scope.request.status = statusId;
                    $scope.request.priority = $scope.request.priority.id;
                    if (!!$scope.request.employee)
                        $scope.request.employee = $scope.request.employee.id;
                    if (!!$scope.request.manager)
                        $scope.request.manager = $scope.request.manager.id;
                    if (!!$scope.request.parent)
                        $scope.request.parent = $scope.request.parent.id;
                    if (!!$scope.request.requestGroup)
                        $scope.request.requestGroup = $scope.request.requestGroup.id;

                    return RequestService.updateRequestStatus($scope.request.id, statusId, $scope.request)
                        .then(function (callback) {
                            $scope.getRequest();
                            $scope.getHistoryPage("month", $scope.historyPageNumber);
                        }, function () {

                        })
                };

                $scope.setInProgressStatus = function () {
                    swal("Request start", "Request successful start!", "success");
                    return $scope.updateRequestStatus(2);
                };

                $scope.setClosedStatus = function () {
                    swal("Request finished", "Request successful finished!", "success");
                    return $scope.updateRequestStatus(3);
                };

                $scope.setReopen = function () {
                    swal("Request reopen", "Request successful reopen!", "success");
                    return $scope.updateRequestStatus(1);
                };

                $scope.isCanceled = function () {
                    return RequestService.isCanceled($scope.request);
                };

                $scope.isAssigned = function () {
                    return RequestService.isAssigned($scope.request);
                };
                //FIXME: Move to service
                $scope.isCurrentUserManager = function () {
                    return currentUser.role == "ROLE_OFFICE MANAGER";
                };
                //FIXME: Move to service
                $scope.isCurrentUserAdministrator = function () {
                    return currentUser.role == "ROLE_ADMINISTRATOR";
                };
                //FIXME: Move to service
                $scope.isAuthor = function () {
                    return currentUser.id === $scope.request.employee.id;
                };
                //FIXME: Move to service
                $scope.isAssignedManager = function () {
                    return $scope.request.manager && currentUser.id === $scope.request.manager.id;
                };
                //FIXME: Move to service
                $scope.isClosed = function () {
                    return $scope.request.status.name === "CLOSED";
                };
                //FIXME: Move to service
                $scope.isInProgress = function () {
                    return $scope.request.status.name === "IN PROGRESS";
                };
                //FIXME: Move to service
                $scope.isFree = function () {
                    return $scope.request.status.name === "FREE";
                };
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

                // $http({
                //     method: 'GET',
                //     url: '/api/request/sub/' + $routeParams.requestId
                // }).then(function successCallback(response) {
                //     $scope.subRequest = response.data;
                // }, function errorCallback(response) {

            }])
})();

