(function () {
    angular.module("OfficeManagementSystem")
        .controller("UpdateRequestController", ["$scope", "$http", "$routeParams",
            function ($scope, $http, $routeParams) {
                $scope.requestCredentials ={};

                var reguestId = $routeParams.requestId;

                $scope.getRequestCredential = function () {
                    $http.get("/api/request/" + reguestId )
                        .then(function (callback) {
                            $scope.requestCredentials = callback.data;
                        }, function (callback) {
                            console.log(callback)
                        })
                }


                $scope.requestNameCheck = function () {
                    var nameValue = $scope.requestCredentials.name,
                        regExp = /^[A-Z][a-zA-Z\d]{2,50}$/;

                    if (nameValue){
                        var lookFor = nameValue.search(regExp);

                        if (lookFor == -1){
                            $("#request-name-input-group").addClass("request-name-incorrect");
                            $("#request-description-field").addClass("form-control_offset");
                        }else{
                            $("#request-name-input-group").removeClass("request-name-incorrect");
                            $("#request-description-field").removeClass("form-control_offset");
                        }

                    }
                };

                $scope.sendRequestCredentials = function () {
                    $scope.requestCredentials.estimate = new Date($('#datetimepicker1').data('date')).getTime();
                    $http.post("/api/request/" + reguestId + "/update?/", $scope.requestCredentials)
                        .then(function (callback) {
                            $scope.name = callback.data.name;
                        }, function (callback) {
                            console.log("Updating request Failure!")
                        })
                }

                $scope.statusDropdown = function(){
                    var statusVisibility = $(".status-text-wrapper-outer").hasClass('active');
                    if(statusVisibility){
                        $(".status-text-wrapper-outer").removeClass("active");
                        $(".status-text-wrapper-outer").fadeOut();
                    }
                    else{
                        $(".status-text-wrapper-outer").addClass("active");
                        $(".status-text-wrapper-outer").fadeIn();
                    }
                }

            }])
})();
