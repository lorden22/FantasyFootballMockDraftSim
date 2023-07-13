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
        parseDraftLogData(data);   
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

async function getNextUserPickRound() {
    var res = await fetch("http://localhost:8080/api/teams/getNextUserPickRound/?username="+getCookie("username"),{
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
    var nextUserPickRound = await getNextUserPickRound();
    console.log(currRound + "." + nextDraftPick + " - " + nextUserPick)


    if (nextDraftPick == nextUserPick && nextUserPickRound == currRound) {
        document.getElementById("waitingDraftControllerForm").style.display = "none";
        document.getElementById("draftControllerForm").style.display = "block";
    }
    else {
        document.getElementById("waitingDraftControllerForm").style.display = "block";
        document.getElementById("draftControllerForm").style.display = "none";
    }

    document.getElementById("currPick").innerHTML =  nextDraftPick
    document.getElementById("currRound").innerHTML = currRound

    if (currRound >= 15 && nextUserPickRound >= 16) {
        document.getElementById("nextPickInfo").innerHTML = "the draft will end at the end of this round. " +
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

async function endOfCurrentDraft() {
    var res = await fetch("http://localhost:8080/api/teams/deleteThisDraft/?username="+getCookie("username"),{
        method: 'POST',})
    var boolForCurrentDraft = await res.json();
    console.log(boolForCurrentDraft);    
    goToHomePage(); 
}