// Purpose: Javascript for the draft page
function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
        c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            console.log(c.substring(name.length, c.length))
            return c.substring(name.length, c.length)
        }
    }
    console.log("No cookie found")
    return "";
}
    
async function getCurrRound() {
    var res = await fetch("http://localhost:8080/api/teams/getCurrRound/?teamName=" + getCookie("teamName"),{
        method: 'GET',
    })
    var data = await res.json()
    console.log(data)
    return data;
}   

async function getCurrPick() {
    var res = await fetch("http://localhost:8080/api/teams/getCurrPick/?teamName=" + getCookie("teamName"),{
        method: 'GET',
    })
    var data = await res.json()
    console.log(data)
    return data;
}   

async function getNextUserPick() {
    var res = await fetch("http://localhost:8080/api/teams/getNextUserPick/?teamName="+getCookie("teamName"),{
        method: 'GET',
    })
    var data = await res.json()
    console.log(data)
    return data;
}   

async function getPlayerLeft() {
    var res = await fetch("http://localhost:8080/api/teams/getPlayersLeft/?teamName="+getCookie("teamName"),{
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

async function cheackToClearDraftLog() {
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
        document.getElementById("nextUserPickRoud").innerHTML = currRound+1;
        document.getElementById("currUserPickRound").innerHTML = currRound
        document.getElementById("currUserPickSpot").innerHTML = nextUserPick;
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
        fetch(("http://localhost:8080/api/teams/startDraft/?teamName="+teamName+"&draftSize="+draftSize+"&draftPosition="+draftPosition), {
            method: 'POST',
        })
        var data = await res.json()
        console.log(data)

        document.cookie = "teamName=" + teamName;

        document.getElementById("draftFormDiv").innerHTML = "";
        updatePlayerListPar(data)
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
        getPlayerLeft()

    }
}

async function simulateToNextPick() {
    await cheackToClearDraftLog();          
    var res = await
    fetch("http://localhost:8080/api/teams/simTo/?teamName="+getCookie("teamName"), {
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
    await cheackToClearDraftLog();   
    var playerToDraftIndex = parseInt(document.getElementById("nextDraftPick").value)

    var res = await
    fetch("http://localhost:8080/api/teams/userDraftPlayer/?teamName="+getCookie("teamName")+"&playerIndex="+playerToDraftIndex, {
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

function reloadPage() {
    deleteTeam();
    document.cookie = "teamName=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    location.reload();
}

function deleteTeam() {
    var teamName = getCookie("teamName")
    fetch("http://localhost:8080/api/teams/deleteTeam/?teamName="+teamName, {
            method: 'POST',
        })
    location.reload();
}