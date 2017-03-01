
(function () {
    angular.module("OfficeManagementSystem")
        .controller("RequestDetailsController", ["$scope", "$http",
            function ($scope, $http) {
                $scope.requestName = 'Make me tee';
                $scope.authorName = 'Ivan Pupkin';
                $scope.managerName = 'Peto Petrov';
                $scope.status = 'Syka blyad';
                $scope.dateCreation = '10-10-2017';
                $scope.priority = 'blya';
                $scope.estimate = '2';
                $scope.description = 'Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...Make Me Tea...'
        }])
})();

