"use strict";
type DraftRowData = {
    draft_id: string;
    team_name: string;
    draft_spot: string;
    num_teams: string;
    date: string;
    time: string;
}

async function renderDraftHistoryTable() {
    function createRow(currDraftRowMetaData: DraftRowData) {
        console.log(currDraftRowMetaData);
        let draftID = currDraftRowMetaData.draft_id;
        console.log(draftID);
        let newDraftHistoryRow = document.createElement("tr");
        newDraftHistoryRow.id = "draftHistoryRow" + draftID;
        let newDraftHistoryRowDraftID = document.createElement("td");
        newDraftHistoryRowDraftID.id = "draftHistoryRowDraftID" + draftID;
        newDraftHistoryRowDraftID.textContent = draftID;
        let newDraftHistoryRowTeamName = document.createElement("td");
        newDraftHistoryRowTeamName.id = "draftHistoryRowTeamName" + draftID;
        newDraftHistoryRowTeamName.textContent = currDraftRowMetaData.team_name;
        let newDraftHistoryRowDraftPosition = document.createElement("td");
        newDraftHistoryRowDraftPosition.id = "draftHistoryRowDraftPosition" + draftID;
        newDraftHistoryRowDraftPosition.textContent = currDraftRowMetaData.draft_spot;
        let newDraftHistoryRowDraftSize = document.createElement("td");
        newDraftHistoryRowDraftSize.id = "draftHistoryRowDraftSize" + draftID;
        newDraftHistoryRowDraftSize.textContent = currDraftRowMetaData.num_teams;
        let newDraftHistoryRowDraftDate = document.createElement("td");
        newDraftHistoryRowDraftDate.id = "draftHistoryRowDraftDate" + draftID;
        newDraftHistoryRowDraftDate.textContent = currDraftRowMetaData.date;
        let newDraftHistoryRowDraftTime = document.createElement("td");
        newDraftHistoryRowDraftTime.id = "draftHistoryRowDraftTime" + draftID;
        newDraftHistoryRowDraftTime.textContent = currDraftRowMetaData.time;
        let newDraftHistoryRowDraftViewDraft = document.createElement("button");
        newDraftHistoryRowDraftViewDraft.id = "draftHistoryRowDraftViewDraft" + draftID;
        newDraftHistoryRowDraftViewDraft.textContent = "View Draft";
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
        let res = await fetch("/api/teams/getDraftHistoryMetaData/?username=" + getCookie("username"), {
            method: 'GET',
        });
        let data = await res.json();
        console.log(data);
        for (let intCurrDraftMetaData in data) {
            let currDraftMetaData = data[intCurrDraftMetaData];
            currDraftMetaData
            createRow(currDraftMetaData);
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}

async function viewDraft(draftID: string) {
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
        let res = await fetch("/api/teams/getDraftHistoryPlayerLog/?username=" + getCookie("username") + "&draftID=" + getCookie("draftIDToView"), {
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
            newDraftHistoryPlayerLogRound.textContent = roundPickArray[0];
            let newDraftHistoryPlayerLogPick = document.createElement("td");
            newDraftHistoryPlayerLogPick.id = "draftHistoryPlayerLogPick" + intCurrDraftLog;
            newDraftHistoryPlayerLogPick.textContent = roundPickArray[1];
            let newDraftHistoryPlayerLogPlayerName = document.createElement("td");
            newDraftHistoryPlayerLogPlayerName.id = "draftHistoryPlayerLogPlayerName" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerName.textContent = currPlayerDraftLog.fullName;
            let newDraftHistoryPlayerLogPlayerPosition = document.createElement("td");
            newDraftHistoryPlayerLogPlayerPosition.id = "draftHistoryPlayerLogPlayerPosition" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerPosition.textContent = currPlayerDraftLog.position;
            let newDraftHistoryPlayerLogPlayerScore = document.createElement("td");
            newDraftHistoryPlayerLogPlayerScore.id = "draftHistoryPlayerLogPlayerScored" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerScore.textContent = currPlayerDraftLog.predictedScore;
            let newDraftHistoryPlayerLogPlayerTeam = document.createElement("td");
            newDraftHistoryPlayerLogPlayerTeam.id = "draftHistoryPlayerLogPlayerTeam" + intCurrDraftLog;
            newDraftHistoryPlayerLogPlayerTeam.textContent = currPlayerDraftLog.teamDraftedBy;
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
        let res = await fetch("/api/teams/getDraftHistoryTeamList/?username=" + getCookie("username") + "&draftID=" + getCookie("draftIDToView"), {
            method: 'GET',
        });
        let data = await res.json();
        console.log(data);
        for (let intCurrTeam in data) {
            let currTeam = data[intCurrTeam];
            let intCurrTeamNumber = Number(intCurrTeam);
            intCurrTeamNumber++;
            console.log(intCurrTeamNumber);
            let currTeamRow = document.createElement("tr");
            currTeamRow.style.width = "100%";
            currTeamRow.style.justifyContent = "space-between";
            currTeamRow.style.alignItems = "center";
            currTeamRow.id = "draftHistoryTeamRow" + intCurrTeamNumber;
            let currTeamName = document.createElement("td");
            currTeamName.id = "draftHistoryTeamName" + intCurrTeamNumber;
            let currTeamViewButtonTd = document.createElement("td");
            currTeamName.textContent = currTeam.teamName;
            let currTeamViewButton = document.createElement("btn");
            currTeamViewButton.id = "draftHistoryViewButton" + intCurrTeamNumber;
            currTeamViewButton.textContent = "View Team";
            currTeamViewButton.onclick = function () { viewTeam(intCurrTeamNumber.toString());};
            currTeamViewButton.className = "btn btn-primary";
            currTeamViewButton.style.margin = "5%";
            currTeamViewButton.style.padding = "5%";
            currTeamViewButtonTd.appendChild(currTeamViewButton);
            currTeamRow.appendChild(currTeamName);
            currTeamRow.appendChild(currTeamViewButtonTd);
            let allTeamsTableBody = document.getElementById("allTeamsTableBody");
            if (allTeamsTableBody == null) {
                console.log("allTeamsTableBody is null. Try again.");
                return;
            }
            allTeamsTableBody.appendChild(currTeamRow);
        }
        let teamReviewSection = document.getElementById("teamReviewSection");
        let draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
        let backButton = document.getElementById("backButton");
        if (teamReviewSection == null || draftReviewSelecterForm == null || backButton == null) {
            console.log("teamReviewSection or draftReviewSelecterForm or backButton is null. Try again.");
            return;
        }
        teamReviewSection.style.display = "flex";
        draftReviewSelecterForm.style.display = "none";
        backButton.style.display = "inline";
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}

async function viewTeam(teamID: string) {
    type Player = {
        firstName: string,
        lastName: string,
        fullName: string,
        position: string,
        predictedScore: number,
        avgADP: number,
        spotDrafted: number,
        teamDraftedBy: string,
    }

    // Helper function to format ADP by removing leading zeros after decimal
    function formatADP(adp: number): string {
        // For values like 4.01 -> 4.1, 4.02 -> 4.2, etc.
        // Remove leading zero after decimal point when followed by single digit
        const formatted = adp.toFixed(2);
        return formatted.replace(/\.0(\d)$/, '.$1');
    }

    function findStarterIndex(players: Player[]) {
        if (players == null || players.length == 0) {
            return -1;
        }
        let highestScore = players[0].predictedScore;
        let highestScoreIndex = 0;
        for (let i = 1; i < players.length; i++) {
            if (players[i].predictedScore > highestScore) {
            highestScore = players[i].predictedScore;
            highestScoreIndex = i;
            }
        }
        return highestScoreIndex;
        }

    async function findBenchedPlayers(benchPlayers: Player[]) {
        let teamHistoryTableBody = document.getElementById("teamHistoryTableBody");
        if (teamHistoryTableBody == null) {
            console.log("benchPlayerTableBody is null. Try again.");
            return;
        }
        else {
            for (let i = 0; i < benchPlayers.length; i++) {
                if(benchPlayers[i] == null){
                    continue;
                }
                let newBenchPlayerRow = document.createElement("tr");
                newBenchPlayerRow.id = "benchPlayerRow" + i;
                newBenchPlayerRow.style.width = "100%";
                newBenchPlayerRow.style.height = "7%";
                newBenchPlayerRow.style.justifyContent = "space-between";
                let newBenchPlayerDepthChartPosition = document.createElement("td");
                newBenchPlayerDepthChartPosition.id = "depthChartPositionBench" + i;
                newBenchPlayerDepthChartPosition.textContent = "Bench";
                let newBenchPlayerPosition = document.createElement("td");
                newBenchPlayerPosition.id = "benchPlayerPosition" + i;
                newBenchPlayerPosition.textContent = benchPlayers[i].position;
                let newBenchPlayerName = document.createElement("td");
                newBenchPlayerName.id = "benchPlayerName" + i;
                newBenchPlayerName.textContent = benchPlayers[i].fullName;
                let newBenchPlayerPredictedScore = document.createElement("td");
                newBenchPlayerPredictedScore.id = "benchPlayerPredictedScore" + i;
                newBenchPlayerPredictedScore.textContent = "" + benchPlayers[i].predictedScore;
                let newBenchPlayerAvgADP = document.createElement("td");
                newBenchPlayerAvgADP.id = "benchPlayerAvgADP" + i;
                newBenchPlayerAvgADP.textContent = formatADP(benchPlayers[i].avgADP);
                let newBenchPlayerSpotDrafted = document.createElement("td");
                newBenchPlayerSpotDrafted.id = "benchPlayerSpotDrafted" + i;
                newBenchPlayerSpotDrafted.textContent = "" + benchPlayers[i].spotDrafted;
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

    function createDataRowForStarter(stringPosition: string, starterIndex: number, playersPositon: Player[]) {
        let newStarterRow = document.createElement("tr");
        newStarterRow.id = "starterRow" + stringPosition;
        newStarterRow.style.width = "100%";
        newStarterRow.style.height = "7%";
        newStarterRow.style.justifyContent = "space-between";
        let newStarterDepthChartPosition = document.createElement("td");
        newStarterDepthChartPosition.id = "depthChartPositionStarter" + stringPosition;
        newStarterDepthChartPosition.textContent = "Starter";
        let newStarterPosition = document.createElement("td");
        newStarterPosition.id = "staterPosition" + stringPosition;
        newStarterPosition.textContent = stringPosition;
        newStarterRow.appendChild(newStarterDepthChartPosition);
        newStarterRow.appendChild(newStarterPosition);
        if (playersPositon != null && playersPositon.length > 0) {
            let startPlayer = playersPositon[starterIndex];
            let newStarterPlayerName = document.createElement("td");
            newStarterPlayerName.id = "starterPlayerName" + stringPosition;
            newStarterPlayerName.textContent = playersPositon[starterIndex].fullName;
            let newStarterPredictedScore = document.createElement("td");
            newStarterPredictedScore.id = "starterPredictedScore" + stringPosition;
            newStarterPredictedScore.textContent = "" + playersPositon[starterIndex].predictedScore;
            let newStarterAvgADP = document.createElement("td");
            newStarterAvgADP.id = "starterAvgADP" + stringPosition;
            newStarterAvgADP.textContent = formatADP(playersPositon[starterIndex].avgADP);
            let newStarterspotDrafted = document.createElement("td");
            newStarterspotDrafted.id = "starteSpotDrafted" + stringPosition;
            newStarterspotDrafted.textContent = "" + playersPositon[starterIndex].spotDrafted;
            newStarterRow.appendChild(newStarterPosition);
            newStarterRow.appendChild(newStarterPlayerName);
            newStarterRow.appendChild(newStarterPredictedScore);
            newStarterRow.appendChild(newStarterAvgADP);
            newStarterRow.appendChild(newStarterspotDrafted);
        }
        return newStarterRow;
    }

    if (await authenticateSession() == true) {
        let teamHistoryTableBody = document.getElementById("teamHistoryTableBody")
        if (teamHistoryTableBody == null) {
            console.log("teamHistoryTableBody is null. Try again.");
            return;
        }
        else {
            console.log("teamHistoryTableBody is not null. Continue.");
            teamHistoryTableBody.innerHTML = "";
            let res = await fetch(`/api/teams/getDraftHistoryTeamReview/?username=${getCookie("username")}&draftID=${getCookie("draftIDToView")}&teamIndex=${teamID}`, {
            method: 'GET',
            });
            let data = await res.json();
            console.log(data);
            let postionOrder = ["QB", "RB", "WR", "TE", "Flex", "K", "DST"];
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
                    let currStarter = null;
                    if(currStarterIndex != -1) {
                        currStarter = currPositionPlayers[currStarterIndex];
                        console.log(currStarter);
                    }
                    let currStarterRow = createDataRowForStarter(currPosition, currStarterIndex, currPositionPlayers);
                    console.log(currStarterRow);
                    teamHistoryTableBody.appendChild(currStarterRow);
                    if (currStarter != null) {
                        data[currStarter.position].splice(currStarterIndex, 1);
                        console.log(data[currPosition]);
                    }
                }
            }
            let benchPlayers = data["QB"].concat(data["RB"]).concat(data["WR"]).concat(data["TE"]).concat(data["K"]).concat(data["DST"]);
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
        let teamReviewSection = document.getElementById("teamReviewSection");
        let allTeamsTableBody = document.getElementById("allTeamsTableBody");
        let teamHistoryTableBody = document.getElementById("teamHistoryTableBody");
        let draftReviewSelecterForm = document.getElementById("draftReviewSelecterForm");
        let backButton = document.getElementById("backButton");
        if (draftHistoryPlayerLog == null || teamReviewSection == null ||
            draftReviewSelecterForm == null || backButton == null || 
            draftHistoryPlayerLogBody == null || allTeamsTableBody == null || teamHistoryTableBody == null) {
                console.log("One or more required elements are null. Try again.");
                return;
        }
        else {
            draftHistoryPlayerLog.style.display = "none";
            draftHistoryPlayerLogBody.innerHTML = "";
            teamReviewSection.style.display = "none";
            allTeamsTableBody.innerHTML = "";
            teamHistoryTableBody.innerHTML = "";
            draftReviewSelecterForm.style.display = "block";
            backButton.style.display = "none";
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}