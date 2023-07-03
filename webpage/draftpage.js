function goToHomePage() {
    deleteCookie("draftType");
    deleteCookie("draftPosition");
    deleteCookie("teamName");
    window.location.href = "drafthomepage.html";

}

async function checkForUserDraftHistory() {

    loadUserName();

    var res = await fetch("http://localhost:8080/api/teams/checkForCurrentDrafts/?username="+getCookie("username"),{
        method: 'GET',})
    var boolForCurrentDraft = await res.json()
    console.log("current draft - " + boolForCurrentDraft)

    var res = await fetch("http://localhost:8080/api/teams/checkForPastDrafts/?username="+getCookie("username"),{
        method: 'GET',})
    var boolPastDrafts = await res.json()
    console.log("past drafts - " + boolPastDrafts)

    if (boolForCurrentDraft == true) {
        document.getElementById("resumeDraftButton").style.display = "inline-block";
        document.getElementById("deleteDraftButton").style.display = "inline-block";
    }
    if (boolPastDrafts == true) {
        document.getElementById("historalDraftButton").style.display = "inline-block";
    }
}

function loadUserName() {
    console.log("UserName = " + getCookie("username"))
    /*if(getCookie("username") == "") {
        alert("You must be logged in to view this page.")
        window.location.href = "loginpage.html"
    }
    else {
        document.getElementById("userNameSpan").innerHTML = getCookie("username")
    }*/
}

async function selectedStartNewDraft() {
    var res = await fetch("http://localhost:8080/api/teams/checkForCurrentDrafts/?username="+getCookie("username"),{
        method: 'GET',})
    var boolForCurrentDraft = await res.json();
    console.log(boolForCurrentDraft)

    if (boolForCurrentDraft == true) {
        alert("You already have a draft in progress. Please resume that draft or delete it before starting a new one.")
    }
    else {
    document.getElementById("draftModeSelect").style.display = "none";
    document.getElementById("draftFormDiv").style.display = "block";
    }
}

function selectedResumeLastSavedDraft () { 
    document.cookie = "draftType=resume;path=/";
    window.location.href = "draftpage.html";
}

function selectedViewDraftHistory() {
    window.location.href = "draftHistory.html";
}

async function selectedDeleteCurrentDraft() {
    var res = await fetch("http://localhost:8080/api/teams/deleteThisDraft/?username="+getCookie("username"),{   
        method: 'POST',})
    var boolForCurrentDraft = await res.json();
    console.log(boolForCurrentDraft);
    alert("Draft deleted. You may now start a new draft.")
    document.getElementById("resumeDraftButton").style.display = "none";
    document.getElementById("deleteDraftButton").style.display = "none";
}

async function startDraft() {

    function checkStartDraftInput(teamName,draftSize,draftPosition) {
        if (typeof teamName != "string") {
            alert(teamName + " - Not a valid string value to name your team. Try again.")
            return false
        }
        if (typeof draftSize != "number" ||
            !Number.isInteger(draftSize)) {
                alert(draftSize + " - Not a valid int entered for the draft size. Try again.")
                return false
        }
        if (typeof draftPosition != "number" ||
            !Number.isInteger(draftPosition) ||
            draftPosition > draftSize) {
                alert(draftPosition + " - Not a valid int entered for your starting draft position. Try again.")
                return false
        }
        return true
    }

    var teamName = document.getElementById("teamNameInput").value
    var draftSize = document.getElementById("sizeOfTeamsInput").value
    draftSize = parseInt(draftSize)
    var draftPosition = document.getElementById("draftPositionInput").value
    draftPosition = parseInt(draftPosition)

    if (checkStartDraftInput(teamName,draftSize,draftPosition) == true) {
        var res = await 
        fetch(("http://localhost:8080/api/teams/startDraft/?username="+getCookie("username")+"&teamName="+teamName+"&draftSize="+draftSize+"&draftPosition="+draftPosition), {
            method: 'POST',
        })
        var data = await res.json()
        console.log(data)

        document.cookie = "teamName=" + teamName + "; path=/";
        document.cookie = "draftPosition=" + draftPosition + "; path=/";
    }
    document.cookie = "draftType=new; path=/";
    window.location.href = "draftpage.html"

}

async function endOfCurrentDraft() {
    var res = await fetch("http://localhost:8080/api/teams/deleteThisDraft/?username="+getCookie("username"),{
        method: 'POST',})
    var boolForCurrentDraft = await res.json();
    console.log(boolForCurrentDraft);    
    goToHomePage();
}

async function setUpDraft() {
    loadUserName();

    var draftType = getCookie("draftType");
    
    if (draftType == "resume") {

        var endOfDraft = await endDraft();

        if (endOfDraft == false) {

            document.getElementById("draftNotStartedForm").style.display = "none";
            document.getElementById("waitingDraftControllerForm").style.display = "block";

            var currPick = await getCurrPick();
            var nextUserPick =  await getNextUserPick();
            var currRound = await getCurrRound(); 

            console.log("currPick = " + currPick)
            console.log("nextUserPick = " + nextUserPick)
            
            if (currPick == nextUserPick) {
                document.getElementById("draftControllerForm").style.display = "block";
                document.getElementById("waitingDraftControllerForm").style.display = "none";
                document.getElementById("currUserPickRound").innerHTML = currRound;
                document.getElementById("currUserPickSpot").innerHTML = currPick;
                
            }
            else {
                document.getElementById("waitingDraftControllerForm").style.display = "block";
                document.getElementById("currRound").innerHTML = currRound;
                document.getElementById("nextUserPickRound").innerHTML = currRound + 1;


            }

            document.getElementById("currPick").innerHTML = currPick
            document.getElementById("nextUserPick").innerHTML = nextUserPick
        
        }
        else {
            document.getElementById("draftNotStartedForm").style.display = "none";
        }
        getPlayerLeft();
        var res = await fetch("http://localhost:8080/api/teams/getAllPlayersDrafted/?username="+getCookie("username"),{
            method: 'GET',})
        var data = await res.json();
        parseDraftLogData(data);s    
    }
    else if (draftType == "new") {
        console.log("Staring New draft");
    }
    else console.log("Error .... draftType = " + draftType);
}

async function startDraftFromDraftPage(){
    document.getElementById("draftNotStartedForm").style.display = "none";

    var draftPosition = getCookie("draftPosition")

    if (draftPosition == 1) {
        document.getElementById("draftControllerForm").style.display = "block";
        document.getElementById("currPick").innerHTML = await getCurrPick();
        document.getElementById("nextUserPick").innerHTML = await getNextUserPick();
    }
    else {
        document.getElementById("waitingDraftControllerForm").style.display = "block";
        document.getElementById("currPick").innerHTML =  await getCurrPick();   
        document.getElementById("nextUserPick").innerHTML = await getNextUserPick();

    }
    document.getElementById("draftLogPar").innerHTML = "Waiting for user input...";
    getPlayerLeft();
}

async function getCurrRound() {
    var res = await fetch("http://localhost:8080/api/teams/getCurrRound/?username=" + getCookie("username"),{
        method: 'GET',
    })
    var data = await res.json()
    console.log(data)
    return data;
}   

async function getCurrPick() {
    var res = await fetch("http://localhost:8080/api/teams/getCurrPick/?username=" + getCookie("username"),{
        method: 'GET',
    })
    var data = await res.json()
    console.log(data)
    return data;
}   

async function getNextUserPick() {
    var res = await fetch("http://localhost:8080/api/teams/getNextUserPick/?username="+getCookie("username"),{
        method: 'GET',
    })
    var data = await res.json()
    console.log(data)
    return data;
}   

async function getPlayerLeft() {
    var res = await fetch("http://localhost:8080/api/teams/getPlayersLeft/?username="+getCookie("username"),{
        method: 'GET',
    })
    var data = await res.json()
    console.log(data)
    var returnVal = ""
    for(var intCurrPlayer in data) {
        currPlayer = data[intCurrPlayer]
        returnVal += ((1+Number(intCurrPlayer)) + ". " + currPlayer.fullName + " " +currPlayer.position + " - Predicted Score 2022 = " 
        + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />")
    }
    console.log(returnVal)
    document.getElementById("playerListPar").innerHTML = returnVal
}

async function updatePlayerListPar(data) {
    returnVal = ""
    for(var intCurrPlayer in data) {
        currPlayer = data[intCurrPlayer]
        returnVal += ((1+Number(intCurrPlayer)) + ". " + currPlayer.fullName + ", " +currPlayer.position + " - Predicted Score 2022 = " 
        + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />")
    }
    document.getElementById("playerListPar").innerHTML = returnVal
}

async function parseDraftLogData(data) {
    var updatedDraftLog
    var currPick = await getCurrPick();
    var currRound = await getCurrRound()

    if (currPick == 1 && currRound == 1) {
        document.getElementById("draftLogPar").innerHTML = ""
        updateVal = ""
    }
    else updateVal = document.getElementById("draftLogPar").innerHTML;

    for(var intCurrPlayer in data) {
        currPlayer = data[intCurrPlayer]
        console.log(currPlayer)
        if (currPlayer.firstName == null) {
            await endDraft();
            break;
        }
        else {
            updateVal += currPlayer.spotDrafted + " - " + currPlayer.teamDraftedBy + " picked " + currPlayer.fullName + ", " +currPlayer.position + " - Predicted Score 2022 = " +
            currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />"
        }
    }
    document.getElementById("draftLogPar").innerHTML = updateVal
}

async function checkToClearDraftLog() {
    var currPick = await getCurrPick();
    var currRound = await getCurrRound();
    if (currPick == 1  && currRound == 1) {
        document.getElementById("draftLogPar").innerHTML = ""
    }
}

async function changeFormForNextPick() {
    var nextDraftPick = await getCurrPick();
    var currRound = await getCurrRound();
    var nextUserPick = await getNextUserPick();
    console.log(currRound + "." + nextDraftPick + " - " + nextUserPick)

    if (nextDraftPick == nextUserPick) {
        document.getElementById("waitingDraftControllerForm").style.display = "none";
        document.getElementById("draftControllerForm").style.display = "block";
    }
    else {
        document.getElementById("waitingDraftControllerForm").style.display = "block";
        document.getElementById("draftControllerForm").style.display = "none";
    }

    document.getElementById("currPick").innerHTML =  nextDraftPick
    document.getElementById("currRound").innerHTML = currRound

    if (currRound >= 15 && nextDraftPick > nextUserPick){
        document.getElementById("nextPickInfo").innerHTML = "The Draft will end at the end of this round. " +
            "Press the button below to end the draft."
    } 
    else {
        document.getElementById("nextUserPick").innerHTML = nextUserPick                
        document.getElementById("nextUserPickRound").innerHTML = currRound+1;
        document.getElementById("currUserPickRound").innerHTML = currRound
        document.getElementById("currUserPickSpot").innerHTML = nextUserPick;
    }
}

async function simulateToNextPick() {
    await checkToClearDraftLog();          
    var res = await
    fetch("http://localhost:8080/api/teams/simTo/?username="+getCookie("username"), {
            method: 'POST',
        })
    var data = await res.json()

    await getPlayerLeft();
    parseDraftLogData(data)

    if (await endDraft() == false) {
        await changeFormForNextPick();
    }
}

async function userDraftPlayer() {
    await checkToClearDraftLog();   
    var playerToDraftIndex = parseInt(document.getElementById("nextDraftPick").value)

    var res = await
    fetch("http://localhost:8080/api/teams/userDraftPlayer/?username="+getCookie("username")+"&playerIndex="+playerToDraftIndex, {
            method: 'POST',
        })
    var data = await res.json()
    console.log(data)

    await getPlayerLeft();
    parseDraftLogData(data)

    if (await endDraft() == false) {
        await changeFormForNextPick();
    }
}

async function endDraft() {
    if( await getCurrRound() >=16) {
        document.getElementById("draftControllerForm").style.display = "none";
        document.getElementById("waitingDraftControllerForm").style.display = "none";
        document.getElementById("draftCompleteForm").style.display = "block"
        return true;
    }
    else return false;
}

async function renderDraftHistoryTable() {
    var res = await fetch("http://localhost:8080/api/teams/getDraftHistoryMetaData/?username="+getCookie("username"),{
        method: 'GET',})
    var data = await res.json()
    console.log(data)
    for(var intCurrDraftMetaData in data) {
        var currDraftMetaDatadata = data[intCurrDraftMetaData]
        var draftID = currDraftMetaDatadata.draftID;

        var newDraftHisoryRow = document.createElement("tr");
        newDraftHisoryRow.id = "draftHistoryRow" + draftID;

        var newDraftHisoryRowDraftID = document.createElement("td");
        newDraftHisoryRowDraftID.id = "draftHistoryRowDraftID" + draftID;
        newDraftHisoryRowDraftID.innerHTML = draftID;

        var newDraftHisoryRowTeamName = document.createElement("td");
        newDraftHisoryRowTeamName.id = "draftHistoryRowTeamName" + draftID;
        newDraftHisoryRowTeamName.innerHTML = currDraftMetaDatadata.teamName;

        var newDraftHisoryRowDraftPosition = document.createElement("td");
        newDraftHisoryRowDraftPosition.id = "draftHistoryRowDraftPosition" + draftID;
        newDraftHisoryRowDraftPosition.innerHTML = currDraftMetaDatadata.draftPosition;

        var newDraftHisoryRowDraftSize = document.createElement("td");
        newDraftHisoryRowDraftSize.id = "draftHistoryRowDraftSize" + draftID;
        newDraftHisoryRowDraftSize.innerHTML = currDraftMetaDatadata.draftSize;

        var newDraftHisoryRowDraftDate = document.createElement("td");
        newDraftHisoryRowDraftDate.id = "draftHistoryRowDraftDate" + draftID;
        newDraftHisoryRowDraftDate.innerHTML = currDraftMetaDatadata.Date;

        var newDraftHisoryRowDraftTime = document.createElement("td");
        newDraftHisoryRowDraftTime.id = "draftHistoryRowDraftTime" + draftID;
        newDraftHisoryRowDraftTime.innerHTML = currDraftMetaDatadata.Time;

        var newDraftHisoryRowDraftViewDraft = document.createElement("btn");
        newDraftHisoryRowDraftViewDraft.id = "draftHistoryRowDraftViewDraft" + draftID;
        newDraftHisoryRowDraftViewDraft.innerHTML = "View Draft";
        newDraftHisoryRowDraftViewDraft.onclick = function() {viewDraft(draftID)};
        newDraftHisoryRowDraftViewDraft.className = "btn btn-primary";

        newDraftHisoryRow.appendChild(newDraftHisoryRowDraftID);
        newDraftHisoryRow.appendChild(newDraftHisoryRowTeamName);
        newDraftHisoryRow.appendChild(newDraftHisoryRowDraftPosition);
        newDraftHisoryRow.appendChild(newDraftHisoryRowDraftSize);
        newDraftHisoryRow.appendChild(newDraftHisoryRowDraftDate);
        newDraftHisoryRow.appendChild(newDraftHisoryRowDraftTime);
        newDraftHisoryRow.appendChild(newDraftHisoryRowDraftViewDraft);

        document.getElementById("draftHistoryTable").appendChild(newDraftHisoryRow);
    }
}

function viewDraft(draftID) {
    alert("Viewing Draft: " + draftID + " - is not yet implemented.")
}
