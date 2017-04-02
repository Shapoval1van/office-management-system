(function () {
    angular.module("OfficeManagementSystem")
        .service("SessionService", ["$http", "$httpParamSerializer","$location","$q",
    function ($http, $httpParamSerializer, $location, $q) {
        var service = {};

        var currentUser = null;
        var currentSession = null;
        var reloading = null;

        service.__isTokenValid = function () {
            currentSession = JSON.parse(localStorage.getItem("currentSession"));
            if (currentSession){
                if (!this._isOlderThanNow(currentSession.accessTokenExpiresIn)){
                    console.log("SESSION_SERVICE: Token is not valid.");
                    return false;
                } else {
                    console.log("SESSION_SERVICE: Token is valid.");
                    if (currentSession) {
                        service.__setSession();
                        console.log("SESSION_SERVICE: Token was added to headers.");
                    }
                    return true;
                }
            } else {
                console.log("SESSION_SERVICE: There is no old session.");
                currentSession = null;
                return false;
            }
        };

        service.__isRefreshTokenValid = function () {
            currentSession = JSON.parse(localStorage.getItem("currentSession"));
            if (currentSession){
                if (!this._isOlderThanNow(currentSession.refreshTokenExpiresIn)){
                    console.log("SESSION_SERVICE: RefreshToken is not valid.");
                    return false;
                } else {
                    console.log("SESSION_SERVICE: RefreshToken is valid.");
                    return true;
                }
            } else {
                console.log("SESSION_SERVICE: There is no old session.");
                currentSession = null;
                return false;
            }
        };

        service.__isOldSession = function () {
            currentSession = JSON.parse(localStorage.getItem("currentSession"));
            currentUser = JSON.parse(localStorage.getItem("currentUser"));
            if (currentSession){
                console.log("SESSION_SERVICE: Old session is loaded.");
                return true;
            } else {
                console.log("SESSION_SERVICE: There is no old session.");
                currentSession = null;
                currentUser = null;
                return false;
            }
        };

        service.__createSession = function (sessionInfo) {

            if (!currentUser){
                currentUser = {
                    firstName: sessionInfo.data.firstName,
                    lastName: sessionInfo.data.lastName,
                    id: sessionInfo.data.id,
                    role: sessionInfo.data.type,
                    email: sessionInfo.data.email
                };
            }

            currentSession = {
                accessTokenExpiresIn: new Date().getTime() + sessionInfo.data.expires_in * 1000,
                accessToken: sessionInfo.data.access_token,
                refreshToken: sessionInfo.data.refresh_token,
                refreshTokenExpiresIn: new Date().getTime() + 1209600 * 1000,
                reloadTime: sessionInfo.data.expires_in*0.8*1000
            };

            service.__setSession();

        };

        service.__setSession = function () {

            localStorage.setItem("currentUser", JSON.stringify(currentUser));
            localStorage.setItem("currentSession", JSON.stringify(currentSession));

            if (currentSession) {
                $http.defaults.headers.common.Authorization = 'Bearer ' + currentSession.accessToken;
                if (reloading){
                    clearInterval(reloading);
                }
                reloading = setInterval(service.__refreshToken, currentSession.reloadTime);

                console.log("SESSION_SERVICE: Session was created.");
            }
        };

        service.__refreshToken = function () {
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
            return $http(req).then(function (callback) {
                console.log("SESSION_SERVICE: Token was refreshed successfully.");
                service.__createSession(callback);
                callback.isError = false;
                return callback;
            }, function (callback) {
                console.log("SESSION_SERVICE: Token was not refreshed.");
                currentSession = null;
                $location.path("/login");
            });
        };

        /*service.loadOldSession = function (succes, fail) {
            console.log("SESSION_SERVICE: Try to load old session.");
          if (service.__isOldSession()){
              if (service.__isTokenValid()){
                  return;
              } else if (service.__isRefreshTokenValid()){
                  return service.__refreshToken();
              } else {
                  $location.path("/login");
              }
          } else {
              $location.path("/login");
          };
        };
*/
        service.loadOldSession = function () {
            console.log("SESSION_SERVICE: Try to load old session.");
            var deferred = $q.defer();
            var result = {
                isOk: false
            };
            if (service.__isOldSession()){
                if (service.__isTokenValid()){
                    result.isOk = true;
                    deferred.resolve(result);
                } else if (service.__isRefreshTokenValid()){
                    service.__refreshToken().then(function (callback) {
                        if (callback.isError == false){
                            result.isOk = true;
                            deferred.resolve(result);
                        } else {
                            deferred.resolve(result);
                        }
                    })
                } else {
                    deferred.resolve(result);
                }
            } else {
                deferred.resolve(result);
            };
            return deferred.promise;
        };

        service.performLogin = function (username, password) {

            var personCredentials = {
                grant_type: "password",
                username: username,
                password: password,
                client_id: "client",
                scope: "read write"
            };

            var req = {
                method: 'POST',
                url: "/oauth/token",
                headers: {
                    "Authorization": "Basic " + btoa("client:"),
                    "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
                },
                data: $httpParamSerializer(personCredentials)
            };

            return $http(req).then(function (callback) {
                service.__createSession(callback);
                callback.isError = false;
                return callback;
            }, function (callback) {
                callback.isError = true;
                return callback;
            });
        };

        service.destroySession = function () {
            localStorage.removeItem("currentUser");
            localStorage.removeItem("currentSession");
            currentUser = null;
            currentSession = null;
            if (reloading){
                clearInterval(reloading);
            }
        };

//==============================================================================================================
//=============================================== Additional methods ===========================================
//==============================================================================================================

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

        service.getUserName = function () {
            if (currentUser){
                return currentUser.firstName+" "+currentUser.lastName;
            } else {
                return null;
            }
        };

        service.getUserRole = function () {
          if (currentUser){
              return currentUser.role;
          } else {
              return null;
          }
        };

        service._isOlderThanNow = function (time) {
            return new Date().getTime() < time;
        };

        return service;
    }])
})();
