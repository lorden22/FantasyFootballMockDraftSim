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

async function setUpDraft(): Promise < void > {
    if(await authenticateSession() == true) {
        loadUserName();

        let draftType = getCookie("draftType");
        
        if (draftType == "resume") {

            let endOfDraft = await endDraft();

            if (endOfDraft == false) {

                let draftNotStartedForm: HTMLDivElement | null = document.getElementById("draftNotStartedForm") as HTMLDivElement | null;
                let draftControllerForm: HTMLDivElement | null = document.getElementById("draftControllerForm") as HTMLDivElement | null;

                if (draftNotStartedForm == null || draftControllerForm == null) {
                    console.log("draftNotStartedForm or draftControllerForm is null. Try again.")
                    return;
                }
                else {
                    draftNotStartedForm.style.display = "none";
                    draftControllerForm.style.display = "block";
                }
                let currPick = await getCurrPick();
                let nextUserPick =  await getNextUserPick();
                let currRound = await getCurrRound(); 

                console.log("currPick = " + currPick)
                console.log("nextUserPick = " + nextUserPick)

                let waitingDraftControllerForm: HTMLDivElement | null = document.getElementById("waitingDraftControllerForm") as HTMLDivElement | null;
                let currUserPickRound: HTMLSpanElement | null = document.getElementById("currUserPickRound") as HTMLSpanElement | null;
                let currUserPickSpot: HTMLSpanElement | null = document.getElementById("currUserPickSpot") as HTMLSpanElement | null;    
                if (draftControllerForm == null || waitingDraftControllerForm ==  null || currUserPickRound == null || currUserPickSpot == null) {
                    console.log("draftControllerForm or waitingDraftControllerForm is null. Try again.")
                    return;
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
                        
                        let currRoundSpan = document.getElementById("currRound") as HTMLSpanElement | null;
                        let nextUserPickRoundSpan = document.getElementById("nextUserPickRound") as HTMLSpanElement | null;

                        if (currRoundSpan == null || nextUserPickRoundSpan == null) {
                            console.log("currRoundSpan or nextUserPickRoundSpan is null. Try again.")
                            return;
                        }
                        else {
                            currRoundSpan.innerHTML = String(currRound);
                            nextUserPickRoundSpan.innerHTML = String(currRound+1);
                        }
                    }

                }

                let currPickSpan = document.getElementById("currPick") as HTMLSpanElement | null;
                let nextUserPickSpan = document.getElementById("nextUserPick") as HTMLSpanElement | null;

                if (currPickSpan == null || nextUserPickSpan == null) {
                    console.log("currPickSpan or nextUserPickSpan is null. Try again.")
                    return;
                }
                else {
                    currPickSpan.innerHTML = String(currPick);
                    nextUserPickSpan.innerHTML = String(nextUserPick);
                }
            }
            else {
                let draftNotStartedForm: HTMLDivElement | null = document.getElementById("draftNotStartedForm") as HTMLDivElement | null;
                if (draftNotStartedForm == null) {
                    console.log("draftNotStartedForm is null. Try again.")
                    return;
                }
                else {
                    draftNotStartedForm.style.display = "none";
                }
            }
            getPlayerLeft();
            let res = await fetch("http://localhost:8080/api/teams/getAllPlayersDrafted/?username="+getCookie("username"),{
                method: 'GET',})
            let data: Player[] = await res.json();
            parseDraftLogData(data);   
        }
        else if (draftType == "new") {
            console.log("Staring New draft");
        }
        else console.log("Error .... draftType = " + draftType);
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function startDraftFromDraftPage(){
    if(await authenticateSession() == true) {

        let draftNotStartedForm: HTMLDivElement | null = document.getElementById("draftNotStartedForm") as HTMLDivElement | null;
        let draftControllerForm: HTMLDivElement | null = document.getElementById("draftControllerForm") as HTMLDivElement | null;
        let waitingDraftControllerForm: HTMLDivElement | null = document.getElementById("waitingDraftControllerForm") as HTMLDivElement | null;
        let currPickSpan: HTMLSpanElement | null = document.getElementById("currPick") as HTMLSpanElement | null;
        let nextUserPickSpan: HTMLSpanElement | null = document.getElementById("nextUserPick") as HTMLSpanElement | null;
        let draftLogPar: HTMLSpanElement | null = document.getElementById("draftLogPar") as HTMLParagraphElement | null;
        
        if (draftNotStartedForm == null) {
            console.log("draftNotStartedForm is null. Try again.")
            return;
        }
        else draftNotStartedForm.style.display = "none";

        let draftPosition: number = parseInt(getCookie("draftPosition"));

        if (draftPosition == 1) {
             if (draftControllerForm == null || currPickSpan == null || nextUserPickSpan == null) {
                console.log("draftControllerForm or currPickSpan or nextUserPickSpan is null. Try again.")
            }
            else {
                draftControllerForm.style.display = "block";
                currPickSpan.innerHTML = String(await getCurrPick());
                nextUserPickSpan.innerHTML = String(await getNextUserPick());
            }
        }
        else {
            if (waitingDraftControllerForm == null || currPickSpan == null || nextUserPickSpan == null) {
                console.log("waitingDraftControllerForm or currPickSpan or nextUserPickSpan is null. Try again.")
                return;
            }
            else {
                waitingDraftControllerForm.style.display = "block";
                currPickSpan.innerHTML = String(await getCurrPick());
                nextUserPickSpan.innerHTML = String(await getNextUserPick());
            }
        }
        if (draftLogPar == null) {
            console.log("draftLogPar is null. Try again.")
            return;
        }
        draftLogPar.innerHTML = "Waiting for user input..."
        getPlayerLeft();
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function getCurrRound(): Promise<number> {
    let res = await fetch("http://localhost:8080/api/teams/getCurrRound/?username=" + getCookie("username"),{
        method: 'GET',
    })
    let data: number = await res.json()
    console.log(data)
    return data;
}   

async function getCurrPick(): Promise<number> {
    let res = await fetch("http://localhost:8080/api/teams/getCurrPick/?username=" + getCookie("username"),{
        method: 'GET',
    })
    let data: number = await res.json()
    console.log(data)
    return data;
}   

async function getNextUserPick(): Promise<number> {
    let res = await fetch("http://localhost:8080/api/teams/getNextUserPick/?username="+getCookie("username"),{
        method: 'GET',
    })
    let data: number = await res.json()
    console.log(data)
    return data;
}

async function getNextUserPickRound(): Promise<number> {
    let res = await fetch("http://localhost:8080/api/teams/getNextUserPickRound/?username="+getCookie("username"),{
        method: 'GET',
    })
    let data: number = await res.json()
    console.log(data)
    return data;
}

async function getPlayerLeft(): Promise<void> {
    let res = await fetch("http://localhost:8080/api/teams/getPlayersLeft/?username="+getCookie("username"),{
        method: 'GET',
    })
    let data: Player[] = await res.json()
    console.log(data)
    let returnVal = ""
    for(let intCurrPlayer in data) {
        let currPlayer: Player = data[intCurrPlayer]
        returnVal += ((1+Number(intCurrPlayer)) + ". " + currPlayer.fullName + " " +currPlayer.position + " - Predicted Score 2022 = " 
        + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />")
    }
    console.log(returnVal)
    let PlayerLeftPar: HTMLParagraphElement | null = document.getElementById("playerListPar") as HTMLParagraphElement | null;
    if (PlayerLeftPar == null) {
        console.log("PlayerLeftPar is null. Try again.")
    }
    else {
        PlayerLeftPar.innerHTML = returnVal;
    }
}

async function updatePlayerListPar(data) {
    if(await authenticateSession() == true) {
        returnVal = ""
        for(let intCurrPlayer in data) {
            currPlayer = data[intCurrPlayer]
            returnVal += ((1+Number(intCurrPlayer)) + ". " + currPlayer.fullName + ", " +currPlayer.position + " - Predicted Score 2022 = " 
            + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />")
        }
        document.getElementById("playerListPar").innerHTML = returnVal
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function parseDraftLogData(data) {
    let updatedDraftLog
    let currPick = await getCurrPick();
    let currRound = await getCurrRound()

    if (currPick == 1 && currRound == 1) {
        document.getElementById("draftLogPar").innerHTML = ""
        updateVal = ""
    }
    else updateVal = document.getElementById("draftLogPar").innerHTML;

    for(let intCurrPlayer in data) {
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
    let currPick = await getCurrPick();
    let currRound = await getCurrRound();
    if (currPick == 1  && currRound == 1) {
        document.getElementById("draftLogPar").innerHTML = ""
    }
}

async function changeFormForNextPick() {
    let nextDraftPick = await getCurrPick();
    let currRound = await getCurrRound();
    let nextUserPick = await getNextUserPick();
    let nextUserPickRound = await getNextUserPickRound();
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
    if(await authenticateSession() == true) {
        await checkToClearDraftLog();          
        let res = await
        fetch("http://localhost:8080/api/teams/simTo/?username="+getCookie("username"), {
                method: 'POST',
            })
        let data = await res.json()

        await getPlayerLeft();
        parseDraftLogData(data)

        if (await endDraft() == false) {
            await changeFormForNextPick();
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function userDraftPlayer() {
    if(await authenticateSession() == true) {
        await checkToClearDraftLog();   
        let playerToDraftIndex = parseInt(document.getElementById("nextDraftPick").value)

        let res = await
        fetch("http://localhost:8080/api/teams/userDraftPlayer/?username="+getCookie("username")+"&playerIndex="+playerToDraftIndex, {
                method: 'POST',
            })
        let data = await res.json()
        console.log(data)

        await getPlayerLeft();
        parseDraftLogData(data)

        if (await endDraft() == false) {
            await changeFormForNextPick();
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
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
    if(await authenticateSession() == true) {
        let res = await fetch("http://localhost:8080/api/teams/deleteThisDraft/?username="+getCookie("username"),{
            method: 'POST',})
        let boolForCurrentDraft = await res.json();
        console.log(boolForCurrentDraft);    
        goToHomePage(); 
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}