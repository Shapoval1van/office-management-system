(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestDetailsController", ['$scope', '$routeParams', "$http", "WebSocketService", "RequestService", "CommentService", "PersonService", "RequestGroupService",
            function ($scope, $routeParams, $http, WebSocketService, RequestService, CommentService, PersonService, RequestGroupService) {

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
                $scope.commentPageSize = 3;
                $scope.commentMaxPageSize = 0;

                $scope.request = {};
                $scope.historyList = [];

                $scope.comments = [];
                $scope.comment = "";

                $scope.periodList = ["Day", "Month", "All"];
                $scope.chosenPeriod = "Month";

                $scope.subscribers = [];

                var requestId = $routeParams.requestId;
                $scope.requestId = $routeParams.requestId;

                $scope.getRequest = function () {
                    RequestService.getRequestById(requestId)
                        .then(function (callback) {
                            $scope.request = callback.data;
                            $scope.getSubscribers();
                            $scope.getRequestGroupById();
                        }, function (callback) {
                            console.log("Error");
                            console.log(callback);
                        });
                };

                $scope.getRequest();

                $scope.getHistoryPage = function (period, pageNumber, pageSize) {
                    return RequestService.getRequestHistory(requestId, period, pageSize, pageNumber)
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

                $scope.getHistoryPage($scope.chosenPeriod, $scope.historyPageNumber, PAGE_SIZE);

                $scope.changeHistoryPeriod = function () {
                    $scope.historyPageNumber = 1;
                    $scope.historyList = [];
                    $scope.getHistoryPage($scope.chosenPeriod, $scope.historyPageNumber, PAGE_SIZE);
                    console.log($scope.chosenPeriod);
                    console.log($scope.historyPageNumber);
                };

                $scope.getNextHistoryPage = function (period) {
                    $scope.historyPageNumber++;
                    $scope.getHistoryPage(period, $scope.historyPageNumber, PAGE_SIZE);
                };

                $scope.getPageSize = function () {
                    return PAGE_SIZE;
                };

                $scope.getCommentPageSize = function () {
                    if ($scope.commentMaxPageSize - $scope.comments.length >= $scope.commentPageSize) {
                        return $scope.commentPageSize;
                    }
                    else {
                        $scope.commentPageSize = $scope.commentMaxPageSize - $scope.comments.length;
                        return $scope.commentPageSize;
                    }
                };

                //Subscribe to topic /topic/request/{requestId}
                WebSocketService.initialize(requestId);
                //Receive message from web socket
                WebSocketService.receive().then(null, null, function (comment) {
                    $scope.comments.push(comment);
                    $scope.maxPageSize++;
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
                            $scope.commentPageSize = callback.data.pageSize;
                            $scope.commentMaxPageSize = callback.data.totalElements;
                            callback.data.data.forEach(function (comment) {
                                $scope.comments.push(comment);
                            })
                        }, function (callback) {
                            console.log("Failure");
                        })
                };

                $scope.getCommentsOfRequest($scope.commentPageNumber, $scope.commentPageSize);

                $scope.getNextCommentPage = function () {
                    $scope.commentPageNumber++;
                    $scope.getCommentsOfRequest($scope.commentPageNumber, $scope.commentPageSize);
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

                $scope.requestDelete = function (requestId) {
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to cancel this request",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, cancel it!",
                            closeOnConfirm: false
                        },
                        function () {
                            RequestService.cancelRequest(requestId)
                                .then(function (callback) {
                                    $scope.requests = callback.data;
                                }, function (error) {
                                    swal("Cancel Failure!", error.data.errors[0].detail, "error");
                                    console.log(error);
                                });

                            swal("Request canceled!", "", "success");
                            window.setTimeout(function () {
                                window.location = "javascript:history.back()"
                            }, 1000)
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
                    return PersonService.assignToMe(requestId)
                        .then(function (response) {
                            $scope.assignedMessage = response.data.message;
                            $scope.getRequest();
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
                            $scope.getRequest();
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
                            // $scope.getHistoryPage($scope.chosenPeriod, 1, 1);
                        }, function () {

                        })
                };

                $scope.getRequestGroupById = function () {
                    if (!!$scope.request.requestGroup)
                        return RequestGroupService.getRequestGroupById($scope.request.requestGroup.id)
                            .then(function (callback) {
                                $scope.request.requestGroup = callback.data;
                            }, function () {
                                console.log("Failure");
                            })
                };

                $scope.removeFromRequestGroup = function () {
                    return RequestService.removeFromRequestGroup($scope.request.id)
                        .then(function () {
                            $scope.getRequest();
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

                $scope.subscribe = function () {
                    return PersonService.subscribe($scope.request.id)
                        .then(function (callback) {
                            $scope.getRequest();
                        }, function (callback) {

                        });
                };

                $scope.unsubscribe = function () {
                    return PersonService.unsubscribe($scope.request.id)
                        .then(function (callback) {
                            $scope.getRequest();
                        }, function (callback) {

                        });
                };

                $scope.getSubscribers = function () {
                    return PersonService.getSubscribers($scope.request.id)
                        .then(function (callback) {
                            $scope.subscribers = callback.data;
                        }, function (callback) {

                        });
                };

                $scope.goToRequestGroupDetails = function () {
                    $scope.goToUrl("/secured/request-group/" + $scope.request.requestGroup.id + "/requests");
                };

                $scope.isCurrentUserSubscribing = function () {
                    return PersonService.isPersonSubscribing($scope.subscribers, currentUser.id);
                };

                $scope.isCanceled = function () {
                    return RequestService.isCanceled($scope.request);
                };

                $scope.isAssigned = function () {
                    return RequestService.isAssigned($scope.request);
                };

                $scope.showAddGroupBtn = function () {
                    return ($scope.isCurrentUserManager() || $scope.isCurrentUserAdministrator()) &&
                        !$scope.request.requestGroup;
                };

                $scope.showRemoveFromGroupBtn = function () {
                    return ($scope.isCurrentUserManager() || $scope.isCurrentUserAdministrator()) &&
                        !!$scope.request.requestGroup;
                };
                //FIXME: Move to service
                $scope.isCurrentUserManager = function () {
                    return currentUser.role === "ROLE_OFFICE MANAGER";
                };
                //FIXME: Move to service
                $scope.isCurrentUserAdministrator = function () {
                    return currentUser.role === "ROLE_ADMINISTRATOR";
                };
                //FIXME: Move to service
                $scope.isAuthor = function () {
                    return !!$scope.request.employee && currentUser.id === $scope.request.employee.id;
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

                $scope.requestUpdate = function (requestId) {
                    window.location = "/secured/request/" + requestId + '/update';
                };

            }])
})();

