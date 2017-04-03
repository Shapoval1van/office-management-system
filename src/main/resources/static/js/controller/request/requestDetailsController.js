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
                $scope.historyPageSize = 10;
                $scope.historyMaxPageSize = 0;

                $scope.commentPageNumber = 1;
                $scope.commentPageSize = 10;
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
                            swal("Get Request Error", callback.data, "error");
                        });
                };

                $scope.getRequest();

                $scope.getHistoryPage = function (period, pageNumber, pageSize) {
                    return RequestService.getRequestHistory(requestId, period, pageSize, pageNumber)
                        .then(function (callback) {
                            $scope.historyPageSize = callback.data.pageSize;
                            $scope.historyMaxPageSize = callback.data.totalElements;

                            callback.data.data.forEach(function (historyItem) {
                                historyItem.changeItems.forEach(function (changeItem) {
                                    changeItem.author = historyItem.author;
                                    changeItem.createDate = historyItem.createDate;
                                    $scope.historyList.push(changeItem);
                                });
                            });

                        }, function (callback) {
                            swal("Get Request Error", callback.data, "error");
                        });
                };

                $scope.getHistoryPage($scope.chosenPeriod, $scope.historyPageNumber, $scope.historyPageSize);

                $scope.getLastHistoryItem = function () {
                    $scope.getHistoryPage($scope.chosenPeriod, 1, 1);
                }

                $scope.changeHistoryPeriod = function () {
                    $scope.historyPageNumber = 1;
                    $scope.historyList = [];
                    $scope.getHistoryPage($scope.chosenPeriod, $scope.historyPageNumber, $scope.historyPageSize);
                };

                $scope.getNextHistoryPage = function (period) {
                    $scope.historyPageNumber++;
                    $scope.getHistoryPage(period, $scope.historyPageNumber, PAGE_SIZE);
                };

                $scope.getCommentPageSize = function () {
                    if ($scope.commentMaxPageSize - $scope.comments.length >= $scope.commentPageSize) {
                        return $scope.commentPageSize;
                    }
                    else {
                        return $scope.commentMaxPageSize - $scope.comments.length;
                    }
                };

                $scope.getHistoryPageSize = function () {
                    if ($scope.historyMaxPageSize - $scope.historyList.length >= $scope.historyPageSize) {
                        return $scope.historyPageSize;
                    }
                    else {
                        return $scope.historyMaxPageSize - $scope.historyList.length;
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
                            $scope.comment = "";
                        }, function (callback) {
                            swal("Send Comment Error", callback.data, "error");
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
                                    swal("Request canceled!", "", "success");
                                    window.location = "javascript:history.back()"
                                }, function (error) {
                                    swal("Cancel Failure!", error.data.errors[0].detail, "error");
                                    console.log(error);
                                });
                        });
                };

                $scope.unassign = function (requestId) {
                    var text = "Do you really want to unassign manager from this request?\n";
                    if($scope.isGrouped()) {
                        text += "Request will be deleted from group " + $scope.request.requestGroup.name;
                    }
                    swal({
                            title: "Are you sure?",
                            text: text,
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, unassign!",
                            closeOnConfirm: false
                        },
                        function () {
                            RequestService.unassign(requestId)
                                .then(function (callback) {
                                    $scope.requests = callback.data;
                                    swal("Request unassigned!", "Request successful unassigned", "success");
                                    $scope.getRequest();
                                    $scope.getLastHistoryItem();
                                }, function (error) {
                                    swal("Unassigning Failure!", error.data.errors, "error");
                                });
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
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to assign this request",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, assign!",
                            closeOnConfirm: false
                        },
                        function () {
                            PersonService.assignToMe(requestId)
                                .then(function (response) {
                                    $scope.requests = response.data;
                                    swal("Request assigned!", "Request successful assigned", "success");
                                    $scope.getRequest();
                                    $scope.getLastHistoryItem();
                                }, function (response) {
                                    swal("Assigning Failure!", response.data.errors, "error");
                                });
                        });
                };

                $scope.assignToSmb = function () {
                    return PersonService.assign($scope.request.id, $scope.selectedManager.id)
                        .then(function (response) {
                            swal("Request assigned!", "Request successful assigned", "success");
                            $scope.getRequest();
                            $scope.getLastHistoryItem();
                        }, function (response) {
                            swal("Assigning Failure!", response.data.errors, "error");
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
                    swal({
                            title: "Are you sure?",
                            text: "Do you really want to remove request from this group",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Yes, remove it!",
                            closeOnConfirm: false
                        },
                        function () {
                            RequestService.removeFromRequestGroup($scope.request.id)
                                .then(function (callback) {
                                    $scope.getRequest();
                                    swal("Request was removed from request group!", "", "success");
                                }, function () {
                                    swal("Remove Request From Group Failure!", "", "error");
                                });
                        });
                };

                $scope.setInProgressStatus = function () {
                    return $scope.updateRequestStatus(2)
                        .then(function (callback) {
                            swal("Request start", "Request successful start!", "success");
                            $scope.getLastHistoryItem();
                        }, function (callback) {
                            swal("Request start", "Can't start request! " + callback.data.errors, "error");
                        });
                };

                $scope.setClosedStatus = function () {
                    return $scope.updateRequestStatus(3)
                        .then(function (callback) {
                            swal("Request finished", "Request successful finished!", "success");
                            $scope.getLastHistoryItem();
                        }, function (callback) {
                            swal("Request finish", "Can't finish request! " + callback.data.errors, "error");
                        });
                };

                $scope.setReopen = function () {
                    return $scope.updateRequestStatus(1)
                        .then(function (callback) {
                            swal("Request reopen", "Request successful reopen!", "success");
                            $scope.getLastHistoryItem();
                        }, function (callback) {
                            swal("Request reopen", "Can't reopen request! " + callback.data.errors, "error");
                        });
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
                    $scope.goToUrl("/secured/manager/request-group/" + $scope.request.requestGroup.id + "/requests");
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

                $scope.showCancelButton = function () {
                    return ($scope.isAuthor() || $scope.isCurrentUserAdministrator()) && $scope.isFree();
                };

                $scope.showAddGroupBtn = function () {
                    return ($scope.isAssignedManager() || $scope.isCurrentUserAdministrator()) && !$scope.isGrouped()
                        && !$scope.isClosed() && !$scope.isCanceled() && $scope.isAssigned();
                };

                $scope.showRemoveFromGroupBtn = function () {
                    return (!$scope.isAssignedManager() || !$scope.isCurrentUserAdministrator()) && $scope.isGrouped();
                };

                $scope.showAssignBtn = function () {
                    return !$scope.isAssigned() && $scope.isCurrentUserManager();
                };

                $scope.showAssignToSmbBtn = function () {
                    return !$scope.isAssigned() && $scope.isCurrentUserAdministrator();
                };

                $scope.showStartRequestBtn = function () {
                    return $scope.isAssignedManager() && $scope.isFree() && !$scope.isGrouped();
                };

                $scope.showFinishRequestBtn = function () {
                    return $scope.isAssignedManager() && $scope.isInProgress() && !$scope.isGrouped();
                };

                $scope.showReopenBtn = function () {
                    return $scope.isAuthor() && $scope.isClosed() && !$scope.isGrouped();
                };

                $scope.showUpdateBtn = function () {
                    return $scope.isFree() && $scope.isAuthor() || $scope.isCurrentUserAdministrator()
                };

                $scope.showUnassignBtn = function () {
                    return $scope.isCurrentUserAdministrator() && $scope.isAssigned() && !$scope.isClosed()
                        && !$scope.isCanceled();
                };
                //FIXME: Move to service
                $scope.isCurrentUserManager = function () {
                    return currentUser.role === "ROLE_OFFICE MANAGER";
                };
                //FIXME: Move to service
                $scope.isCurrentUserEmployee = function () {
                    return currentUser.role === "ROLE_EMPLOYEE";
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
                    return !!$scope.request.manager && currentUser.id === $scope.request.manager.id;
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

                $scope.isGrouped = function () {
                    return !!$scope.request.requestGroup;
                };

                $scope.requestUpdate = function (requestId) {
                    $scope.goToUrl("/secured/employee/request/" + requestId + '/update');
                };

            }])
})();

