(function () {
    angular.module("OfficeManagementSystem")
        .factory("FieldFactory", [
            function () {
                var fieldFactory = {};

                fieldFactory.request = {
                    NAME: "name",
                    DESCRIPTION: "description",
                    CREATE_TIME: "creation_time",
                    ESTIMATE: "estimate",
                    MANAGER: "manager_id",
                    STATUS: "status_id",
                    PRIORITY: "priority_id"
                };

                fieldFactory.person = {
                    FIRST_NAME: "first_name",
                    LAST_NAME: "last_name",
                    EMAIL: "email",
                    ROLE: "role_id"
                };

                fieldFactory.requestGroup = {
                    NAME: "name"
                };

                fieldFactory.desc = function (fieldName) {
                    return "-" + fieldName;
                };

                return fieldFactory;
            }])
})();