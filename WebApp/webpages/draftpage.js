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
function setUpDraft() {
    return __awaiter(this, void 0, void 0, function () {
        var draftType, endOfDraft, draftNotStartedForm, draftControllerForm, currPick, nextUserPick, currRound, waitingDraftControllerForm, currUserPickRound, currUserPickSpot, currRoundSpan, nextUserPickRoundSpan, currPickSpan, nextUserPickSpan, draftNotStartedForm, res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 12];
                    loadUserName();
                    draftType = getCookie("draftType");
                    if (!(draftType == "resume")) return [3 /*break*/, 10];
                    return [4 /*yield*/, endDraft()];
                case 2:
                    endOfDraft = _a.sent();
                    if (!(endOfDraft == false)) return [3 /*break*/, 6];
                    draftNotStartedForm = document.getElementById("draftNotStartedForm");
                    draftControllerForm = document.getElementById("draftControllerForm");
                    if (draftNotStartedForm == null || draftControllerForm == null) {
                        console.log("draftNotStartedForm or draftControllerForm is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        draftNotStartedForm.style.display = "none";
                        draftControllerForm.style.display = "none";
                    }
                    return [4 /*yield*/, getCurrPick()];
                case 3:
                    currPick = _a.sent();
                    return [4 /*yield*/, getNextUserPick()];
                case 4:
                    nextUserPick = _a.sent();
                    return [4 /*yield*/, getCurrRound()];
                case 5:
                    currRound = _a.sent();
                    console.log("currPick = " + currPick);
                    console.log("nextUserPick = " + nextUserPick);
                    waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
                    currUserPickRound = document.getElementById("currUserPickRound");
                    currUserPickSpot = document.getElementById("currUserPickSpot");
                    if (draftControllerForm == null || waitingDraftControllerForm == null || currUserPickRound == null || currUserPickSpot == null) {
                        console.log("draftControllerForm or waitingDraftControllerForm is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        if (currPick == nextUserPick) {
                            draftControllerForm.style.display = "block";
                            waitingDraftControllerForm.style.display = "none";
                            currUserPickRound.innerHTML = String(currRound);
                            currUserPickSpot.innerHTML = String(currPick);
                        }
                        else {
                            waitingDraftControllerForm.style.display = "block";
                            currRoundSpan = document.getElementById("currRound");
                            nextUserPickRoundSpan = document.getElementById("nextUserPickRound");
                            if (currRoundSpan == null || nextUserPickRoundSpan == null) {
                                console.log("currRoundSpan or nextUserPickRoundSpan is null. Try again.");
                                return [2 /*return*/];
                            }
                            else {
                                currRoundSpan.innerHTML = String(currRound);
                                nextUserPickRoundSpan.innerHTML = String(currRound + 1);
                            }
                        }
                    }
                    currPickSpan = document.getElementById("currPick");
                    nextUserPickSpan = document.getElementById("nextUserPick");
                    if (currPickSpan == null || nextUserPickSpan == null) {
                        console.log("currPickSpan or nextUserPickSpan is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        currPickSpan.innerHTML = String(currPick);
                        nextUserPickSpan.innerHTML = String(nextUserPick);
                    }
                    return [3 /*break*/, 7];
                case 6:
                    draftNotStartedForm = document.getElementById("draftNotStartedForm");
                    if (draftNotStartedForm == null) {
                        console.log("draftNotStartedForm is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        draftNotStartedForm.style.display = "none";
                    }
                    _a.label = 7;
                case 7:
                    getPlayerLeft();
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/getAllPlayersDrafted/?username=" + getCookie("username"), {
                            method: 'GET'
                        })];
                case 8:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 9:
                    data = _a.sent();
                    parseDraftLogData(data);
                    return [3 /*break*/, 11];
                case 10:
                    if (draftType == "new") {
                        console.log("Staring New draft");
                    }
                    else
                        console.log("Error .... draftType = " + draftType);
                    _a.label = 11;
                case 11: return [3 /*break*/, 13];
                case 12:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 13;
                case 13: return [2 /*return*/];
            }
        });
    });
}
function startDraftFromDraftPage() {
    return __awaiter(this, void 0, void 0, function () {
        var draftNotStartedForm, draftControllerForm, waitingDraftControllerForm, currPickSpan, nextUserPickSpan, draftLogPar, draftPosition, _a, _b, _c, _d, _e, _f, _g, _h;
        return __generator(this, function (_j) {
            switch (_j.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_j.sent()) == true)) return [3 /*break*/, 11];
                    draftNotStartedForm = document.getElementById("draftNotStartedForm");
                    draftControllerForm = document.getElementById("draftControllerForm");
                    waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
                    currPickSpan = document.getElementById("currPick");
                    nextUserPickSpan = document.getElementById("nextUserPick");
                    draftLogPar = document.getElementById("draftLogPar");
                    if (draftNotStartedForm == null) {
                        console.log("draftNotStartedForm is null. Try again.");
                        return [2 /*return*/];
                    }
                    else
                        draftNotStartedForm.style.display = "none";
                    draftPosition = parseInt(getCookie("draftPosition"));
                    if (!(draftPosition == 1)) return [3 /*break*/, 6];
                    if (!(draftControllerForm == null || currPickSpan == null || nextUserPickSpan == null)) return [3 /*break*/, 2];
                    console.log("draftControllerForm or currPickSpan or nextUserPickSpan is null. Try again.");
                    return [3 /*break*/, 5];
                case 2:
                    draftControllerForm.style.display = "block";
                    _a = currPickSpan;
                    _b = String;
                    return [4 /*yield*/, getCurrPick()];
                case 3:
                    _a.innerHTML = _b.apply(void 0, [_j.sent()]);
                    _c = nextUserPickSpan;
                    _d = String;
                    return [4 /*yield*/, getNextUserPick()];
                case 4:
                    _c.innerHTML = _d.apply(void 0, [_j.sent()]);
                    _j.label = 5;
                case 5: return [3 /*break*/, 10];
                case 6:
                    if (!(waitingDraftControllerForm == null || currPickSpan == null || nextUserPickSpan == null)) return [3 /*break*/, 7];
                    console.log("waitingDraftControllerForm or currPickSpan or nextUserPickSpan is null. Try again.");
                    return [2 /*return*/];
                case 7:
                    waitingDraftControllerForm.style.display = "block";
                    _e = currPickSpan;
                    _f = String;
                    return [4 /*yield*/, getCurrPick()];
                case 8:
                    _e.innerHTML = _f.apply(void 0, [_j.sent()]);
                    _g = nextUserPickSpan;
                    _h = String;
                    return [4 /*yield*/, getNextUserPick()];
                case 9:
                    _g.innerHTML = _h.apply(void 0, [_j.sent()]);
                    _j.label = 10;
                case 10:
                    if (draftLogPar == null) {
                        console.log("draftLogPar is null. Try again.");
                        return [2 /*return*/];
                    }
                    draftLogPar.innerHTML = "Waiting for user input...";
                    getPlayerLeft();
                    return [3 /*break*/, 12];
                case 11:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _j.label = 12;
                case 12: return [2 /*return*/];
            }
        });
    });
}
function getCurrRound() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("http://localhost:80/api/teams/getCurrRound/?username=" + getCookie("username"), {
                        method: 'GET'
                    })];
                case 1:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 2:
                    data = _a.sent();
                    console.log(data);
                    return [2 /*return*/, data];
            }
        });
    });
}
function getCurrPick() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("http://localhost:80/api/teams/getCurrPick/?username=" + getCookie("username"), {
                        method: 'GET'
                    })];
                case 1:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 2:
                    data = _a.sent();
                    console.log(data);
                    return [2 /*return*/, data];
            }
        });
    });
}
function getNextUserPick() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("http://localhost:80/api/teams/getNextUserPick/?username=" + getCookie("username"), {
                        method: 'GET'
                    })];
                case 1:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 2:
                    data = _a.sent();
                    console.log(data);
                    return [2 /*return*/, data];
            }
        });
    });
}
function getNextUserPickRound() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("http://localhost:80/api/teams/getNextUserPickRound/?username=" + getCookie("username"), {
                        method: 'GET'
                    })];
                case 1:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 2:
                    data = _a.sent();
                    console.log(data);
                    return [2 /*return*/, data];
            }
        });
    });
}
function getPlayerLeft() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data, returnVal, intCurrPlayer, currPlayer, PlayerLeftPar;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("http://localhost:80/api/teams/getPlayersLeft/?username=" + getCookie("username"), {
                        method: 'GET'
                    })];
                case 1:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 2:
                    data = _a.sent();
                    console.log(data);
                    returnVal = "";
                    for (intCurrPlayer in data) {
                        currPlayer = data[intCurrPlayer];
                        returnVal += ((1 + Number(intCurrPlayer)) + ". " + currPlayer.fullName + " " + currPlayer.position + " - Predicted Score 2023 = "
                            + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />");
                    }
                    console.log(returnVal);
                    PlayerLeftPar = document.getElementById("playerListPar");
                    if (PlayerLeftPar == null) {
                        console.log("PlayerLeftPar is null. Try again.");
                    }
                    else {
                        PlayerLeftPar.innerHTML = returnVal;
                    }
                    return [2 /*return*/];
            }
        });
    });
}
function updatePlayerListPar(data) {
    return __awaiter(this, void 0, void 0, function () {
        var returnVal, intCurrPlayer, currPlayer, PlayerLeftPar;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if ((_a.sent()) == true) {
                        returnVal = "";
                        for (intCurrPlayer in data) {
                            currPlayer = data[intCurrPlayer];
                            returnVal += ((1 + Number(intCurrPlayer)) + ". " + currPlayer.fullName + ", " + currPlayer.position + " - Predicted Score 2023 = "
                                + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />");
                        }
                        PlayerLeftPar = document.getElementById("playerListPar");
                        if (PlayerLeftPar == null) {
                            console.log("PlayerLeftPar is null. Try again.");
                        }
                        else {
                            PlayerLeftPar.innerHTML = returnVal;
                        }
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
function parseDraftLogData(data) {
    return __awaiter(this, void 0, void 0, function () {
        var updateVal, currPick, currRound, draftLogPar_1, draftLogPar_2, _a, _b, _i, intCurrPlayer, currPlayer, draftLogPar;
        return __generator(this, function (_c) {
            switch (_c.label) {
                case 0: return [4 /*yield*/, getCurrPick()];
                case 1:
                    currPick = _c.sent();
                    return [4 /*yield*/, getCurrRound()];
                case 2:
                    currRound = _c.sent();
                    if (currPick == 1 && currRound == 1) {
                        draftLogPar_1 = document.getElementById("draftLogPar");
                        if (draftLogPar_1 == null) {
                            console.log("draftLogPar is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            draftLogPar_1.innerHTML = "";
                            updateVal = "";
                        }
                    }
                    else {
                        draftLogPar_2 = document.getElementById("draftLogPar");
                        if (draftLogPar_2 == null) {
                            console.log("draftLogPar is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            updateVal = draftLogPar_2.innerHTML;
                        }
                    }
                    _a = [];
                    for (_b in data)
                        _a.push(_b);
                    _i = 0;
                    _c.label = 3;
                case 3:
                    if (!(_i < _a.length)) return [3 /*break*/, 7];
                    intCurrPlayer = _a[_i];
                    currPlayer = data[intCurrPlayer];
                    console.log(currPlayer);
                    if (!(currPlayer.firstName == null)) return [3 /*break*/, 5];
                    return [4 /*yield*/, endDraft()];
                case 4:
                    _c.sent();
                    return [3 /*break*/, 7];
                case 5:
                    updateVal += currPlayer.spotDrafted + " - " + currPlayer.teamDraftedBy + " picked " + currPlayer.fullName + ", " + currPlayer.position + " - Predicted Score 2023 = " +
                        currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />";
                    _c.label = 6;
                case 6:
                    _i++;
                    return [3 /*break*/, 3];
                case 7:
                    draftLogPar = document.getElementById("draftLogPar");
                    if (draftLogPar == null) {
                        console.log("draftLogPar is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        draftLogPar.innerHTML = updateVal;
                    }
                    return [2 /*return*/];
            }
        });
    });
}
function checkToClearDraftLog() {
    return __awaiter(this, void 0, void 0, function () {
        var currPick, currRound, draftLogPar;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, getCurrPick()];
                case 1:
                    currPick = _a.sent();
                    return [4 /*yield*/, getCurrRound()];
                case 2:
                    currRound = _a.sent();
                    if (currPick == 1 && currRound == 1) {
                        draftLogPar = document.getElementById("draftLogPar");
                        if (draftLogPar == null) {
                            console.log("draftLogPar is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            draftLogPar.innerHTML = "";
                        }
                    }
                    return [2 /*return*/];
            }
        });
    });
}
function changeFormForNextPick() {
    return __awaiter(this, void 0, void 0, function () {
        var nextDraftPick, currRound, nextUserPick, nextUserPickRound, draftControllerForm, waitingDraftControllerForm, draftControllerForm, waitingDraftControllerForm, currPickSpan, currRoundSpan, nextPickInfo, nextUserPickSpan, nextUserPickRoundSpan, currUserPickRoundSpan, currUserPickSpotSpan;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, getCurrPick()];
                case 1:
                    nextDraftPick = _a.sent();
                    return [4 /*yield*/, getCurrRound()];
                case 2:
                    currRound = _a.sent();
                    return [4 /*yield*/, getNextUserPick()];
                case 3:
                    nextUserPick = _a.sent();
                    return [4 /*yield*/, getNextUserPickRound()];
                case 4:
                    nextUserPickRound = _a.sent();
                    console.log(currRound + "." + nextDraftPick + " - " + nextUserPick);
                    if (nextDraftPick == nextUserPick && nextUserPickRound == currRound) {
                        draftControllerForm = document.getElementById("draftControllerForm");
                        waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
                        if (draftControllerForm == null || waitingDraftControllerForm == null) {
                            console.log("draftControllerForm or waitingDraftControllerForm is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            draftControllerForm.style.display = "block";
                            waitingDraftControllerForm.style.display = "none";
                        }
                    }
                    else {
                        draftControllerForm = document.getElementById("draftControllerForm");
                        waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
                        if (draftControllerForm == null || waitingDraftControllerForm == null) {
                            console.log("draftControllerForm or waitingDraftControllerForm is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            draftControllerForm.style.display = "none";
                            waitingDraftControllerForm.style.display = "block";
                        }
                    }
                    currPickSpan = document.getElementById("currPick");
                    currRoundSpan = document.getElementById("currRound");
                    if (currPickSpan == null || currRoundSpan == null) {
                        console.log("currPickSpan or currRoundSpan is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        currPickSpan.innerHTML = String(nextDraftPick);
                        currRoundSpan.innerHTML = String(currRound);
                    }
                    if (currRound >= 15 && nextUserPickRound >= 16) {
                        nextPickInfo = document.getElementById("nextPickInfo");
                        if (nextPickInfo == null) {
                            console.log("nextUserPickSpan is null. Try again.");
                            return [2 /*return*/];
                        }
                        else
                            nextPickInfo.innerHTML = "the draft will end at the end of this round. " +
                                "Press the button below to end the draft.";
                    }
                    else {
                        nextUserPickSpan = document.getElementById("nextUserPick");
                        nextUserPickRoundSpan = document.getElementById("nextUserPickRound");
                        currUserPickRoundSpan = document.getElementById("currUserPickRound");
                        currUserPickSpotSpan = document.getElementById("currUserPickSpot");
                        if (nextUserPickSpan == null || nextUserPickRoundSpan == null || currUserPickRoundSpan == null || currUserPickSpotSpan == null) {
                            console.log("nextUserPickSpan or nextUserPickRoundSpan or currUserPickRoundSpan or currUserPickSpotSpan is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            nextUserPickSpan.innerHTML = String(nextUserPick);
                            nextUserPickRoundSpan.innerHTML = String(nextUserPickRound);
                            currUserPickRoundSpan.innerHTML = String(currRound);
                            currUserPickSpotSpan.innerHTML = String(nextUserPick);
                        }
                    }
                    return [2 /*return*/];
            }
        });
    });
}
function simulateToNextPick() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 9];
                    return [4 /*yield*/, checkToClearDraftLog()];
                case 2:
                    _a.sent();
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/simTo/?username=" + getCookie("username"), {
                            method: 'POST'
                        })];
                case 3:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 4:
                    data = _a.sent();
                    return [4 /*yield*/, getPlayerLeft()];
                case 5:
                    _a.sent();
                    parseDraftLogData(data);
                    return [4 /*yield*/, endDraft()];
                case 6:
                    if (!((_a.sent()) == false)) return [3 /*break*/, 8];
                    return [4 /*yield*/, changeFormForNextPick()];
                case 7:
                    _a.sent();
                    _a.label = 8;
                case 8: return [3 /*break*/, 10];
                case 9:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 10;
                case 10: return [2 /*return*/];
            }
        });
    });
}
function userDraftPlayer() {
    return __awaiter(this, void 0, void 0, function () {
        var res1, data1, nextDraftPickElement, nextDraftNumber, res, data;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 11];
                    return [4 /*yield*/, checkToClearDraftLog()];
                case 2:
                    _a.sent();
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/getPlayersLeft/?username=" + getCookie("username"), {
                            method: 'GET'
                        })];
                case 3:
                    res1 = _a.sent();
                    return [4 /*yield*/, res1.json()];
                case 4:
                    data1 = _a.sent();
                    nextDraftPickElement = document.getElementById("nextDraftPick");
                    nextDraftNumber = nextDraftPickElement ? Number(nextDraftPickElement.value) : null;
                    if (nextDraftNumber == null || isNaN(Number(nextDraftPickElement.value)) || nextDraftNumber <= 0 || nextDraftNumber > data1.length) {
                        console.log("nextDraftPickNumber is null or out of range. Try again.");
                        alert("Please enter a valid number.");
                        return [2 /*return*/];
                    }
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/userDraftPlayer/?username=" + getCookie("username") + "&playerIndex=" + nextDraftNumber, {
                            method: 'POST'
                        })];
                case 5:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 6:
                    data = _a.sent();
                    console.log(data);
                    return [4 /*yield*/, getPlayerLeft()];
                case 7:
                    _a.sent();
                    parseDraftLogData(data);
                    return [4 /*yield*/, endDraft()];
                case 8:
                    if (!((_a.sent()) == false)) return [3 /*break*/, 10];
                    return [4 /*yield*/, changeFormForNextPick()];
                case 9:
                    _a.sent();
                    _a.label = 10;
                case 10: return [3 /*break*/, 12];
                case 11:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 12;
                case 12: return [2 /*return*/];
            }
        });
    });
}
function endDraft() {
    return __awaiter(this, void 0, void 0, function () {
        var draftControllerForm, waitingDraftControllerForm, draftCompleteForm;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, getCurrRound()];
                case 1:
                    if ((_a.sent()) >= 16) {
                        draftControllerForm = document.getElementById("draftControllerForm");
                        waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
                        draftCompleteForm = document.getElementById("draftCompleteForm");
                        if (draftControllerForm == null || waitingDraftControllerForm == null || draftCompleteForm == null) {
                            console.log("draftControllerForm or waitingDraftControllerForm or draftCompleteForm is null. Try again.");
                            return [2 /*return*/, null];
                        }
                        else {
                            draftControllerForm.style.display = "none";
                            waitingDraftControllerForm.style.display = "none";
                            draftCompleteForm.style.display = "block";
                        }
                        return [2 /*return*/, true];
                    }
                    else
                        return [2 /*return*/, false];
                    return [2 /*return*/];
            }
        });
    });
}
function endOfCurrentDraft() {
    return __awaiter(this, void 0, void 0, function () {
        var res, boolForCurrentDraft;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 4];
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/userMarkCurrentDraftComplete/?username=" + getCookie("username"), {
                            method: 'POST'
                        })];
                case 2:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 3:
                    boolForCurrentDraft = _a.sent();
                    console.log(boolForCurrentDraft);
                    goToHomePage();
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
