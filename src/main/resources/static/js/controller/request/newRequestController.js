(function () {
    angular.module("OfficeManagementSystem")
        .controller("NewRequestController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.wrongRequestNameMessage = "Name must contain at least 3 letters";
                $scope.requestNameRegExp = /^(([a-zA-Z\d]+)\s?\1?){3,}$/;
                $scope.requestCredentials = {
                    priority:'2'
                };
                
                $scope.sendRequestCredentials = function () {
                    //$scope.requestCredentials.estimate = new Date($('#datetimepicker1').data('date')).getTime();
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