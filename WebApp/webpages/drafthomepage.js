"use strict";
async function checkForUserDraftHistory() {
    if (await authenticateSession() == true) {
        loadUserName();
        let res = await fetch("http://localhost:80/api/teams/checkForCurrentDraft/?username=" + getCookie("username"), {
            method: 'POST'
        });
        let boolForCurrentDraft = await res.json();
        console.log("current draft - " + boolForCurrentDraft);
        let res2 = await fetch("http://localhost:80/api/teams/checkForPastDraft/?username=" + getCookie("username"), {
            method: 'POST'
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
        let res = await fetch("http://localhost:80/api/teams/checkForCurrentDraft/?username=" + getCookie("username"), {
            method: 'POST',
        });
        let boolForCurrentDraft = await res.json();
        console.log(boolForCurrentDraft);
        if (boolForCurrentDraft == true) {
            alert("You already have a draft in progress. Please resume that draft or delete it before starting a new one.");
        }
        else {
            let draftModeSelectContainer = document.getElementById("draftModeSelectContainer");
            let draftFormContainer = document.getElementById("draftFormContainer");
            if (draftModeSelectContainer == null || draftFormContainer == null) {
                console.log("draftModeSelectContainer or draftFormContainer is null. Try again.");
                return;
            }
            else {
                draftModeSelectContainer.style.display = "none";
                draftFormContainer.style.display = "block";
            }
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
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
        window.location.href = "draftHistory.html";
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function selectedDeleteCurrentDraft() {
    if (await authenticateSession() == true) {
        let res = await fetch("http://localhost:80/api/teams/deleteThisDraft/?username=" + getCookie("username"), {
            method: 'POST',
        });
        let boolForCurrentDraft = await res.json();
        console.log(boolForCurrentDraft);
        alert("Draft deleted. You may now start a new draft.");
        let resumeDraftButton = document.getElementById("resumeDraftButton");
        let deleteDraftButton = document.getElementById("deleteDraftButton");
        if (resumeDraftButton == null || deleteDraftButton == null) {
            console.log("resumeDraftButton or deleteDraftButton is null. Try again.");
            return;
        }
        else {
            resumeDraftButton.style.display = "none";
            deleteDraftButton.style.display = "none";
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
async function startDraft() {
    function checkStartDraftInput(teamName, draftSize, draftPosition) {
        if (typeof teamName != "string") {
            alert(teamName + " - Not a valid string value to name your team. Try again.");
            return false;
        }
        if (typeof draftSize != "number" ||
            !Number.isInteger(draftSize)) {
            alert(draftSize + " - Not a valid int entered for the draft size. Try again.");
            return false;
        }
        if (typeof draftPosition != "number" ||
            !Number.isInteger(draftPosition) ||
            draftPosition > draftSize) {
            alert(draftPosition + " - Not a valid int entered for your starting draft position. Try again.");
            return false;
        }
        return true;
    }
    if (await authenticateSession() == true) {
        let teamNameInput = document.getElementById("teamNameInput");
        let sizeOfTeamsInput = document.getElementById("sizeOfTeamsInput");
        let draftPositionInput = document.getElementById("draftPositionInput");
        if (teamNameInput == null || sizeOfTeamsInput == null || draftPositionInput == null) {
            console.log("teamNameInput or sizeOfTeamsInput or draftPositionInput is null. Try again.");
            return;
        }
        else {
            let teamName = teamNameInput.value;
            let draftSizeString = sizeOfTeamsInput.value;
            let draftPositionString = draftPositionInput.value;
            let draftSize = parseInt(draftSizeString);
            let draftPosition = parseInt(draftPositionString);
            if (checkStartDraftInput(teamName, draftSize, draftPosition) == true) {
                let res = await fetch(("http://localhost:80/api/teams/startDraft/?username=" + getCookie("username") + "&teamName=" + teamName + "&draftSize=" + draftSize + "&draftPosition=" + draftPosition), {
                    method: 'POST',
                });
                let data = await res.json();
                console.log(data);
                document.cookie = "teamName=" + teamName + "; path=/";
                document.cookie = "draftPosition=" + draftPositionString + "; path=/";
            }
            document.cookie = "draftType=new; path=/";
            window.location.href = "draftpage.html";
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}
