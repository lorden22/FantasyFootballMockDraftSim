"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
function loadGifWhileRenderPage(functionToLoad) {
    setTimeout(function () {
        document.getElementById("loading-container").style.display = "none";
        document.getElementById("content-container").style.display = "block";
    }, 300);
    functionToLoad();
}
function loadGifWhileBigPageChange(functionToLoad) {
    setTimeout(function () {
        document.getElementById("loading-container").style.display = "none";
        document.getElementById("content-container").style.display = "block";
    }, 50);
    functionToLoad();
}
function goToHomePage() {
    deleteCookie("draftType");
    deleteCookie("draftPosition");
    deleteCookie("teamName");
    deleteCookie("draftIDToView");
    window.location.href = "drafthomepage.html";
}
function loadUserName() {
    console.log("UserName = " + getCookie("username"));
    if (getCookie("username") == "") {
        alert("You must be logged in to view this page.");
        window.location.href = "loginpage.html";
    }
    else {
        document.getElementById("userNameSpan").innerHTML = getCookie("username");
    }
}
function authenticateSession() {
    return __awaiter(this, void 0, void 0, function () {
        var authenticateSessionRes, _a, authenticateSessionData;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    _b.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, fetch("http://localhost:80/api/login/attemptSession/?username=" + getCookie("username") + "&sessionID=" + getCookie("sessionID"), {
                            method: 'GET'
                        })];
                case 1:
                    authenticateSessionRes = _b.sent();
                    return [3 /*break*/, 3];
                case 2:
                    _a = _b.sent();
                    alert("Server is down. Please try again later. Returning to login page.");
                    window.location.href = "loginpage.html";
                    return [2 /*return*/, false];
                case 3: return [4 /*yield*/, authenticateSessionRes.json()];
                case 4:
                    authenticateSessionData = _b.sent();
                    if (authenticateSessionData == true) {
                        console.log("Session authenticated.");
                        return [2 /*return*/, true];
                    }
                    else
                        return [2 /*return*/, false];
                    return [2 /*return*/];
            }
        });
    });
}
function logoutServerSide() {
    return __awaiter(this, void 0, void 0, function () {
        var logoutRes, logoutData;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("http://localhost:80/api/login/logout/?username=" + getCookie("username") + "&sessionID=" + getCookie("sessionID"), {
                        method: 'POST'
                    })];
                case 1:
                    logoutRes = _a.sent();
                    return [4 /*yield*/, logoutRes.json()];
                case 2:
                    logoutData = _a.sent();
                    if (logoutData == true) {
                        deleteAllCookies();
                        window.location.href = "loginpage.html";
                    }
                    else
                        alert("Logout failed.");
                    return [2 /*return*/];
            }
        });
    });
}
