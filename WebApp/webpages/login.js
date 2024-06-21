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
function addUser() {
    return __awaiter(this, void 0, void 0, function () {
        var userNameEleValue, passwordEleValue, username, password, checkUserRes, checkUserData, addUser_1, addUserRes, setUpUserRes, setUpUserData;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    userNameEleValue = document.getElementById("username").value || null;
                    passwordEleValue = document.getElementById("password").value || null;
                    if (userNameEleValue == null || passwordEleValue == null) {
                        alert("Username or password is null. Try again.");
                        return [2 /*return*/];
                    }
                    username = userNameEleValue;
                    password = passwordEleValue;
                    return [4 /*yield*/, fetch("http://localhost:80/api/login/checkUser/?username=" + username, {
                            method: 'GET'
                        })];
                case 1:
                    checkUserRes = _a.sent();
                    return [4 /*yield*/, checkUserRes.json()];
                case 2:
                    checkUserData = _a.sent();
                    if (!(checkUserData == true)) return [3 /*break*/, 3];
                    alert("Username already taken. Try again.");
                    return [3 /*break*/, 8];
                case 3: return [4 /*yield*/, fetch("http://localhost:80/api/login/addUser/?username=" + username + "&password=" + password, {
                        method: 'PUT'
                    })];
                case 4:
                    addUser_1 = _a.sent();
                    return [4 /*yield*/, addUser_1.json()];
                case 5:
                    addUserRes = _a.sent();
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/initaizeUserAccountSetup/?username=" + username, {
                            method: 'POST'
                        })];
                case 6:
                    setUpUserRes = _a.sent();
                    return [4 /*yield*/, setUpUserRes.json()];
                case 7:
                    setUpUserData = _a.sent();
                    console.log(addUserRes);
                    console.log(setUpUserData);
                    alert("Account created. Please login.");
                    window.location.href = "loginpage.html";
                    _a.label = 8;
                case 8: return [2 /*return*/];
            }
        });
    });
}
function attemptLogin() {
    return __awaiter(this, void 0, void 0, function () {
        var userNameEleValue, passwordEleValue, username, password, attemptLoginRes, attemptLoginData, generateSessionID, generateSessionIDData;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    userNameEleValue = document.getElementById("username").value || null;
                    passwordEleValue = document.getElementById("password").value || null;
                    if (userNameEleValue == null || passwordEleValue == null) {
                        alert("Username or password is null. Try again.");
                        return [2 /*return*/];
                    }
                    username = userNameEleValue;
                    password = passwordEleValue;
                    return [4 /*yield*/, fetch("http://localhost:80/api/login/attemptLogin/?username=" + username + "&password=" + password, {
                            method: 'GET'
                        })];
                case 1:
                    attemptLoginRes = _a.sent();
                    return [4 /*yield*/, attemptLoginRes.json()];
                case 2:
                    attemptLoginData = _a.sent();
                    if (!(attemptLoginData == true)) return [3 /*break*/, 5];
                    return [4 /*yield*/, fetch("http://localhost:80/api/login/generateSessionID/?username=" + username, {
                            method: 'GET'
                        })];
                case 3:
                    generateSessionID = _a.sent();
                    console.log(generateSessionID);
                    return [4 /*yield*/, generateSessionID.text()];
                case 4:
                    generateSessionIDData = _a.sent();
                    console.log(generateSessionIDData);
                    alert("Login successful.");
                    document.cookie = "username=" + username + "; path=/";
                    document.cookie = "sessionID=" + generateSessionIDData + "; path=/";
                    window.location.href = "drafthomepage.html";
                    return [3 /*break*/, 6];
                case 5:
                    alert("Login failed. Try different credentials.");
                    _a.label = 6;
                case 6: return [2 /*return*/];
            }
        });
    });
}
