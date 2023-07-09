async function renderDraftHistoryTable() {
    
    function createRow (currDraftRowMetaData) {
        console.log(currDraftMetaData)
        var draftID = currDraftMetaData.draftID;
        console.log(draftID)

        var newDraftHistoryRow = document.createElement("tr");
        newDraftHistoryRow.id = "draftHistoryRow" + draftID;

        var newDraftHistoryRowDraftID = document.createElement("td");
        newDraftHistoryRowDraftID.id = "draftHistoryRowDraftID" + draftID;
        newDraftHistoryRowDraftID.innerHTML = draftID;

        var newDraftHistoryRowTeamName = document.createElement("td");
        newDraftHistoryRowTeamName.id = "draftHistoryRowTeamName" + draftID;
        newDraftHistoryRowTeamName.innerHTML = currDraftMetaData.teamName;

        var newDraftHistoryRowDraftPosition = document.createElement("td");
        newDraftHistoryRowDraftPosition.id = "draftHistoryRowDraftPosition" + draftID;
        newDraftHistoryRowDraftPosition.innerHTML = currDraftMetaData.draftPosition;

        var newDraftHistoryRowDraftSize = document.createElement("td");
        newDraftHistoryRowDraftSize.id = "draftHistoryRowDraftSize" + draftID;
        newDraftHistoryRowDraftSize.innerHTML = currDraftMetaData.draftSize;

        var newDraftHistoryRowDraftDate = document.createElement("td");
        newDraftHistoryRowDraftDate.id = "draftHistoryRowDraftDate" + draftID;
        newDraftHistoryRowDraftDate.innerHTML = currDraftMetaData.Date;

        var newDraftHistoryRowDraftTime = document.createElement("td");
        newDraftHistoryRowDraftTime.id = "draftHistoryRowDraftTime" + draftID;
        newDraftHistoryRowDraftTime.innerHTML = currDraftMetaData.Time;

        var newDraftHistoryRowDraftViewDraft = document.createElement("btn");
        newDraftHistoryRowDraftViewDraft.id = "draftHistoryRowDraftViewDraft" + draftID;
        newDraftHistoryRowDraftViewDraft.innerHTML = "View Draft";
        newDraftHistoryRowDraftViewDraft.onclick = function() {viewDraft(draftID)};
        newDraftHistoryRowDraftViewDraft.className = "btn btn-primary";

        newDraftHistoryRow.appendChild(newDraftHistoryRowDraftID);
        newDraftHistoryRow.appendChild(newDraftHistoryRowTeamName);
        newDraftHistoryRow.appendChild(newDraftHistoryRowDraftPosition);
        newDraftHistoryRow.appendChild(newDraftHistoryRowDraftSize);
        newDraftHistoryRow.appendChild(newDraftHistoryRowDraftDate);
        newDraftHistoryRow.appendChild(newDraftHistoryRowDraftTime);
        newDraftHistoryRow.appendChild(newDraftHistoryRowDraftViewDraft);

        document.getElementById("draftHistoryTable").appendChild(newDraftHistoryRow);
    }

    loadUserName();

    var res = await fetch("http://localhost:8080/api/teams/getDraftHistoryMetaData/?username="+getCookie("username"),{
        method: 'GET',})
    var data = await res.json()
    console.log(data)

    for(var intCurrDraftMetaData in data) {
        var currDraftMetaData = data[intCurrDraftMetaData]

        createRow(currDraftMetaData);
    }
}

function viewDraft(draftID) {
    console.log(draftID)
    document.cookie = "draftIDToView="+draftID + ";path=/";
    window.location.href = "draftreview.html";
}

async function renderDraftReviewPage() {

    loadUserName();
    document.getElementById("backButton").style.display = "none";
}

async function renderDraftHistoryPlayerLog() {
    var res =  await fetch ("http://localhost:8080/api/teams/getDraftHistoryPlayerLog/?username="+getCookie("username")+"&draftID="+getCookie("draftIDToView"), {
        method: 'GET',
    })
    var data = await res.json()
    
    console.log(data);

    document.getElementById("draftHistoryPlayerLog").style.display = "block";
    document.getElementById("draftReviewSelecterForm").style.display = "none";

    for(var intCurrDraftLog in data.slice(0, data.length-1)) {
        var currPlayerDraftLog = data[intCurrDraftLog]
        
        var newDraftHistoryPlayerLogRow = document.createElement("tr");
        newDraftHistoryPlayerLogRow.id = "draftHistoryPlayerLogRow" + intCurrDraftLog;

        var newDraftHistoryPlayerLogSpot = currPlayerDraftLog.spotDrafted;
        var roundPickArray = newDraftHistoryPlayerLogSpot.split(".");

        var newDraftHistoryPlayerLogRound = document.createElement("td");
        newDraftHistoryPlayerLogRound.id = "draftHistoryPlayerLogRound" + intCurrDraftLog;
        newDraftHistoryPlayerLogRound.innerHTML = roundPickArray[0];

        var newDraftHistoryPlayerLogPick = document.createElement("td");
        newDraftHistoryPlayerLogPick.id = "draftHistoryPlayerLogPick" + intCurrDraftLog;
        newDraftHistoryPlayerLogPick.innerHTML = roundPickArray[1];

        var newDraftHistoryPlayerLogPlayerName = document.createElement("td");
        newDraftHistoryPlayerLogPlayerName.id = "draftHistoryPlayerLogPlayerName" + intCurrDraftLog;
        newDraftHistoryPlayerLogPlayerName.innerHTML = currPlayerDraftLog.fullName;

        var newDraftHistoryPlayerLogPlayerPosition = document.createElement("td");
        newDraftHistoryPlayerLogPlayerPosition.id = "draftHistoryPlayerLogPlayerPosition" + intCurrDraftLog;
        newDraftHistoryPlayerLogPlayerPosition.innerHTML = currPlayerDraftLog.position;

        var newDraftHistoryPlayerLogPlayerScore = document.createElement("td");
        newDraftHistoryPlayerLogPlayerScore.id = "draftHistoryPlayerLogPlayerScored" + intCurrDraftLog;
        newDraftHistoryPlayerLogPlayerScore.innerHTML = currPlayerDraftLog.predictedScore;

        var newDraftHistoryPlayerLogPlayerTeam = document.createElement("td");
        newDraftHistoryPlayerLogPlayerTeam.id = "draftHistoryPlayerLogPlayerTeam" + intCurrDraftLog;
        newDraftHistoryPlayerLogPlayerTeam.innerHTML = currPlayerDraftLog.teamDraftedBy;

        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogRound);
        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPick);
        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerName);
        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerPosition);
        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerScore);
        newDraftHistoryPlayerLogRow.appendChild(newDraftHistoryPlayerLogPlayerTeam);

        document.getElementById("draftReviewPlayerLogBody").appendChild(newDraftHistoryPlayerLogRow);
    }
    document.getElementById("backButton").style.display = "inline";

}

async function renderDraftHistoryTeamHistorySelecter() {

    var res = await fetch ("http://localhost:8080/api/teams/getDraftHistoryTeamList/?username="+getCookie("username")+"&draftID="+getCookie("draftIDToView"), {
        method: 'GET',
    })

    var data = await res.json()
    console.log(data);

    for(var intCurrTeam in data) {
        var currTeam = data[intCurrTeam];

        var currTeamRow = document.createElement("tr");
        currTeamRow.id = "draftHistoryTeamRow" + intCurrTeam;

        var currTeamName = document.createElement("td");
        currTeamName.id = "draftHistoryTeamName" + intCurrTeam;
        currTeamName.innerHTML = currTeam.teamName;

        var currTeamViewButton = document.createElement("btn");
        currTeamViewButton.id = "draftHistoryViewButton" + intCurrTeam;
        currTeamViewButton.innerHTML = "View Team";
        currTeamViewButton.onclick = function() {viewTeam(this.id.slice(-1))};
        currTeamViewButton.className = "btn btn-primary";
        currTeamViewButton.style.margin = "5%";
        currTeamViewButton.style.padding = "5%";

        currTeamRow.appendChild(currTeamName);
        currTeamRow.appendChild(currTeamViewButton);

        document.getElementById("allTeamsTableBody").appendChild(currTeamRow);
    }
    document.getElementById("allTeamsTable").style.display = "block";
    document.getElementById("draftReviewSelecterForm").style.display = "none";
    document.getElementById("backButton").style.display = "inline";
}

async function viewTeam(teamID) {

    function findStarterIndex(players) {
        var starterIndex = 0;

        if(players.length > 1) {
            for(var intCurrPlayer in players) {
                var currPlayer = players[intCurrPlayer];

                if(currPlayer.predictedScore > players[starterIndex].predictedScore) {
                    starterIndex = intCurrPlayer;
                }
            }
        }
        return starterIndex
    }

    function findBenchedPlayers(players) {

        for(var intCurrPlayer in players) {
            var currPlayer = players[intCurrPlayer];

            var newBenchRow = document.createElement("tr");
            newBenchRow.id = "benchRow" + intCurrPlayer;

            var newBenchDepthChartPosition = document.createElement("td");
            newBenchDepthChartPosition.id = "depthChartPositionBench" + intCurrPlayer;
            newBenchDepthChartPosition.innerHTML = "Bench";

            var newBenchPosition = document.createElement("td");
            newBenchPosition.id =  "benchPosition" + intCurrPlayer
            newBenchPosition.innerHTML = currPlayer.position;

            var newBenchPlayerName = document.createElement("td");
            newBenchPlayerName.id = "benchPlayerName" + intCurrPlayer;
            newBenchPlayerName.innerHTML = currPlayer.fullName;

            var newBenchPredictedScore = document.createElement("td");
            newBenchPredictedScore.id = "benchPredictedScore" + intCurrPlayer;
            newBenchPredictedScore.innerHTML = currPlayer.predictedScore;

            var newBenchAvgADP = document.createElement("td");
            newBenchAvgADP.id = "benchAvgADP" + intCurrPlayer;
            newBenchAvgADP.innerHTML = currPlayer.avgADP;

            var newBenchSpotDrafted = document.createElement("td");
            newBenchSpotDrafted.id = "benchSpotDrafted" + intCurrPlayer;
            newBenchSpotDrafted.innerHTML = currPlayer.spotDrafted;

            newBenchRow.appendChild(newBenchDepthChartPosition);
            newBenchRow.appendChild(newBenchPosition);
            newBenchRow.appendChild(newBenchPlayerName);
            newBenchRow.appendChild(newBenchPredictedScore);
            newBenchRow.appendChild(newBenchAvgADP);
            newBenchRow.appendChild(newBenchSpotDrafted);

            document.getElementById("teamHistoryTableBody").appendChild(newBenchRow);
        }
     }

    function createDataRowForStarter(stringPosition, starterIndex, playersPositon) {
            var newStarterRow = document.createElement("tr");
            newStarterRow.id = "starterRow" + stringPosition;

            var newStarterDepthChartPosition = document.createElement("td");
            newStarterDepthChartPosition.id = "depthChartPositionStarter" + stringPosition;
            newStarterDepthChartPosition.innerHTML = "Starter";

            var newStarterPosition = document.createElement("td");
            newStarterPosition.id =  "staterPosition" + stringPosition
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
            newStarterPredictedScore.innerHTML = playersPositon[starterIndex].predictedScore;

            var newStarterAvgADP = document.createElement("td");
            newStarterAvgADP.id = "starterAvgADP" + stringPosition;
            newStarterAvgADP.innerHTML = playersPositon[starterIndex].avgADP;

            var newStarterspotDrafted = document.createElement("td");
            newStarterspotDrafted.id = "starteSpotDrafted" + stringPosition;
            newStarterspotDrafted.innerHTML = playersPositon[starterIndex].spotDrafted;

            newStarterRow.appendChild(newStarterPosition);
            newStarterRow.appendChild(newStarterPlayerName);
            newStarterRow.appendChild(newStarterPredictedScore);
            newStarterRow.appendChild(newStarterAvgADP);
            newStarterRow.appendChild(newStarterspotDrafted);
        }
        return  newStarterRow;  
    }

    document.getElementById("teamHistoryTableBody").innerHTML = "";

    var res = await fetch ("http://localhost:8080/api/teams/getDraftHistoryTeamReview/?username="+getCookie("username")+"&draftID="+getCookie("draftIDToView")+"&teamIndex="+teamID, {
        method: 'GET',
    })

    var data = await res.json()
    console.log(data);

    var postionOrder = ["QB", "RB", "WR", "TE", "Flex" ,"K", "DEF"];

    for(var intCurrPosition in postionOrder) {
        console.log(intCurrPosition);
        var currPosition = postionOrder[intCurrPosition];
        console.log(currPosition);
        var starterAmount = 1;

        if(currPosition == "RB" ||
            currPosition == "WR") {
                starterAmount = 2;
        }

        for(var intCurrStarter = 0; intCurrStarter < starterAmount; intCurrStarter++) {
            if (currPosition == "Flex") {
                var currPositionPlayers = data["RB"].concat(data["WR"]).concat(data["TE"]);
            }
            else { 
                var currPositionPlayers = data[currPosition];
            }
            console.log(currPositionPlayers);
            var currStarterIndex = findStarterIndex(currPositionPlayers);
            console.log(currStarterIndex);
            var currStarter = currPositionPlayers[currStarterIndex];
            console.log(currStarter);
            var currStarterRow = createDataRowForStarter(currPosition, currStarterIndex, currPositionPlayers);
            console.log(currStarterRow);
            document.getElementById("teamHistoryTableBody").appendChild(currStarterRow);

            if(currStarter != null) {
                data[currStarter.position].splice(currStarterIndex, 1);
                console.log(data[currPosition]);
            }
        }
    }
    var benchPlayers = data["QB"].concat(data["RB"]).concat(data["WR"]).concat(data["TE"]).concat(data["K"]).concat(data["DEF"]);
    console.log(benchPlayers);
    findBenchedPlayers(benchPlayers);
        
    document.getElementById("teamHistoryTable").style.display = "block";
}

function resetDraftHistoryPage() {
    document.getElementById("draftHistoryPlayerLog").style.display = "none";
    document.getElementById("draftReviewPlayerLogBody").innerHTML = "";


    document.getElementById("allTeamsTable").style.display = "none";
    document.getElementById("allTeamsTableBody").innerHTML = "";

    document.getElementById("teamHistoryTable").style.display = "none";
    document.getElementById("teamHistoryTableBody").innerHTML = "";

    document.getElementById("draftReviewSelecterForm").style.display = "block";
    document.getElementById("backButton").style.display = "none";
}