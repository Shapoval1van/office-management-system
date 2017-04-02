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

                fieldFactory.isDescOrder = function (requestString, fieldName) {
                    var nameIndex = requestString.indexOf(fieldName);
                    return ~nameIndex && requestString.charAt(nameIndex - 1) === "-";
                };

                fieldFactory.isAscOrder = function (requestString, fieldName) {
                    var nameIndex = requestString.indexOf(fieldName);
                    return ~nameIndex && requestString.charAt(nameIndex - 1) !== "-";
                };

                fieldFactory.removeSortField = function (requestString, fieldName) {
                    return  (fieldFactory.isDescOrder(requestString, fieldName)) ?
                        requestString.replace("-" + fieldName, "").replace(",,", ",") :
                        requestString.replace(fieldName, "").replace(",,", ",");
                };

                fieldFactory.toggleOrder = function (requestString, fieldName) {
                    if (fieldFactory.isDescOrder(requestString, fieldName))
                        return requestString.replace("-" + fieldName, fieldName);
                    else if (fieldFactory.isAscOrder(requestString, fieldName))
                        return requestString.replace(fieldName, "-" + fieldName);
                    else {
                        return (requestString.length > 0) ? (requestString + "," + fieldName).replace(",,", ",") :
                            requestString + fieldName;
                    }
                };

                return fieldFactory;
            }])
})();