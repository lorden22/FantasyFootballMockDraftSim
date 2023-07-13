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


async function selectedStartNewDraft() {
    var res = await fetch("http://localhost:8080/api/teams/checkForCurrentDrafts/?username="+getCookie("username"),{
        method: 'GET',})
    var boolForCurrentDraft = await res.json();
    console.log(boolForCurrentDraft)

    if (boolForCurrentDraft == true) {
        alert("You already have a draft in progress. Please resume that draft or delete it before starting a new one.")
    }
    else {
    document.getElementById("draftModeSelectContainer").style.display = "none";
    document.getElementById("draftFormContainer").style.display = "block";
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