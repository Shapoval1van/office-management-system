(function () {
    angular.module("OfficeManagementSystem")
        .controller("NewRequestController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.requestCredentials = {};

                $scope.submitStatusArray = [true]
                $scope.submitStatus = true;

                $scope.fromValidationCheck = function () {
                    var count = 0;
                    for (var i = 0; i < $scope.submitStatusArray.length; i++){
                        if ($scope.submitStatusArray[i] == false) count++;
                        if (count == 1) $scope.submitStatus = false;
                        else $scope.submitStatus = true;
                    }
                };

                $scope.requestNameCheck = function () {
                    var nameValue = $scope.requestCredentials.name,
                        regExp = /^[A-Z][a-zA-Z\d]{3,50}$/;

                    if (nameValue){
                        var lookFor = nameValue.search(regExp);

                        if (lookFor == -1){
                            $("#request-name-input-group").addClass("request-name-incorrect");
                            $("#request-name-input-group").addClass("form-control_offset");
                            $scope.submitStatusArray[0] = false;
                            $scope.formValidationCheck();
                        }else{
                            $("#request-name-input-group").removeClass("request-name-incorrect");
                            $("#request-name-input-group").removeClass("form-control_offset");
                            $scope.submitStatusArray[0] = false;
                            $scope.formValidationCheck();
                        }

                    }
                };

               /* $scope.sendRequestCredentials(){
                    $http.post("/api/v1/new/request", $scope.requestCredentials)
                        .then(function (callback) {

                        }, function (callback) {
                            console.log("Creating request Failure!")
                        })
                };*/

            }])
})();