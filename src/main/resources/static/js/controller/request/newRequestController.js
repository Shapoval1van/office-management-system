(function () {
    angular.module("OfficeManagementSystem")
        .controller("NewRequestController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.requestCredentials = {
                    priority:'2'
                };

                $scope.requestNameCheck = function () {
                    var nameValue = $scope.requestCredentials.name,
                        regExp = /^[A-Z][a-zA-Z\d\s]{2,50}$/;

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
                    $http.post("/api/request/addRequest", $scope.requestCredentials)
                        .then(function (callback) {
                            $scope.name = callback.data.name;
                            window.location = "/requestListByEmployee";
                        }, function (callback) {
                            console.log("Creating request Failure!")
                        })
                }

            }])
})();