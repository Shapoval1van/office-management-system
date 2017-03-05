(function () {
    angular.module("OfficeManagementSystem")
        .service("SessionService", ["$http",
            function ($http) {
                var service = {};

                var currentUser = null;
                var currentSession = null;

                service.createSession = function (sessionInfo) {
                    currentUser = {
                        firstName: sessionInfo.data.firstName,
                        lastName: sessionInfo.data.lastName,
                        id: sessionInfo.data.id,
                        role: sessionInfo.data.type,
                        email: sessionInfo.data.email
                    };

                    currentSession = {
                        accessToken : sessionInfo.data.access_token,
                        expiresIn: new Date().getTime() + sessionInfo.data.expires_in * 1000,
                        refreshToken : sessionInfo.data.refresh_token
                    };

                    localStorage.setItem("currentUser", JSON.stringify(currentUser));
                    localStorage.setItem("currentSession", JSON.stringify(currentSession));
                    if (currentSession){
                        $http.defaults.headers.common.Authorization =
                            'Bearer ' + currentSession.accessToken;
                    }
                };

                service.destroySession = function () {
                    localStorage.removeItem("currentUser");
                    localStorage.removeItem("currentSession");
                    currentUser = null;
                    currentSession = null;
                };

                service.loadSession = function () {
                    currentUser = JSON.parse(localStorage.getItem("currentUser"));
                    currentSession = JSON.parse(localStorage.getItem("currentSession"));
                    if (currentSession){
                        $http.defaults.headers.common.Authorization =
                            'Bearer ' + currentSession.accessToken;
                    }
                };

                service.isUserLoggedIn = function () {
                    if (currentSession && currentUser){
                        var now = new Date().getTime();
                        if (now < currentSession.expiresIn){
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                };

                service.getAccessToken = function () {
                    return currentSession.accessToken;
                };

                service.getCurrentUser = function () {
                    return currentUser;
                };


                return service;
            }])
})();
