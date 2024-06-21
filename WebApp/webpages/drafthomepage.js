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
function checkForUserDraftHistory() {
    return __awaiter(this, void 0, void 0, function () {
        var res, boolForCurrentDraft, res2, boolPastDrafts, resumeDraftButton, deleteDraftButton, historalDraftButton;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 6];
                    loadUserName();
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/checkForCurrentDraft/?username=" + getCookie("username"), {
                            method: 'POST'
                        })];
                case 2:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 3:
                    boolForCurrentDraft = _a.sent();
                    console.log("current draft - " + boolForCurrentDraft);
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/checkForPastDraft/?username=" + getCookie("username"), {
                            method: 'POST'
                        })];
                case 4:
                    res2 = _a.sent();
                    return [4 /*yield*/, res2.json()];
                case 5:
                    boolPastDrafts = _a.sent();
                    console.log("past drafts - " + boolPastDrafts);
                    if (boolForCurrentDraft == true) {
                        resumeDraftButton = document.getElementById("resumeDraftButton");
                        deleteDraftButton = document.getElementById("deleteDraftButton");
                        if (resumeDraftButton == null || deleteDraftButton == null) {
                            console.log("resumeDraftButton or deleteDraftButton is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            resumeDraftButton.style.display = "inline-block";
                            deleteDraftButton.style.display = "inline-block";
                        }
                    }
                    if (boolPastDrafts == true) {
                        historalDraftButton = document.getElementById("historalDraftButton");
                        if (historalDraftButton == null) {
                            console.log("historalDraftButton is null. Try again.");
                        }
                        else {
                            historalDraftButton.style.display = "inline-block";
                        }
                    }
                    return [3 /*break*/, 7];
                case 6:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 7;
                case 7: return [2 /*return*/];
            }
        });
    });
}
function selectedStartNewDraft() {
    return __awaiter(this, void 0, void 0, function () {
        var res, boolForCurrentDraft, draftModeSelectContainer, draftFormContainer;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 4];
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/checkForCurrentDraft/?username=" + getCookie("username"), {
                            method: 'POST'
                        })];
                case 2:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 3:
                    boolForCurrentDraft = _a.sent();
                    console.log(boolForCurrentDraft);
                    if (boolForCurrentDraft == true) {
                        alert("You already have a draft in progress. Please resume that draft or delete it before starting a new one.");
                    }
                    else {
                        draftModeSelectContainer = document.getElementById("draftModeSelectContainer");
                        draftFormContainer = document.getElementById("draftFormContainer");
                        if (draftModeSelectContainer == null || draftFormContainer == null) {
                            console.log("draftModeSelectContainer or draftFormContainer is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            draftModeSelectContainer.style.display = "none";
                            draftFormContainer.style.display = "block";
                        }
                    }
                    return [3 /*break*/, 5];
                case 4:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 5;
                case 5: return [2 /*return*/];
            }
        });
    });
}
function selectedResumeLastSavedDraft() {
    return __awaiter(this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if ((_a.sent()) == true) {
                        document.cookie = "draftType=resume;path=/";
                        window.location.href = "draftpage.html";
                    }
                    else {
                        console.log("User not logged in. Redirecting to login page.");
                        deleteAllCookies();
                    }
                    return [2 /*return*/];
            }
        });
    });
}
function selectedViewDraftHistory() {
    return __awaiter(this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if ((_a.sent()) == true) {
                        window.location.href = "drafthistory.html";
                    }
                    else {
                        console.log("User not logged in. Redirecting to login page.");
                        deleteAllCookies();
                    }
                    return [2 /*return*/];
            }
        });
    });
}
function selectedDeleteCurrentDraft() {
    return __awaiter(this, void 0, void 0, function () {
        var res, boolForCurrentDraft, resumeDraftButton, deleteDraftButton;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 4];
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/deleteThisDraft/?username=" + getCookie("username"), {
                            method: 'POST'
                        })];
                case 2:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 3:
                    boolForCurrentDraft = _a.sent();
                    console.log(boolForCurrentDraft);
                    alert("Draft deleted. You may now start a new draft.");
                    resumeDraftButton = document.getElementById("resumeDraftButton");
                    deleteDraftButton = document.getElementById("deleteDraftButton");
                    if (resumeDraftButton == null || deleteDraftButton == null) {
                        console.log("resumeDraftButton or deleteDraftButton is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        resumeDraftButton.style.display = "none";
                        deleteDraftButton.style.display = "none";
                    }
                    return [3 /*break*/, 5];
                case 4:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 5;
                case 5: return [2 /*return*/];
            }
        });
    });
}
function startDraft() {
    return __awaiter(this, void 0, void 0, function () {
        function checkStartDraftInput(teamName, draftSize, draftPosition) {
            if (typeof teamName != "string") {
                alert(teamName + " - Not a valid string value to name your team. Try again.");
                return false;
            }
            if (typeof draftSize != "number" ||
                !Number.isInteger(draftSize)) {
                alert(draftSize + " - Not a valid int entered for the draft size. Try again.");
                return false;
            }
            if (typeof draftPosition != "number" ||
                !Number.isInteger(draftPosition) ||
                draftPosition > draftSize) {
                alert(draftPosition + " - Not a valid int entered for your starting draft position. Try again.");
                return false;
            }
            return true;
        }
        var teamNameInput, sizeOfTeamsInput, draftPositionInput, teamName, draftSizeString, draftPositionString, draftSize, draftPosition, res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 7];
                    teamNameInput = document.getElementById("teamNameInput");
                    sizeOfTeamsInput = document.getElementById("sizeOfTeamsInput");
                    draftPositionInput = document.getElementById("draftPositionInput");
                    if (!(teamNameInput == null || sizeOfTeamsInput == null || draftPositionInput == null)) return [3 /*break*/, 2];
                    console.log("teamNameInput or sizeOfTeamsInput or draftPositionInput is null. Try again.");
                    return [2 /*return*/];
                case 2:
                    teamName = teamNameInput.value;
                    draftSizeString = sizeOfTeamsInput.value;
                    draftPositionString = draftPositionInput.value;
                    draftSize = parseInt(draftSizeString);
                    draftPosition = parseInt(draftPositionString);
                    if (!(checkStartDraftInput(teamName, draftSize, draftPosition) == true)) return [3 /*break*/, 5];
                    return [4 /*yield*/, fetch(("http://localhost:80/api/teams/startDraft/?username=" + getCookie("username") + "&teamName=" + teamName + "&draftSize=" + draftSize + "&draftPosition=" + draftPosition), {
                            method: 'POST'
                        })];
                case 3:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 4:
                    data = _a.sent();
                    console.log(data);
                    document.cookie = "teamName=" + teamName + "; path=/";
                    document.cookie = "draftPosition=" + draftPositionString + "; path=/";
                    _a.label = 5;
                case 5:
                    document.cookie = "draftType=new; path=/";
                    window.location.href = "draftpage.html";
                    _a.label = 6;
                case 6: return [3 /*break*/, 8];
                case 7:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 8;
                case 8: return [2 /*return*/];
            }
        });
    });
}
