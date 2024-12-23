"use strict";
async function setUpDraft() {
    if (await authenticateSession() == true) {
        loadUserName();
        let draftType = getCookie("draftType");
        if (draftType == "resume") {
            let endOfDraft = await endDraft();
            if (endOfDraft == false) {
                let draftNotStartedForm = document.getElementById("draftNotStartedForm");
                let draftControllerForm = document.getElementById("draftControllerForm");
                if (draftNotStartedForm == null || draftControllerForm == null) {
                    showMessage("An error occurred while loading the draft interface. Please refresh the page.", "error");
                    return;
                }
                else {
                    draftNotStartedForm.style.display = "none";
                    draftControllerForm.style.display = "none";
                }
                let currPick = await getCurrPick();
                let nextUserPick = await getNextUserPick();
                let currRound = await getCurrRound();
                console.log("currPick = " + currPick);
                console.log("nextUserPick = " + nextUserPick);
                let waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
                let currUserPickRound = document.getElementById("currUserPickRound");
                let currUserPickSpot = document.getElementById("currUserPickSpot");
                if (draftControllerForm == null || waitingDraftControllerForm == null || currUserPickRound == null || currUserPickSpot == null) {
                    showMessage("An error occurred while loading the draft interface. Please refresh the page.", "error");
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
                        let currRoundSpan = document.getElementById("currRound");
                        let nextUserPickRoundSpan = document.getElementById("nextUserPickRound");
                        if (currRoundSpan == null || nextUserPickRoundSpan == null) {
                            console.log("currRoundSpan or nextUserPickRoundSpan is null. Try again.");
                            return;
                        }
                        else {
                            currRoundSpan.innerHTML = String(currRound);
                            nextUserPickRoundSpan.innerHTML = String(currRound + 1);
                        }
                    }
                }
                let currPickSpan = document.getElementById("currPick");
                let nextUserPickSpan = document.getElementById("nextUserPick");
                if (currPickSpan == null || nextUserPickSpan == null) {
                    showMessage("An error occurred while loading the draft interface. Please refresh the page.", "error");
                    return;
                }
                else {
                    currPickSpan.innerHTML = String(currPick);
                    nextUserPickSpan.innerHTML = String(nextUserPick);
                }
            }
            else {
                let draftNotStartedForm = document.getElementById("draftNotStartedForm");
                if (draftNotStartedForm == null) {
                    console.log("draftNotStartedForm is null. Try again.");
                    return;
                }
                else {
                    draftNotStartedForm.style.display = "none";
                }
            }
            getPlayerLeft();
            let res = await fetch("http://localhost:80/api/teams/getAllPlayersDrafted/?username=" + getCookie("username"), {
                method: 'GET',
            });
            let data = await res.json();
            parseDraftLogData(data);
        }
        else if (draftType == "new") {
            showMessage("Starting new draft...", "success");
        }
        else {
            showMessage("Invalid draft type. Please try again.", "error");
        }
    }
    else {
        showMessage("You must be logged in to access the draft. Redirecting to login page...", "error");
        setTimeout(() => {
            deleteAllCookies();
            window.location.href = "loginpage.html";
        }, 2000);
    }
}
async function startDraftFromDraftPage() {
    if (await authenticateSession() == true) {
        let draftNotStartedForm = document.getElementById("draftNotStartedForm");
        let draftControllerForm = document.getElementById("draftControllerForm");
        let waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
        let currPickSpan = document.getElementById("currPick");
        let nextUserPickSpan = document.getElementById("nextUserPick");
        let draftLogPar = document.getElementById("draftLogPar");
        if (draftNotStartedForm == null) {
            console.log("draftNotStartedForm is null. Try again.");
            return;
        }
        else
            draftNotStartedForm.style.display = "none";
        let draftPosition = parseInt(getCookie("draftPosition"));
        if (draftPosition == 1) {
            if (draftControllerForm == null || currPickSpan == null || nextUserPickSpan == null) {
                console.log("draftControllerForm or currPickSpan or nextUserPickSpan is null. Try again.");
            }
            else {
                draftControllerForm.style.display = "block";
                currPickSpan.innerHTML = String(await getCurrPick());
                nextUserPickSpan.innerHTML = String(await getNextUserPick());
            }
        }
        else {
            if (waitingDraftControllerForm == null || currPickSpan == null || nextUserPickSpan == null) {
                console.log("waitingDraftControllerForm or currPickSpan or nextUserPickSpan is null. Try again.");
                return;
            }
            else {
                waitingDraftControllerForm.style.display = "block";
                currPickSpan.innerHTML = String(await getCurrPick());
                nextUserPickSpan.innerHTML = String(await getNextUserPick());
            }
        }
        if (draftLogPar == null) {
            console.log("draftLogPar is null. Try again.");
            return;
        }
        draftLogPar.innerHTML = "Waiting for user input...";
        getPlayerLeft();
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function getCurrRound() {
    let res = await fetch("http://localhost:80/api/teams/getCurrRound/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getCurrPick() {
    let res = await fetch("http://localhost:80/api/teams/getCurrPick/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getNextUserPick() {
    let res = await fetch("http://localhost:80/api/teams/getNextUserPick/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getNextUserPickRound() {
    let res = await fetch("http://localhost:80/api/teams/getNextUserPickRound/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getPlayerLeft() {
    let res = await fetch("http://localhost:80/api/teams/getPlayersLeft/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    let returnVal = "";
    for (let intCurrPlayer in data) {
        let currPlayer = data[intCurrPlayer];
        returnVal += ((1 + Number(intCurrPlayer)) + ". " + currPlayer.fullName + " " + currPlayer.position + " - Predicted Score 2024 = "
            + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />");
    }
    console.log(returnVal);
    let PlayerLeftPar = document.getElementById("playerListPar");
    if (PlayerLeftPar == null) {
        console.log("PlayerLeftPar is null. Try again.");
    }
    else {
        PlayerLeftPar.innerHTML = returnVal;
    }
}
async function updatePlayerListPar(data) {
    if (await authenticateSession() == true) {
        let returnVal = "";
        for (let intCurrPlayer in data) {
            let currPlayer = data[intCurrPlayer];
            returnVal += ((1 + Number(intCurrPlayer)) + ". " + currPlayer.fullName + ", " + currPlayer.position + " - Predicted Score 2024 = "
                + currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />");
        }
        let PlayerLeftPar = document.getElementById("playerListPar");
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
}
async function parseDraftLogData(data) {
    let updateVal;
    let currPick = await getCurrPick();
    let currRound = await getCurrRound();
    if (currPick == 1 && currRound == 1) {
        let draftLogPar = document.getElementById("draftLogPar");
        if (draftLogPar == null) {
            console.log("draftLogPar is null. Try again.");
            return;
        }
        else {
            draftLogPar.innerHTML = "";
            updateVal = "";
        }
    }
    else {
        let draftLogPar = document.getElementById("draftLogPar");
        if (draftLogPar == null) {
            console.log("draftLogPar is null. Try again.");
            return;
        }
        else {
            updateVal = draftLogPar.innerHTML;
        }
    }
    for (let intCurrPlayer in data) {
        let currPlayer = data[intCurrPlayer];
        console.log(currPlayer);
        if (currPlayer.firstName == null) {
            await endDraft();
            break;
        }
        else {
            updateVal += currPlayer.spotDrafted + " - " + currPlayer.teamDraftedBy + " picked " + currPlayer.fullName + ", " + currPlayer.position + " - Predicted Score 2024 = " +
                currPlayer.predictedScore + ", Avg ADP = " + currPlayer.avgADP + "<br />";
        }
    }
    let draftLogPar = document.getElementById("draftLogPar");
    if (draftLogPar == null) {
        console.log("draftLogPar is null. Try again.");
        return;
    }
    else {
        draftLogPar.innerHTML = updateVal;
    }
}
async function checkToClearDraftLog() {
    let currPick = await getCurrPick();
    let currRound = await getCurrRound();
    if (currPick == 1 && currRound == 1) {
        let draftLogPar = document.getElementById("draftLogPar");
        if (draftLogPar == null) {
            console.log("draftLogPar is null. Try again.");
            return;
        }
        else {
            draftLogPar.innerHTML = "";
        }
    }
}
async function changeFormForNextPick() {
    let nextDraftPick = await getCurrPick();
    let currRound = await getCurrRound();
    let nextUserPick = await getNextUserPick();
    let nextUserPickRound = await getNextUserPickRound();
    console.log(currRound + "." + nextDraftPick + " - " + nextUserPick);
    if (nextDraftPick == nextUserPick && nextUserPickRound == currRound) {
        let draftControllerForm = document.getElementById("draftControllerForm");
        let waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
        if (draftControllerForm == null || waitingDraftControllerForm == null) {
            console.log("draftControllerForm or waitingDraftControllerForm is null. Try again.");
            return;
        }
        else {
            draftControllerForm.style.display = "block";
            waitingDraftControllerForm.style.display = "none";
        }
    }
    else {
        let draftControllerForm = document.getElementById("draftControllerForm");
        let waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
        if (draftControllerForm == null || waitingDraftControllerForm == null) {
            console.log("draftControllerForm or waitingDraftControllerForm is null. Try again.");
            return;
        }
        else {
            draftControllerForm.style.display = "none";
            waitingDraftControllerForm.style.display = "block";
        }
    }
    let currPickSpan = document.getElementById("currPick");
    let currRoundSpan = document.getElementById("currRound");
    if (currPickSpan == null || currRoundSpan == null) {
        console.log("currPickSpan or currRoundSpan is null. Try again.");
        return;
    }
    else {
        currPickSpan.innerHTML = String(nextDraftPick);
        currRoundSpan.innerHTML = String(currRound);
    }
    if (currRound >= 15 && nextUserPickRound >= 16) {
        let nextPickInfo = document.getElementById("nextPickInfo");
        if (nextPickInfo == null) {
            console.log("nextUserPickSpan is null. Try again.");
            return;
        }
        else
            nextPickInfo.innerHTML = "the draft will end at the end of this round. " +
                "Press the button below to end the draft.";
    }
    else {
        let nextUserPickSpan = document.getElementById("nextUserPick");
        let nextUserPickRoundSpan = document.getElementById("nextUserPickRound");
        let currUserPickRoundSpan = document.getElementById("currUserPickRound");
        let currUserPickSpotSpan = document.getElementById("currUserPickSpot");
        if (nextUserPickSpan == null || nextUserPickRoundSpan == null || currUserPickRoundSpan == null || currUserPickSpotSpan == null) {
            console.log("nextUserPickSpan or nextUserPickRoundSpan or currUserPickRoundSpan or currUserPickSpotSpan is null. Try again.");
            return;
        }
        else {
            nextUserPickSpan.innerHTML = String(nextUserPick);
            nextUserPickRoundSpan.innerHTML = String(nextUserPickRound);
            currUserPickRoundSpan.innerHTML = String(currRound);
            currUserPickSpotSpan.innerHTML = String(nextUserPick);
        }
    }
}
async function simulateToNextPick() {
    if (await authenticateSession() == true) {
        await checkToClearDraftLog();
        let res = await fetch("http://localhost:80/api/teams/simTo/?username=" + getCookie("username"), {
            method: 'POST',
        });
        let data = await res.json();
        await getPlayerLeft();
        parseDraftLogData(data);
        if (await endDraft() == false) {
            await changeFormForNextPick();
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function userDraftPlayer() {
    if (await authenticateSession() == true) {
        let nextDraftPick = document.getElementById("nextDraftPick");
        if (nextDraftPick == null) {
            showMessage("An error occurred while reading your draft pick. Please try again.", "error");
            return;
        }
        let pick = parseInt(nextDraftPick.value);
        if (isNaN(pick) || !Number.isInteger(pick) || pick < 1) {
            showMessage("Please enter a valid player number to draft.", "error");
            return;
        }
        let res = await fetch("http://localhost:80/api/teams/userDraftPlayer/?username=" + getCookie("username") + "&playerIndex=" + pick, {
            method: 'POST',
        });
        let data = await res.json();
        parseDraftLogData(data);
        getPlayerLeft();
        nextDraftPick.value = "";
        let currPick = await getCurrPick();
        let nextUserPick = await getNextUserPick();
        let currRound = await getCurrRound();
        let draftControllerForm = document.getElementById("draftControllerForm");
        let waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
        let currPickSpan = document.getElementById("currPick");
        let nextUserPickSpan = document.getElementById("nextUserPick");
        if (draftControllerForm == null || waitingDraftControllerForm == null || currPickSpan == null || nextUserPickSpan == null) {
            showMessage("An error occurred while updating the draft interface. Please refresh the page.", "error");
            return;
        }
        draftControllerForm.style.display = "none";
        waitingDraftControllerForm.style.display = "block";
        currPickSpan.innerHTML = String(currPick);
        nextUserPickSpan.innerHTML = String(nextUserPick);
        changeFormForNextPick();
    }
    else {
        showMessage("You must be logged in to make a draft pick. Redirecting to login page...", "error");
        setTimeout(() => {
            deleteAllCookies();
            window.location.href = "loginpage.html";
        }, 2000);
    }
}
async function endDraft() {
    if (await getCurrRound() >= 16) {
        let draftControllerForm = document.getElementById("draftControllerForm");
        let waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
        let draftCompleteForm = document.getElementById("draftCompleteForm");
        if (draftControllerForm == null || waitingDraftControllerForm == null || draftCompleteForm == null) {
            console.log("draftControllerForm or waitingDraftControllerForm or draftCompleteForm is null. Try again.");
            return null;
        }
        else {
            draftControllerForm.style.display = "none";
            waitingDraftControllerForm.style.display = "none";
            draftCompleteForm.style.display = "block";
        }
        return true;
    }
    else
        return false;
}
async function endOfCurrentDraft() {
    if (await authenticateSession() == true) {
        let res = await fetch("http://localhost:80/api/teams/userMarkCurrentDraftComplete/?username=" + getCookie("username"), {
            method: 'POST',
        });
        let boolForCurrentDraft = await res.json();
        console.log(boolForCurrentDraft);
        goToHomePage();
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
