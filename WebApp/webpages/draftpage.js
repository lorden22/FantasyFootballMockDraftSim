"use strict";
// Helper function to format ADP by removing leading zeros after decimal
function formatADP(adp) {
    // For values like 4.01 -> 4.1, 4.02 -> 4.2, etc.
    // Remove leading zero after decimal point when followed by single digit
    const formatted = adp.toFixed(2);
    return formatted.replace(/\.0(\d)$/, '.$1');
}
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
                        // UPDATE VALUES FIRST
                        currUserPickRound.innerHTML = String(currRound);
                        currUserPickSpot.innerHTML = String(currPick);
                        // THEN show/hide divs with small delay for DOM rendering
                        setTimeout(() => {
                            draftControllerForm.style.display = "block";
                            waitingDraftControllerForm.style.display = "none";
                        }, 10);
                    }
                    else {
                        // UPDATE VALUES FIRST
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
                        // THEN show div with small delay for DOM rendering
                        setTimeout(() => {
                            waitingDraftControllerForm.style.display = "block";
                        }, 10);
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
            // Load both players left and draft log in parallel, wait for completion
            await Promise.all([
                getPlayerLeft(),
                (async () => {
                    let res = await fetch("/api/teams/getAllPlayersDrafted/?username=" + getCookie("username"), {
                        method: 'GET',
                    });
                    let data = await res.json();
                    parseDraftLogData(data);
                    // Small delay for DOM rendering
                    await new Promise(resolve => setTimeout(resolve, 150));
                })()
            ]);
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
                // UPDATE VALUES FIRST
                currPickSpan.innerHTML = String(await getCurrPick());
                nextUserPickSpan.innerHTML = String(await getNextUserPick());
                // THEN show div with small delay for DOM rendering
                setTimeout(() => {
                    draftControllerForm.style.display = "block";
                }, 10);
            }
        }
        else {
            if (waitingDraftControllerForm == null || currPickSpan == null || nextUserPickSpan == null) {
                console.log("waitingDraftControllerForm or currPickSpan or nextUserPickSpan is null. Try again.");
                return;
            }
            else {
                // UPDATE VALUES FIRST
                currPickSpan.innerHTML = String(await getCurrPick());
                nextUserPickSpan.innerHTML = String(await getNextUserPick());
                // THEN show div with small delay for DOM rendering  
                setTimeout(() => {
                    waitingDraftControllerForm.style.display = "block";
                }, 10);
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
    let res = await fetch("/api/teams/getCurrRound/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getCurrPick() {
    let res = await fetch("/api/teams/getCurrPick/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getNextUserPick() {
    let res = await fetch("/api/teams/getNextUserPick/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getNextUserPickRound() {
    let res = await fetch("/api/teams/getNextUserPickRound/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    return data;
}
async function getPlayerLeft() {
    let res = await fetch("/api/teams/getPlayersLeft/?username=" + getCookie("username"), {
        method: 'GET',
    });
    let data = await res.json();
    console.log(data);
    // Update the table
    updatePlayersLeftTable(data);
}
// New function to update the players left table
function updatePlayersLeftTable(data) {
    const tableBody = document.getElementById("playersLeftTableBody");
    if (!tableBody) {
        console.log("playersLeftTableBody is null. Try again.");
        return;
    }
    // Clear existing rows
    tableBody.innerHTML = "";
    // Add new rows using safe DOM methods to prevent XSS
    for (let intCurrPlayer in data) {
        const currPlayer = data[intCurrPlayer];
        const playerNumber = 1 + Number(intCurrPlayer);
        const row = document.createElement("tr");
        // Create cells safely using textContent instead of innerHTML
        const cellNumber = document.createElement("td");
        cellNumber.textContent = String(playerNumber);
        const cellName = document.createElement("td");
        cellName.textContent = currPlayer.fullName;
        const cellPosition = document.createElement("td");
        cellPosition.textContent = currPlayer.position;
        const cellScore = document.createElement("td");
        cellScore.textContent = String(currPlayer.predictedScore);
        const cellADP = document.createElement("td");
        cellADP.textContent = formatADP(currPlayer.avgADP);
        row.appendChild(cellNumber);
        row.appendChild(cellName);
        row.appendChild(cellPosition);
        row.appendChild(cellScore);
        row.appendChild(cellADP);
        // Add click event to select player
        row.addEventListener('click', () => {
            const nextDraftPickInput = document.getElementById("nextDraftPick");
            if (nextDraftPickInput) {
                nextDraftPickInput.value = playerNumber.toString();
            }
        });
        tableBody.appendChild(row);
    }
}
async function updatePlayerListPar(data) {
    if (await authenticateSession() == true) {
        // Update the table instead of the paragraph
        updatePlayersLeftTable(data);
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
// New function to update the draft log table (REVERSED ORDER - most recent at top)
function updateDraftLogTable(data) {
    const tableBody = document.getElementById("draftLogTableBody");
    if (!tableBody) {
        console.log("draftLogTableBody is null. Try again.");
        return;
    }
    // Clear existing rows only if this is the start of a new draft
    const currPick = data.length > 0 ? String(data[0].spotDrafted) : "";
    if (currPick === "1.1" || tableBody.children.length === 0) {
        tableBody.innerHTML = "";
    }
    // Add new rows for recently drafted players - INSERT AT TOP (reversed order)
    // Using safe DOM methods to prevent XSS
    for (let intCurrPlayer in data) {
        const currPlayer = data[intCurrPlayer];
        console.log(currPlayer);
        if (currPlayer.firstName == null) {
            break;
        }
        const row = document.createElement("tr");
        // Create cells safely using textContent instead of innerHTML
        const cellSpot = document.createElement("td");
        cellSpot.textContent = String(currPlayer.spotDrafted);
        const cellName = document.createElement("td");
        cellName.textContent = currPlayer.fullName;
        const cellPosition = document.createElement("td");
        cellPosition.textContent = currPlayer.position;
        const cellTeam = document.createElement("td");
        cellTeam.textContent = currPlayer.teamDraftedBy;
        row.appendChild(cellSpot);
        row.appendChild(cellName);
        row.appendChild(cellPosition);
        row.appendChild(cellTeam);
        // INSERT AT TOP instead of append (most recent picks at top)
        tableBody.insertBefore(row, tableBody.firstChild);
    }
    // NO AUTO-SCROLL needed since most recent picks are now at the top (always visible)
}
async function parseDraftLogData(data) {
    // Update the table instead of the paragraph
    updateDraftLogTable(data);
}
async function checkToClearDraftLog() {
    let currPick = await getCurrPick();
    let currRound = await getCurrRound();
    if (currPick == 1 && currRound == 1) {
        const tableBody = document.getElementById("draftLogTableBody");
        if (tableBody == null) {
            console.log("draftLogTableBody is null. Try again.");
            return;
        }
        else {
            tableBody.innerHTML = "";
        }
    }
}
async function changeFormForNextPick() {
    let nextDraftPick = await getCurrPick();
    let currRound = await getCurrRound();
    let nextUserPick = await getNextUserPick();
    let nextUserPickRound = await getNextUserPickRound();
    console.log(currRound + "." + nextDraftPick + " - " + nextUserPick);
    // UPDATE VALUES FIRST before showing/hiding divs
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
    // NOW show/hide divs AFTER values are updated (with small delay for DOM rendering)
    setTimeout(() => {
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
    }, 10); // Very small delay to ensure DOM updates are rendered
}
async function simulateToNextPick() {
    if (await authenticateSession() == true) {
        await checkToClearDraftLog();
        let res = await fetch("/api/teams/simTo/?username=" + getCookie("username"), {
            method: 'POST',
        });
        let data = await res.json();
        // Wait for both table updates to complete before showing controls
        await Promise.all([
            getPlayerLeft(),
            new Promise((resolve) => {
                parseDraftLogData(data);
                // Give a small delay for DOM updates to complete
                setTimeout(() => resolve(), 100);
            })
        ]);
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
        let res = await fetch("/api/teams/userDraftPlayer/?username=" + getCookie("username") + "&playerIndex=" + pick, {
            method: 'POST',
        });
        let data = await res.json();
        // Wait for both table updates to complete before updating controls
        await Promise.all([
            getPlayerLeft(),
            new Promise((resolve) => {
                parseDraftLogData(data);
                // Give a small delay for DOM updates to complete
                setTimeout(() => resolve(), 100);
            })
        ]);
        nextDraftPick.value = "";
        let currPick = await getCurrPick();
        let nextUserPick = await getNextUserPick();
        let currRound = await getCurrRound();
        let nextUserPickRound = await getNextUserPickRound();
        let draftControllerForm = document.getElementById("draftControllerForm");
        let waitingDraftControllerForm = document.getElementById("waitingDraftControllerForm");
        let currPickSpan = document.getElementById("currPick");
        let nextUserPickSpan = document.getElementById("nextUserPick");
        let currRoundSpan = document.getElementById("currRound");
        let nextUserPickRoundSpan = document.getElementById("nextUserPickRound");
        if (draftControllerForm == null || waitingDraftControllerForm == null || currPickSpan == null || nextUserPickSpan == null || currRoundSpan == null || nextUserPickRoundSpan == null) {
            showMessage("An error occurred while updating the draft interface. Please refresh the page.", "error");
            return;
        }
        // UPDATE ALL VALUES FIRST before showing/hiding divs
        currPickSpan.innerHTML = String(currPick);
        nextUserPickSpan.innerHTML = String(nextUserPick);
        currRoundSpan.innerHTML = String(currRound);
        nextUserPickRoundSpan.innerHTML = String(nextUserPickRound);
        // THEN show/hide divs with small delay for DOM rendering
        setTimeout(() => {
            draftControllerForm.style.display = "none";
            waitingDraftControllerForm.style.display = "block";
        }, 10);
        await changeFormForNextPick();
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
        let res = await fetch("/api/teams/userMarkCurrentDraftComplete/?username=" + getCookie("username"), {
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
