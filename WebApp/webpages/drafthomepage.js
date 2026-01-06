"use strict";
async function checkForUserDraftHistory() {
    if (await authenticateSession() == true) {
        loadUserName();
        let res = await fetch("/api/teams/checkForCurrentDraft/?username=" + getCookie("username"), {
            method: 'GET'
        });
        let boolForCurrentDraft = await res.json();
        console.log("current draft - " + boolForCurrentDraft);
        let res2 = await fetch("/api/teams/checkForPastDrafts/?username=" + getCookie("username"), {
            method: 'GET'
        });
        let boolPastDrafts = await res2.json();
        console.log("past drafts - " + boolPastDrafts);
        if (boolForCurrentDraft == true) {
            let resumeDraftButton = document.getElementById("resumeDraftButton");
            let deleteDraftButton = document.getElementById("deleteDraftButton");
            if (resumeDraftButton == null || deleteDraftButton == null) {
                console.log("resumeDraftButton or deleteDraftButton is null. Try again.");
                return;
            }
            else {
                resumeDraftButton.style.display = "inline-block";
                deleteDraftButton.style.display = "inline-block";
            }
        }
        if (boolPastDrafts == true) {
            let historalDraftButton = document.getElementById("historalDraftButton");
            if (historalDraftButton == null) {
                console.log("historalDraftButton is null. Try again.");
            }
            else {
                historalDraftButton.style.display = "inline-block";
            }
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function selectedStartNewDraft() {
    if (await authenticateSession() == true) {
        let res = await fetch("/api/teams/checkForCurrentDraft/?username=" + getCookie("username"), {
            method: 'GET',
        });
        let boolForCurrentDraft = await res.json();
        if (boolForCurrentDraft == true) {
            showMessage("You already have a draft in progress. Please resume that draft or delete it before starting a new one.", "error");
        }
        else {
            let draftModeSelectContainer = document.getElementById("draftModeSelectContainer");
            let draftFormContainer = document.getElementById("draftFormContainer");
            if (draftModeSelectContainer == null || draftFormContainer == null) {
                showMessage("An error occurred while loading the draft form. Please try again.", "error");
                return;
            }
            else {
                draftModeSelectContainer.style.display = "none";
                draftFormContainer.style.display = "block";
            }
        }
    }
    else {
        showMessage("You must be logged in to start a draft. Redirecting to login page...", "error");
        setTimeout(() => {
            deleteAllCookies();
            window.location.href = "loginpage.html";
        }, 2000);
    }
}
async function selectedResumeLastSavedDraft() {
    if (await authenticateSession() == true) {
        document.cookie = "draftType=resume;path=/";
        window.location.href = "draftpage.html";
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function selectedViewDraftHistory() {
    if (await authenticateSession() == true) {
        window.location.href = "drafthistory.html";
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function selectedDeleteCurrentDraft() {
    if (await authenticateSession()) {
        let res = await fetch("/api/teams/deleteThisDraft/?username=" + getCookie("username"), {
            method: 'POST',
        });
        let boolForCurrentDraft = await res.json();
        console.log(boolForCurrentDraft);
        showMessage("Draft deleted. You may now start a new draft.", "error");
        let resumeDraftButton = document.getElementById("resumeDraftButton");
        let deleteDraftButton = document.getElementById("deleteDraftButton");
        if (resumeDraftButton && deleteDraftButton) {
            resumeDraftButton.style.display = "none";
            deleteDraftButton.style.display = "none";
        }
        else {
            console.log("resumeDraftButton or deleteDraftButton is null. Try again.");
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function startDraft() {
    function checkStartDraftInput(teamName, draftSize, draftPosition) {
        if (typeof teamName != "string" || teamName.trim() === "") {
            showMessage("Please enter a valid team name.", "error");
            return false;
        }
        if (typeof draftSize != "number" || !Number.isInteger(draftSize) || draftSize < 2) {
            showMessage("Please enter a valid number of teams (must be at least 2).", "error");
            return false;
        }
        if (typeof draftPosition != "number" || !Number.isInteger(draftPosition) || draftPosition < 1 || draftPosition > draftSize) {
            showMessage("Please enter a valid draft position (must be between 1 and " + draftSize + ").", "error");
            return false;
        }
        return true;
    }
    if (await authenticateSession() == true) {
        let teamNameInput = document.getElementById("teamNameInput");
        let sizeOfTeamsInput = document.getElementById("sizeOfTeamsInput");
        let draftPositionInput = document.getElementById("draftPositionInput");
        if (teamNameInput == null || sizeOfTeamsInput == null || draftPositionInput == null) {
            showMessage("An error occurred while reading the form. Please try again.", "error");
            return;
        }
        else {
            let teamName = teamNameInput.value;
            let draftSizeString = sizeOfTeamsInput.value;
            let draftPositionString = draftPositionInput.value;
            let draftSize = parseInt(draftSizeString);
            let draftPosition = parseInt(draftPositionString);
            if (isNaN(draftSize)) {
                showMessage("Please enter a valid number for the number of teams.", "error");
                return;
            }
            if (isNaN(draftPosition)) {
                showMessage("Please enter a valid number for your draft position.", "error");
                return;
            }
            if (checkStartDraftInput(teamName, draftSize, draftPosition) == true) {
                let res = await fetch(("/api/teams/startDraft/?username=" + getCookie("username") + "&teamName=" + teamName + "&draftSize=" + draftSize + "&draftPosition=" + draftPosition), {
                    method: 'POST',
                });
                let data = await res.json();
                document.cookie = "teamName=" + teamName + "; path=/";
                document.cookie = "draftPosition=" + draftPositionString + "; path=/";
                document.cookie = "draftType=new; path=/";
                window.location.href = "draftpage.html";
            }
        }
    }
    else {
        showMessage("You must be logged in to start a draft. Redirecting to login page...", "error");
        setTimeout(() => {
            deleteAllCookies();
            window.location.href = "loginpage.html";
        }, 2000);
    }
}
