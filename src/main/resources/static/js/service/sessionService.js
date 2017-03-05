(function () {
    angular.module("OfficeManagementSystem")
        .service("SessionService", ["$http", "$httpParamSerializer",
    function ($http, $httpParamSerializer) {
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
                accessTokenExpiresIn: new Date().getTime() + sessionInfo.data.expires_in * 1000,
                accessToken : sessionInfo.data.access_token,
                refreshToken : sessionInfo.data.refresh_token,
                refreshTokenExpiresIn: new Date().getTime() + 1209600 * 1000
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

        service.isOlderThanNow = function (time) {
            return new Date().getTime() < time;
        };

        service.loadSession = function () {
            currentUser = JSON.parse(localStorage.getItem("currentUser"));
            currentSession = JSON.parse(localStorage.getItem("currentSession"));
            if (currentSession){
                if (!service.isOlderThanNow(currentSession.accessTokenExpiresIn)) {
                    if (service.isOlderThanNow(currentSession.refreshTokenExpiresIn)){
                        var personCredentials = {
                            grant_type: "refresh_token",
                            refresh_token: currentSession.refreshToken,
                            client_id: "client"
                        };
                        var encoded = btoa("client:");
                        var req = {
                            method: 'POST',
                            url: "/oauth/token",
                            headers: {
                                "Authorization": "Basic " + encoded,
                                "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                            },
                            data: $httpParamSerializer(personCredentials)
                        };
                        $http(req).then(function (callback) {
                            service.createSession(callback);
                        }, function (callback) {
                            console.log("Error");
                            console.log(callback);
                        });
                    }
                }
                    $http.defaults.headers.common.Authorization =
                        'Bearer ' + currentSession.accessToken;
            }
        };

        service.isUserLoggedIn = function () {
            if (currentSession && currentUser){
                var now = new Date().getTime();
                if (now < currentSession.accessTokenExpiresIn){
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
