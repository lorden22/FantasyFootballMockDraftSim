"use strict";
async function renderDraftHistoryTable() {
    function createRow(currDraftRowMetaData) {
        console.log(currDraftRowMetaData);
        let draftID = currDraftRowMetaData.draft_id;
        console.log(draftID);
        let newDraftHistoryRow = document.createElement("tr");
        newDraftHistoryRow.id = "draftHistoryRow" + draftID;
        let newDraftHistoryRowDraftID = document.createElement("td");
        newDraftHistoryRowDraftID.id = "draftHistoryRowDraftID" + draftID;
        newDraftHistoryRowDraftID.innerHTML = draftID;
        let newDraftHistoryRowTeamName = document.createElement("td");
        newDraftHistoryRowTeamName.id = "draftHistoryRowTeamName" + draftID;
        newDraftHistoryRowTeamName.innerHTML = currDraftRowMetaData.team_name;
        let newDraftHistoryRowDraftPosition = document.createElement("td");
        newDraftHistoryRowDraftPosition.id = "draftHistoryRowDraftPosition" + draftID;
        newDraftHistoryRowDraftPosition.innerHTML = currDraftRowMetaData.draft_spot;
        let newDraftHistoryRowDraftSize = document.createElement("td");
        newDraftHistoryRowDraftSize.id = "draftHistoryRowDraftSize" + draftID;
        newDraftHistoryRowDraftSize.innerHTML = currDraftRowMetaData.num_teams;
        let newDraftHistoryRowDraftDate = document.createElement("td");
        newDraftHistoryRowDraftDate.id = "draftHistoryRowDraftDate" + draftID;
        newDraftHistoryRowDraftDate.innerHTML = currDraftRowMetaData.date;
        let newDraftHistoryRowDraftTime = document.createElement("td");
        newDraftHistoryRowDraftTime.id = "draftHistoryRowDraftTime" + draftID;
        newDraftHistoryRowDraftTime.innerHTML = currDraftRowMetaData.time;
        let newDraftHistoryRowDraftViewDraft = document.createElement("button");
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
        let draftHistoryTableBody = document.getElementById("draftHistoryTableBody");
        if (draftHistoryTableBody != null) {
            draftHistoryTableBody.appendChild(newDraftHistoryRow);
        }
    }
    if (await authenticateSession() == true) {
        loadUserName();
        let res = await fetch("http://localhost:80/api/teams/getDraftHistoryMetaData/?username=" + getCookie("username"), {
            method: 'GET',
        });
        let data = await res.json();
        console.log(data);
        for (let intCurrDraftMetaData in data) {
            let currDraftMetaData = data[intCurrDraftMetaData];
            currDraftMetaData;
            createRow(currDraftMetaData);
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function viewDraft(draftID) {
    document.cookie = "draftIDToView=" + draftID + ";path=/";
    window.location.href = "draftreview.html";
}
async function renderDraftReviewPage() {
    if (await authenticateSession() == true) {
        loadUserName();
        let backButton = document.getElementById("backButton");
        if (backButton == null) {
            console.log("backButton is null. Try again.");
            return;
        }
        backButton.style.display = "none";
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function renderDraftHistoryPlayerLog() {
    if (await authenticateSession() == true) {
        let res = await fetch("http://localhost:80/api/teams/getDraftHistoryPlayerLog/?username=" + getCookie("username") + "&draftID=" + getCookie("draftIDToView"), {
            method: 'GET',
        });
        let data = await res.json();
        console.log(data);
        let draftHistoryPlayerLog = document.getElementById("draftHistoryPlayerLog");
        let draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
        if (draftHistoryPlayerLog == null || draftReviewSelecterForm == null) {
            console.log("draftReviewPlayerLogBody or draftReviewSelecterForm is null. Try again.");
            return;
        }
        draftHistoryPlayerLog.style.display = "block";
        draftReviewSelecterForm.style.display = "none";
        for (let intCurrDraftLog in data.slice(0, data.length)) {
            let currPlayerDraftLog = data[intCurrDraftLog];
            let newDraftHistoryPlayerLogRow = document.createElement("tr");
            newDraftHistoryPlayerLogRow.id = "draftHistoryPlayerLogRow" + intCurrDraftLog;
            let newDraftHistoryPlayerLogSpot = currPlayerDraftLog.spotDrafted;
            let roundPickArray = newDraftHistoryPlayerLogSpot.split(".");
            let newDraftHistoryPlayerLogRound = document.createElement("td");
            newDraftHistoryPlayerLogRound.id = "draftHistoryPlayerLogRound" + intCurrDraftLog;
            newDraftHistoryPlayerLogRound.innerHTML = roundPickArray[0];
            let newDraftHistoryPlayerLogPick = document.createElement("td");
            newDraftHistoryPlayerLogPick.id = "draftHistoryPlayerLogPick" + intCurrDraftLog;
            newDraftHistoryPlayerLogPick.innerHTML = roundPickArray[1];
            let newDraftHistoryPlayerLogPlayerName = document.createElement("td");
            newDraftHistoryPlayerLogPlayerName.id = "draftHistoryPlayerLogPlayerName" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerName.innerHTML = currPlayerDraftLog.fullName;
            let newDraftHistoryPlayerLogPlayerPosition = document.createElement("td");
            newDraftHistoryPlayerLogPlayerPosition.id = "draftHistoryPlayerLogPlayerPosition" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerPosition.innerHTML = currPlayerDraftLog.position;
            let newDraftHistoryPlayerLogPlayerScore = document.createElement("td");
            newDraftHistoryPlayerLogPlayerScore.id = "draftHistoryPlayerLogPlayerScored" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerScore.innerHTML = currPlayerDraftLog.predictedScore;
            let newDraftHistoryPlayerLogPlayerTeam = document.createElement("td");
            newDraftHistoryPlayerLogPlayerTeam.id = "draftHistoryPlayerLogPlayerTeam" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerTeam.innerHTML = currPlayerDraftLog.teamDraftedBy;
            newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogRound);
            newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPick);
            newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerName);
            newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerPosition);
            newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerScore);
            newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerTeam);
            let draftReviewPlayerLogBody = document.getElementById("draftReviewPlayerLogBody");
            if (draftReviewPlayerLogBody == null) {
                console.log("draftReviewPlayerLogBody is null. Try again.");
                return;
            }
            else {
                draftReviewPlayerLogBody.appendChild(newDraftHistoryPlayerLogRow);
            }
        }
        let backButton = document.getElementById("backButton");
        if (backButton == null) {
            console.log("backButton is null. Try again.");
            return;
        }
        backButton.style.display = "inline";
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function renderDraftHistoryTeamHistorySelecter() {
    if (await authenticateSession() == true) {
        let res = await fetch("http://localhost:80/api/teams/getDraftHistoryTeamList/?username=" + getCookie("username") + "&draftID=" + getCookie("draftIDToView"), {
            method: 'GET',
        });
        let data = await res.json();
        console.log(data);
        for (let intCurrTeam in data) {
            let currTeam = data[intCurrTeam];
            let currTeamRow = document.createElement("tr");
            currTeamRow.id = "draftHistoryTeamRow" + intCurrTeam;
            let currTeamName = document.createElement("td");
            currTeamName.id = "draftHistoryTeamName" + intCurrTeam;
            currTeamName.innerHTML = currTeam.teamName;
            let currTeamViewButton = document.createElement("btn");
            currTeamViewButton.id = "draftHistoryViewButton" + intCurrTeam;
            currTeamViewButton.innerHTML = "View Team";
            currTeamViewButton.onclick = function () { viewTeam(currTeamViewButton.id.slice(-1)); };
            currTeamViewButton.className = "btn btn-primary";
            currTeamViewButton.style.margin = "5%";
            currTeamViewButton.style.padding = "5%";
            currTeamRow.appendChild(currTeamName);
            currTeamRow.appendChild(currTeamViewButton);
            let allTeamsTableBody = document.getElementById("allTeamsTableBody");
            if (allTeamsTableBody == null) {
                console.log("allTeamsTableBody is null. Try again.");
                return;
            }
            allTeamsTableBody.appendChild(currTeamRow);
        }
        let allTeamsTable = document.getElementById("allTeamsTable");
        let draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
        let backButton = document.getElementById("backButton");
        if (allTeamsTable == null || draftReviewSelecterForm == null || backButton == null) {
            console.log("allTeamsTable or draftReviewSelecterForm or backButton is null. Try again.");
            return;
        }
        allTeamsTable.style.display = "block";
        draftReviewSelecterForm.style.display = "none";
        backButton.style.display = "inline";
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function viewTeam(teamID) {
    function findStarterIndex(players) {
        let highestScore = -1;
        let highestScoreIndex = -1;
        for (let i = 0; i < players.length; i++) {
            if (players[i].predictedScore > highestScore) {
                highestScore = players[i].predictedScore;
                highestScoreIndex = i;
            }
        }
        return highestScoreIndex;
    }
    async function findBenchedPlayers(benchPlayers) {
        let teamHistoryTableBody = document.getElementById("teamHistoryTableBody");
        if (teamHistoryTableBody == null) {
            console.log("benchPlayerTableBody is null. Try again.");
            return;
        }
        else {
            for (let i = 0; i < benchPlayers.length; i++) {
                let newBenchPlayerRow = document.createElement("tr");
                newBenchPlayerRow.id = "benchPlayerRow" + i;
                newBenchPlayerRow.style.width = "100%";
                let newBenchPlayerDepthChartPosition = document.createElement("td");
                newBenchPlayerDepthChartPosition.id = "depthChartPositionBench" + i;
                newBenchPlayerDepthChartPosition.innerHTML = "Bench";
                let newBenchPlayerPosition = document.createElement("td");
                newBenchPlayerPosition.id = "benchPlayerPosition" + i;
                newBenchPlayerPosition.innerHTML = benchPlayers[i].position;
                let newBenchPlayerName = document.createElement("td");
                newBenchPlayerName.id = "benchPlayerName" + i;
                newBenchPlayerName.innerHTML = benchPlayers[i].fullName;
                let newBenchPlayerPredictedScore = document.createElement("td");
                newBenchPlayerPredictedScore.id = "benchPlayerPredictedScore" + i;
                newBenchPlayerPredictedScore.innerHTML = "" + benchPlayers[i].predictedScore;
                let newBenchPlayerAvgADP = document.createElement("td");
                newBenchPlayerAvgADP.id = "benchPlayerAvgADP" + i;
                newBenchPlayerAvgADP.innerHTML = "" + benchPlayers[i].avgADP;
                let newBenchPlayerSpotDrafted = document.createElement("td");
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
    }
    function createDataRowForStarter(stringPosition, starterIndex, playersPositon) {
        let newStarterRow = document.createElement("tr");
        newStarterRow.id = "starterRow" + stringPosition;
        newStarterRow.style.width = "100%";
        let newStarterDepthChartPosition = document.createElement("td");
        newStarterDepthChartPosition.id = "depthChartPositionStarter" + stringPosition;
        newStarterDepthChartPosition.innerHTML = "Starter";
        let newStarterPosition = document.createElement("td");
        newStarterPosition.id = "staterPosition" + stringPosition;
        newStarterPosition.innerHTML = stringPosition;
        newStarterRow.appendChild(newStarterDepthChartPosition);
        newStarterRow.appendChild(newStarterPosition);
        if (playersPositon.length > 0) {
            let startPlayer = playersPositon[starterIndex];
            let newStarterPlayerName = document.createElement("td");
            newStarterPlayerName.id = "starterPlayerName" + stringPosition;
            newStarterPlayerName.innerHTML = playersPositon[starterIndex].fullName;
            let newStarterPredictedScore = document.createElement("td");
            newStarterPredictedScore.id = "starterPredictedScore" + stringPosition;
            newStarterPredictedScore.innerHTML = "" + playersPositon[starterIndex].predictedScore;
            let newStarterAvgADP = document.createElement("td");
            newStarterAvgADP.id = "starterAvgADP" + stringPosition;
            newStarterAvgADP.innerHTML = "" + playersPositon[starterIndex].avgADP;
            let newStarterspotDrafted = document.createElement("td");
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
    if (await authenticateSession() == true) {
        let teamHistoryTableBody = document.getElementById("teamHistoryTableBody");
        if (teamHistoryTableBody == null) {
            console.log("teamHistoryTableBody is null. Try again.");
            return;
        }
        else {
            console.log("teamHistoryTableBody is not null. Continue.");
            teamHistoryTableBody.innerHTML = "";
            let res = await fetch(`http://localhost:80/api/teams/getDraftHistoryTeamReview/?username=${getCookie("username")}&draftID=${getCookie("draftIDToView")}&teamIndex=${teamID}`, {
                method: 'GET',
            });
            let data = await res.json();
            console.log(data);
            let postionOrder = ["QB", "RB", "WR", "TE", "Flex", "K", "DEF"];
            for (let intCurrPosition in postionOrder) {
                console.log(intCurrPosition);
                let currPosition = postionOrder[intCurrPosition];
                console.log(currPosition);
                let starterAmount = 1;
                if (currPosition == "RB" ||
                    currPosition == "WR") {
                    starterAmount = 2;
                }
                for (let intCurrStarter = 0; intCurrStarter < starterAmount; intCurrStarter++) {
                    let currPositionPlayers;
                    if (currPosition == "Flex") {
                        currPositionPlayers = data["RB"].concat(data["WR"]).concat(data["TE"]);
                    }
                    else {
                        currPositionPlayers = data[currPosition];
                        console.log(currPositionPlayers);
                    }
                    console.log(currPositionPlayers);
                    let currStarterIndex = findStarterIndex(currPositionPlayers);
                    console.log(currStarterIndex);
                    let currStarter = currPositionPlayers[currStarterIndex];
                    console.log(currStarter);
                    let currStarterRow = createDataRowForStarter(currPosition, currStarterIndex, currPositionPlayers);
                    console.log(currStarterRow);
                    teamHistoryTableBody.appendChild(currStarterRow);
                    if (currStarter != null) {
                        data[currStarter.position].splice(currStarterIndex, 1);
                        console.log(data[currPosition]);
                    }
                }
            }
            let benchPlayers = data["QB"].concat(data["RB"]).concat(data["WR"]).concat(data["TE"]).concat(data["K"]).concat(data["DEF"]);
            console.log(benchPlayers);
            findBenchedPlayers(benchPlayers);
            let teamHistoryTable = document.getElementById("teamHistoryTable");
            if (teamHistoryTable == null) {
                console.log("teamHistoryTable is null. Try again.");
                return;
            }
            teamHistoryTable.style.display = "block";
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function resetDraftHistoryPage() {
    if (await authenticateSession() == true) {
        let draftHistoryPlayerLog = document.getElementById("draftHistoryPlayerLog");
        let draftHistoryPlayerLogBody = document.getElementById("draftReviewPlayerLogBody");
        let allTeamsTable = document.getElementById("allTeamsTable");
        let teamHistoryTable = document.getElementById("teamHistoryTable");
        let draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
        let backButton = document.getElementById("backButton");
        if (draftHistoryPlayerLog == null || allTeamsTable == null ||
            teamHistoryTable == null || draftReviewSelecterForm == null || backButton == null || draftHistoryPlayerLogBody == null) {
            console.log("draftHistoryPlayerLog or draftReviewPlayerLogBody or allTeamsTable or allTeamsTableBody or teamHistoryTable or teamHistoryTableBody or " +
                "draftReviewSelecterForm or backButton is null. Try again.");
            return;
        }
        else {
            draftHistoryPlayerLog.style.display = "none";
            draftHistoryPlayerLogBody.innerHTML = "";
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
}
