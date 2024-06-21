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
function renderDraftHistoryTable() {
    return __awaiter(this, void 0, void 0, function () {
        function createRow(currDraftRowMetaData) {
            console.log(currDraftRowMetaData);
            var draftID = currDraftRowMetaData.draftID;
            console.log(draftID);
            var newDraftHistoryRow = document.createElement("tr");
            newDraftHistoryRow.id = "draftHistoryRow" + draftID;
            var newDraftHistoryRowDraftID = document.createElement("td");
            newDraftHistoryRowDraftID.id = "draftHistoryRowDraftID" + draftID;
            newDraftHistoryRowDraftID.innerHTML = draftID;
            var newDraftHistoryRowTeamName = document.createElement("td");
            newDraftHistoryRowTeamName.id = "draftHistoryRowTeamName" + draftID;
            newDraftHistoryRowTeamName.innerHTML = currDraftRowMetaData.teamName;
            var newDraftHistoryRowDraftPosition = document.createElement("td");
            newDraftHistoryRowDraftPosition.id = "draftHistoryRowDraftPosition" + draftID;
            newDraftHistoryRowDraftPosition.innerHTML = currDraftRowMetaData.draftPosition;
            var newDraftHistoryRowDraftSize = document.createElement("td");
            newDraftHistoryRowDraftSize.id = "draftHistoryRowDraftSize" + draftID;
            newDraftHistoryRowDraftSize.innerHTML = currDraftRowMetaData.draftSize;
            var newDraftHistoryRowDraftDate = document.createElement("td");
            newDraftHistoryRowDraftDate.id = "draftHistoryRowDraftDate" + draftID;
            newDraftHistoryRowDraftDate.innerHTML = currDraftRowMetaData.Date;
            var newDraftHistoryRowDraftTime = document.createElement("td");
            newDraftHistoryRowDraftTime.id = "draftHistoryRowDraftTime" + draftID;
            newDraftHistoryRowDraftTime.innerHTML = currDraftRowMetaData.Time;
            var newDraftHistoryRowDraftViewDraft = document.createElement("button");
            newDraftHistoryRowDraftViewDraft.id = "draftHistoryRowDraftViewDraft" + draftID;
            newDraftHistoryRowDraftViewDraft.innerHTML = "View Draft";
            newDraftHistoryRowDraftViewDraft.onclick = function () { viewDraft(draftID); };
            newDraftHistoryRowDraftViewDraft.className = "btn btn-primary";
            newDraftHistoryRow.appendChild(newDraftHistoryRowDraftID);
            newDraftHistoryRow.appendChild(newDraftHistoryRowTeamName);
            newDraftHistoryRow.appendChild(newDraftHistoryRowDraftPosition);
            newDraftHistoryRow.appendChild(newDraftHistoryRowDraftSize);
            newDraftHistoryRow.appendChild(newDraftHistoryRowDraftDate);
            newDraftHistoryRow.appendChild(newDraftHistoryRowDraftTime);
            newDraftHistoryRow.appendChild(newDraftHistoryRowDraftViewDraft);
            var draftHistoryTableBody = document.getElementById("draftHistoryTableBody");
            if (draftHistoryTableBody != null) {
                draftHistoryTableBody.appendChild(newDraftHistoryRow);
            }
        }
        var res, data, intCurrDraftMetaData, currDraftMetaData;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 4];
                    loadUserName();
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/getDraftHistoryMetaData/?username=" + getCookie("username"), {
                            method: 'GET'
                        })];
                case 2:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 3:
                    data = _a.sent();
                    console.log(data);
                    for (intCurrDraftMetaData in data) {
                        currDraftMetaData = data[intCurrDraftMetaData];
                        createRow(currDraftMetaData);
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
function viewDraft(draftID) {
    return __awaiter(this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            document.cookie = "draftIDToView=" + draftID + ";path=/";
            window.location.href = "draftreview.html";
            return [2 /*return*/];
        });
    });
}
function renderDraftReviewPage() {
    return __awaiter(this, void 0, void 0, function () {
        var backButton;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if ((_a.sent()) == true) {
                        loadUserName();
                        backButton = document.getElementById("backButton");
                        if (backButton == null) {
                            console.log("backButton is null. Try again.");
                            return [2 /*return*/];
                        }
                        backButton.style.display = "none";
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
function renderDraftHistoryPlayerLog() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data, draftHistoryPlayerLog, draftReviewSelecterForm, intCurrDraftLog, currPlayerDraftLog, newDraftHistoryPlayerLogRow, newDraftHistoryPlayerLogSpot, roundPickArray, newDraftHistoryPlayerLogRound, newDraftHistoryPlayerLogPick, newDraftHistoryPlayerLogPlayerName, newDraftHistoryPlayerLogPlayerPosition, newDraftHistoryPlayerLogPlayerScore, newDraftHistoryPlayerLogPlayerTeam, draftReviewPlayerLogBody, backButton;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 4];
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/getDraftHistoryPlayerLog/?username=" + getCookie("username") + "&draftID=" + getCookie("draftIDToView"), {
                            method: 'GET'
                        })];
                case 2:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 3:
                    data = _a.sent();
                    console.log(data);
                    draftHistoryPlayerLog = document.getElementById("draftHistoryPlayerLog");
                    draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
                    if (draftHistoryPlayerLog == null || draftReviewSelecterForm == null) {
                        console.log("draftReviewPlayerLogBody or draftReviewSelecterForm is null. Try again.");
                        return [2 /*return*/];
                    }
                    draftHistoryPlayerLog.style.display = "block";
                    draftReviewSelecterForm.style.display = "none";
                    for (intCurrDraftLog in data.slice(0, data.length - 1)) {
                        currPlayerDraftLog = data[intCurrDraftLog];
                        newDraftHistoryPlayerLogRow = document.createElement("tr");
                        newDraftHistoryPlayerLogRow.id = "draftHistoryPlayerLogRow" + intCurrDraftLog;
                        newDraftHistoryPlayerLogSpot = currPlayerDraftLog.spotDrafted;
                        roundPickArray = newDraftHistoryPlayerLogSpot.split(".");
                        newDraftHistoryPlayerLogRound = document.createElement("td");
                        newDraftHistoryPlayerLogRound.id = "draftHistoryPlayerLogRound" + intCurrDraftLog;
                        newDraftHistoryPlayerLogRound.innerHTML = roundPickArray[0];
                        newDraftHistoryPlayerLogPick = document.createElement("td");
                        newDraftHistoryPlayerLogPick.id = "draftHistoryPlayerLogPick" + intCurrDraftLog;
                        newDraftHistoryPlayerLogPick.innerHTML = roundPickArray[1];
                        newDraftHistoryPlayerLogPlayerName = document.createElement("td");
                        newDraftHistoryPlayerLogPlayerName.id = "draftHistoryPlayerLogPlayerName" + intCurrDraftLog;
                        newDraftHistoryPlayerLogPlayerName.innerHTML = currPlayerDraftLog.fullName;
                        newDraftHistoryPlayerLogPlayerPosition = document.createElement("td");
                        newDraftHistoryPlayerLogPlayerPosition.id = "draftHistoryPlayerLogPlayerPosition" + intCurrDraftLog;
                        newDraftHistoryPlayerLogPlayerPosition.innerHTML = currPlayerDraftLog.position;
                        newDraftHistoryPlayerLogPlayerScore = document.createElement("td");
                        newDraftHistoryPlayerLogPlayerScore.id = "draftHistoryPlayerLogPlayerScored" + intCurrDraftLog;
                        newDraftHistoryPlayerLogPlayerScore.innerHTML = currPlayerDraftLog.predictedScore;
                        newDraftHistoryPlayerLogPlayerTeam = document.createElement("td");
                        newDraftHistoryPlayerLogPlayerTeam.id = "draftHistoryPlayerLogPlayerTeam" + intCurrDraftLog;
                        newDraftHistoryPlayerLogPlayerTeam.innerHTML = currPlayerDraftLog.teamDraftedBy;
                        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogRound);
                        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPick);
                        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerName);
                        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerPosition);
                        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerScore);
                        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerTeam);
                        draftReviewPlayerLogBody = document.getElementById("draftReviewPlayerLogBody");
                        if (draftReviewPlayerLogBody == null) {
                            console.log("draftReviewPlayerLogBody is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            draftReviewPlayerLogBody.appendChild(newDraftHistoryPlayerLogRow);
                        }
                    }
                    backButton = document.getElementById("backButton");
                    if (backButton == null) {
                        console.log("backButton is null. Try again.");
                        return [2 /*return*/];
                    }
                    backButton.style.display = "inline";
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
function renderDraftHistoryTeamHistorySelecter() {
    return __awaiter(this, void 0, void 0, function () {
        var res, data, _loop_1, intCurrTeam, state_1, allTeamsTable, draftReviewSelecterForm, backButton;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 4];
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/getDraftHistoryTeamList/?username=" + getCookie("username") + "&draftID=" + getCookie("draftIDToView"), {
                            method: 'GET'
                        })];
                case 2:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 3:
                    data = _a.sent();
                    console.log(data);
                    _loop_1 = function (intCurrTeam) {
                        var currTeam = data[intCurrTeam];
                        var currTeamRow = document.createElement("tr");
                        currTeamRow.id = "draftHistoryTeamRow" + intCurrTeam;
                        var currTeamName = document.createElement("td");
                        currTeamName.id = "draftHistoryTeamName" + intCurrTeam;
                        currTeamName.innerHTML = currTeam.teamName;
                        var currTeamViewButton = document.createElement("btn");
                        currTeamViewButton.id = "draftHistoryViewButton" + intCurrTeam;
                        currTeamViewButton.innerHTML = "View Team";
                        currTeamViewButton.onclick = function () { viewTeam(currTeamViewButton.id.slice(-1)); };
                        currTeamViewButton.className = "btn btn-primary";
                        currTeamViewButton.style.margin = "5%";
                        currTeamViewButton.style.padding = "5%";
                        currTeamRow.appendChild(currTeamName);
                        currTeamRow.appendChild(currTeamViewButton);
                        var allTeamsTableBody = document.getElementById("allTeamsTableBody");
                        if (allTeamsTableBody == null) {
                            console.log("allTeamsTableBody is null. Try again.");
                            return { value: void 0 };
                        }
                        allTeamsTableBody.appendChild(currTeamRow);
                    };
                    for (intCurrTeam in data) {
                        state_1 = _loop_1(intCurrTeam);
                        if (typeof state_1 === "object")
                            return [2 /*return*/, state_1.value];
                    }
                    allTeamsTable = document.getElementById("allTeamsTable");
                    draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
                    backButton = document.getElementById("backButton");
                    if (allTeamsTable == null || draftReviewSelecterForm == null || backButton == null) {
                        console.log("allTeamsTable or draftReviewSelecterForm or backButton is null. Try again.");
                        return [2 /*return*/];
                    }
                    allTeamsTable.style.display = "block";
                    draftReviewSelecterForm.style.display = "none";
                    backButton.style.display = "inline";
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
function viewTeam(teamID) {
    return __awaiter(this, void 0, void 0, function () {
        function findStarterIndex(players) {
            var highestScore = -1;
            var highestScoreIndex = -1;
            for (var i = 0; i < players.length; i++) {
                if (players[i].predictedScore > highestScore) {
                    highestScore = players[i].predictedScore;
                    highestScoreIndex = i;
                }
            }
            return highestScoreIndex;
        }
        function findBenchedPlayers(benchPlayers) {
            return __awaiter(this, void 0, void 0, function () {
                var teamHistoryTableBody, i, newBenchPlayerRow, newBenchPlayerDepthChartPosition, newBenchPlayerPosition, newBenchPlayerName, newBenchPlayerPredictedScore, newBenchPlayerAvgADP, newBenchPlayerSpotDrafted;
                return __generator(this, function (_a) {
                    teamHistoryTableBody = document.getElementById("teamHistoryTableBody");
                    if (teamHistoryTableBody == null) {
                        console.log("benchPlayerTableBody is null. Try again.");
                        return [2 /*return*/];
                    }
                    else {
                        for (i = 0; i < benchPlayers.length; i++) {
                            newBenchPlayerRow = document.createElement("tr");
                            newBenchPlayerRow.id = "benchPlayerRow" + i;
                            newBenchPlayerRow.style.width = "100%";
                            newBenchPlayerDepthChartPosition = document.createElement("td");
                            newBenchPlayerDepthChartPosition.id = "depthChartPositionBench" + i;
                            newBenchPlayerDepthChartPosition.innerHTML = "Bench";
                            newBenchPlayerPosition = document.createElement("td");
                            newBenchPlayerPosition.id = "benchPlayerPosition" + i;
                            newBenchPlayerPosition.innerHTML = benchPlayers[i].position;
                            newBenchPlayerName = document.createElement("td");
                            newBenchPlayerName.id = "benchPlayerName" + i;
                            newBenchPlayerName.innerHTML = benchPlayers[i].fullName;
                            newBenchPlayerPredictedScore = document.createElement("td");
                            newBenchPlayerPredictedScore.id = "benchPlayerPredictedScore" + i;
                            newBenchPlayerPredictedScore.innerHTML = "" + benchPlayers[i].predictedScore;
                            newBenchPlayerAvgADP = document.createElement("td");
                            newBenchPlayerAvgADP.id = "benchPlayerAvgADP" + i;
                            newBenchPlayerAvgADP.innerHTML = "" + benchPlayers[i].avgADP;
                            newBenchPlayerSpotDrafted = document.createElement("td");
                            newBenchPlayerSpotDrafted.id = "benchPlayerSpotDrafted" + i;
                            newBenchPlayerSpotDrafted.innerHTML = "" + benchPlayers[i].spotDrafted;
                            newBenchPlayerRow.appendChild(newBenchPlayerDepthChartPosition);
                            newBenchPlayerRow.appendChild(newBenchPlayerPosition);
                            newBenchPlayerRow.appendChild(newBenchPlayerName);
                            newBenchPlayerRow.appendChild(newBenchPlayerPredictedScore);
                            newBenchPlayerRow.appendChild(newBenchPlayerAvgADP);
                            newBenchPlayerRow.appendChild(newBenchPlayerSpotDrafted);
                            teamHistoryTableBody.appendChild(newBenchPlayerRow);
                        }
                    }
                    return [2 /*return*/];
                });
            });
        }
        function createDataRowForStarter(stringPosition, starterIndex, playersPositon) {
            var newStarterRow = document.createElement("tr");
            newStarterRow.id = "starterRow" + stringPosition;
            newStarterRow.style.width = "100%";
            var newStarterDepthChartPosition = document.createElement("td");
            newStarterDepthChartPosition.id = "depthChartPositionStarter" + stringPosition;
            newStarterDepthChartPosition.innerHTML = "Starter";
            var newStarterPosition = document.createElement("td");
            newStarterPosition.id = "staterPosition" + stringPosition;
            newStarterPosition.innerHTML = stringPosition;
            newStarterRow.appendChild(newStarterDepthChartPosition);
            newStarterRow.appendChild(newStarterPosition);
            if (playersPositon.length > 0) {
                var startPlayer = playersPositon[starterIndex];
                var newStarterPlayerName = document.createElement("td");
                newStarterPlayerName.id = "starterPlayerName" + stringPosition;
                newStarterPlayerName.innerHTML = playersPositon[starterIndex].fullName;
                var newStarterPredictedScore = document.createElement("td");
                newStarterPredictedScore.id = "starterPredictedScore" + stringPosition;
                newStarterPredictedScore.innerHTML = "" + playersPositon[starterIndex].predictedScore;
                var newStarterAvgADP = document.createElement("td");
                newStarterAvgADP.id = "starterAvgADP" + stringPosition;
                newStarterAvgADP.innerHTML = "" + playersPositon[starterIndex].avgADP;
                var newStarterspotDrafted = document.createElement("td");
                newStarterspotDrafted.id = "starteSpotDrafted" + stringPosition;
                newStarterspotDrafted.innerHTML = "" + playersPositon[starterIndex].spotDrafted;
                newStarterRow.appendChild(newStarterPosition);
                newStarterRow.appendChild(newStarterPlayerName);
                newStarterRow.appendChild(newStarterPredictedScore);
                newStarterRow.appendChild(newStarterAvgADP);
                newStarterRow.appendChild(newStarterspotDrafted);
            }
            return newStarterRow;
        }
        var teamHistoryTableBody, res, data, postionOrder, intCurrPosition, currPosition, starterAmount, intCurrStarter, currPositionPlayers, currStarterIndex, currStarter, currStarterRow, benchPlayers, teamHistoryTable;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    return [4 /*yield*/, authenticateSession()];
                case 1:
                    if (!((_a.sent()) == true)) return [3 /*break*/, 6];
                    teamHistoryTableBody = document.getElementById("teamHistoryTableBody");
                    if (!(teamHistoryTableBody == null)) return [3 /*break*/, 2];
                    console.log("teamHistoryTableBody is null. Try again.");
                    return [2 /*return*/];
                case 2:
                    console.log("teamHistoryTableBody is not null. Continue.");
                    teamHistoryTableBody.innerHTML = "";
                    return [4 /*yield*/, fetch("http://localhost:80/api/teams/getDraftHistoryTeamReview/?username=".concat(getCookie("username"), "&draftID=").concat(getCookie("draftIDToView"), "&teamIndex=").concat(teamID), {
                            method: 'GET'
                        })];
                case 3:
                    res = _a.sent();
                    return [4 /*yield*/, res.json()];
                case 4:
                    data = _a.sent();
                    console.log(data);
                    postionOrder = ["QB", "RB", "WR", "TE", "Flex", "K", "DEF"];
                    for (intCurrPosition in postionOrder) {
                        console.log(intCurrPosition);
                        currPosition = postionOrder[intCurrPosition];
                        console.log(currPosition);
                        starterAmount = 1;
                        if (currPosition == "RB" ||
                            currPosition == "WR") {
                            starterAmount = 2;
                        }
                        for (intCurrStarter = 0; intCurrStarter < starterAmount; intCurrStarter++) {
                            currPositionPlayers = void 0;
                            if (currPosition == "Flex") {
                                currPositionPlayers = data["RB"].concat(data["WR"]).concat(data["TE"]);
                            }
                            else {
                                currPositionPlayers = data[currPosition];
                                console.log(currPositionPlayers);
                            }
                            console.log(currPositionPlayers);
                            currStarterIndex = findStarterIndex(currPositionPlayers);
                            console.log(currStarterIndex);
                            currStarter = currPositionPlayers[currStarterIndex];
                            console.log(currStarter);
                            currStarterRow = createDataRowForStarter(currPosition, currStarterIndex, currPositionPlayers);
                            console.log(currStarterRow);
                            teamHistoryTableBody.appendChild(currStarterRow);
                            if (currStarter != null) {
                                data[currStarter.position].splice(currStarterIndex, 1);
                                console.log(data[currPosition]);
                            }
                        }
                    }
                    benchPlayers = data["QB"].concat(data["RB"]).concat(data["WR"]).concat(data["TE"]).concat(data["K"]).concat(data["DEF"]);
                    console.log(benchPlayers);
                    findBenchedPlayers(benchPlayers);
                    teamHistoryTable = document.getElementById("teamHistoryTable");
                    if (teamHistoryTable == null) {
                        console.log("teamHistoryTable is null. Try again.");
                        return [2 /*return*/];
                    }
                    teamHistoryTable.style.display = "block";
                    _a.label = 5;
                case 5: return [3 /*break*/, 7];
                case 6:
                    console.log("User not logged in. Redirecting to login page.");
                    deleteAllCookies();
                    _a.label = 7;
                case 7: return [2 /*return*/];
            }
        });
    });
}
function resetDraftHistoryPage() {
    return __awaiter(this, void 0, void 0, function () {
        var draftHistoryPlayerLog, allTeamsTable, teamHistoryTable, draftReviewSelecterForm, backButton;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, authenticateSession()];
                case 1:
                    if ((_a.sent()) == true) {
                        draftHistoryPlayerLog = document.getElementById("draftHistoryPlayerLog");
                        allTeamsTable = document.getElementById("allTeamsTable");
                        teamHistoryTable = document.getElementById("teamHistoryTable");
                        draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
                        backButton = document.getElementById("backButton");
                        if (draftHistoryPlayerLog == null || allTeamsTable == null ||
                            teamHistoryTable == null || draftReviewSelecterForm == null || backButton == null) {
                            console.log("draftHistoryPlayerLog or draftReviewPlayerLogBody or allTeamsTable or allTeamsTableBody or teamHistoryTable or teamHistoryTableBody or " +
                                "draftReviewSelecterForm or backButton is null. Try again.");
                            return [2 /*return*/];
                        }
                        else {
                            draftHistoryPlayerLog.style.display = "none";
                            allTeamsTable.style.display = "none";
                            teamHistoryTable.style.display = "none";
                            draftReviewSelecterForm.style.display = "block";
                            backButton.style.display = "none";
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
