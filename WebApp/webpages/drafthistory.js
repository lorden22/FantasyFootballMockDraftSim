async function renderDraftHistoryTable() {
    
    function createRow (currDraftRowMetaData) {
        console.log(currDraftRowMetaData)
        let draftID = currDraftRowMetaData.draftID;
        console.log(draftID)

        let newDraftHistoryRow = document.createElement("tr");
        newDraftHistoryRow.id = "draftHistoryRow" + draftID;

        let newDraftHistoryRowDraftID = document.createElement("td");
        newDraftHistoryRowDraftID.id = "draftHistoryRowDraftID" + draftID;
        newDraftHistoryRowDraftID.innerHTML = draftID;

        let newDraftHistoryRowTeamName = document.createElement("td");
        newDraftHistoryRowTeamName.id = "draftHistoryRowTeamName" + draftID;
        newDraftHistoryRowTeamName.innerHTML = currDraftRowMetaData.teamName;

        let newDraftHistoryRowDraftPosition = document.createElement("td");
        newDraftHistoryRowDraftPosition.id = "draftHistoryRowDraftPosition" + draftID;
        newDraftHistoryRowDraftPosition.innerHTML = currDraftRowMetaData.draftPosition;

        let newDraftHistoryRowDraftSize = document.createElement("td");
        newDraftHistoryRowDraftSize.id = "draftHistoryRowDraftSize" + draftID;
        newDraftHistoryRowDraftSize.innerHTML = currDraftRowMetaData.draftSize;

        let newDraftHistoryRowDraftDate = document.createElement("td");
        newDraftHistoryRowDraftDate.id = "draftHistoryRowDraftDate" + draftID;
        newDraftHistoryRowDraftDate.innerHTML = currDraftRowMetaData.Date;

        let newDraftHistoryRowDraftTime = document.createElement("td");
        newDraftHistoryRowDraftTime.id = "draftHistoryRowDraftTime" + draftID;
        newDraftHistoryRowDraftTime.innerHTML = currDraftRowMetaData.Time;

        let newDraftHistoryRowDraftViewDraft = document.createElement("btn");
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
    if(await authenticateSession() == true) {

        loadUserName();

        let res = await fetch("http://localhost:8080/api/teams/getDraftHistoryMetaData/?username="+getCookie("username"),{
            method: 'GET',})
        let data = await res.json()
        console.log(data)

        for(let intCurrDraftMetaData in data) {
            let currDraftMetaData = data[intCurrDraftMetaData]

            createRow(currDraftMetaData);
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function viewDraft(draftID) {
    document.cookie = "draftIDToView="+draftID + ";path=/";
    window.location.href = "draftreview.html";
}

async function renderDraftReviewPage() {
    if(await authenticateSession() == true) {
        loadUserName();
        document.getElementById("backButton").style.display = "none";
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function renderDraftHistoryPlayerLog() {
    if(await authenticateSession() == true) {
        let res =  await fetch ("http://localhost:8080/api/teams/getDraftHistoryPlayerLog/?username="+getCookie("username")+"&draftID="+getCookie("draftIDToView"), {
            method: 'GET',
        })
        let data = await res.json()
        
        console.log(data);

        document.getElementById("draftHistoryPlayerLog").style.display = "block";
        document.getElementById("draftReviewSelecterForm").style.display = "none";

        for(let intCurrDraftLog in data.slice(0, data.length-1)) {
            let currPlayerDraftLog = data[intCurrDraftLog]
            
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

            document.getElementById("draftReviewPlayerLogBody").appendChild(newDraftHistoryPlayerLogRow);
        }
        document.getElementById("backButton").style.display = "inline";
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function renderDraftHistoryTeamHistorySelecter() {
    if(await authenticateSession() == true){

        let res = await fetch ("http://localhost:8080/api/teams/getDraftHistoryTeamList/?username="+getCookie("username")+"&draftID="+getCookie("draftIDToView"), {
            method: 'GET',
        })

        let data = await res.json()
        console.log(data);

        for(let intCurrTeam in data) {
            let currTeam = data[intCurrTeam];

            let currTeamRow = document.createElement("tr");
            currTeamRow.id = "draftHistoryTeamRow" + intCurrTeam;

            let currTeamName = document.createElement("td");
            currTeamName.id = "draftHistoryTeamName" + intCurrTeam;
            currTeamName.innerHTML = currTeam.teamName;

            let currTeamViewButton = document.createElement("btn");
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
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function viewTeam(teamID) {

    function findStarterIndex(players) {
        let starterIndex = 0;

        if(players.length > 1) {
            for(let intCurrPlayer in players) {
                let currPlayer = players[intCurrPlayer];

                if(currPlayer.predictedScore > players[starterIndex].predictedScore) {
                    starterIndex = intCurrPlayer;
                }
            }
        }
        return starterIndex
    }

    function findBenchedPlayers(players) {

        for(let intCurrPlayer in players) {
            let currPlayer = players[intCurrPlayer];

            let newBenchRow = document.createElement("tr");
            newBenchRow.id = "benchRow" + intCurrPlayer;

            let newBenchDepthChartPosition = document.createElement("td");
            newBenchDepthChartPosition.id = "depthChartPositionBench" + intCurrPlayer;
            newBenchDepthChartPosition.innerHTML = "Bench";

            let newBenchPosition = document.createElement("td");
            newBenchPosition.id =  "benchPosition" + intCurrPlayer
            newBenchPosition.innerHTML = currPlayer.position;

            let newBenchPlayerName = document.createElement("td");
            newBenchPlayerName.id = "benchPlayerName" + intCurrPlayer;
            newBenchPlayerName.innerHTML = currPlayer.fullName;

            let newBenchPredictedScore = document.createElement("td");
            newBenchPredictedScore.id = "benchPredictedScore" + intCurrPlayer;
            newBenchPredictedScore.innerHTML = currPlayer.predictedScore;

            let newBenchAvgADP = document.createElement("td");
            newBenchAvgADP.id = "benchAvgADP" + intCurrPlayer;
            newBenchAvgADP.innerHTML = currPlayer.avgADP;

            let newBenchSpotDrafted = document.createElement("td");
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
            let newStarterRow = document.createElement("tr");
            newStarterRow.id = "starterRow" + stringPosition;
            newStarterRow.style.width = "100%";

            let newStarterDepthChartPosition = document.createElement("td");
            newStarterDepthChartPosition.id = "depthChartPositionStarter" + stringPosition;
            newStarterDepthChartPosition.innerHTML = "Starter";

            let newStarterPosition = document.createElement("td");
            newStarterPosition.id =  "staterPosition" + stringPosition
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
            newStarterPredictedScore.innerHTML = playersPositon[starterIndex].predictedScore;

            let newStarterAvgADP = document.createElement("td");
            newStarterAvgADP.id = "starterAvgADP" + stringPosition;
            newStarterAvgADP.innerHTML = playersPositon[starterIndex].avgADP;

            let newStarterspotDrafted = document.createElement("td");
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

    if(await authenticateSession() == true) {

        document.getElementById("teamHistoryTableBody").innerHTML = "";

        let res = await fetch ("http://localhost:8080/api/teams/getDraftHistoryTeamReview/?username="+getCookie("username")+"&draftID="+getCookie("draftIDToView")+"&teamIndex="+teamID, {
            method: 'GET',
        })

        let data = await res.json()
        console.log(data);

        let postionOrder = ["QB", "RB", "WR", "TE", "Flex" ,"K", "DEF"];

        for(let intCurrPosition in postionOrder) {
            console.log(intCurrPosition);
            let currPosition = postionOrder[intCurrPosition];
            console.log(currPosition);
            let starterAmount = 1;

            if(currPosition == "RB" ||
                currPosition == "WR") {
                    starterAmount = 2;
            }

            for(let intCurrStarter = 0; intCurrStarter < starterAmount; intCurrStarter++) {
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
                document.getElementById("teamHistoryTableBody").appendChild(currStarterRow);

                if(currStarter != null) {
                    data[currStarter.position].splice(currStarterIndex, 1);
                    console.log(data[currPosition]);
                }
            }
        }
        let benchPlayers = data["QB"].concat(data["RB"]).concat(data["WR"]).concat(data["TE"]).concat(data["K"]).concat(data["DEF"]);
        console.log(benchPlayers);
        findBenchedPlayers(benchPlayers);
            
        document.getElementById("teamHistoryTable").style.display = "block";
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    } 
}

async function resetDraftHistoryPage() {
    if(await authenticateSession() == true) {
        document.getElementById("draftHistoryPlayerLog").style.display = "none";
        document.getElementById("draftReviewPlayerLogBody").innerHTML = "";


        document.getElementById("allTeamsTable").style.display = "none";
        document.getElementById("allTeamsTableBody").innerHTML = "";

        document.getElementById("teamHistoryTable").style.display = "none";
        document.getElementById("teamHistoryTableBody").innerHTML = "";

        document.getElementById("draftReviewSelecterForm").style.display = "block";
        document.getElementById("backButton").style.display = "none";
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}