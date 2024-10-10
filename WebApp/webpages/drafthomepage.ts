async function checkForUserDraftHistory(): Promise<void> {
    if(await authenticateSession() == true) {
        loadUserName();

        let res = await fetch("http://localhost:80/api/teams/checkForCurrentDraft/?username="+getCookie("username"),{
            method: 'POST'})
        let boolForCurrentDraft: Boolean = await res.json()
        console.log("current draft - " + boolForCurrentDraft)

        let res2 = await fetch("http://localhost:80/api/teams/checkForPastDraft/?username="+getCookie("username"),{
            method: 'POST'})
        
        let boolPastDrafts: Boolean = await res2.json();
        console.log("past drafts - " + boolPastDrafts)

        if (boolForCurrentDraft == true) {
            let resumeDraftButton: HTMLButtonElement | null = document.getElementById("resumeDraftButton") as HTMLButtonElement | null;
            let deleteDraftButton: HTMLButtonElement | null = document.getElementById("deleteDraftButton") as HTMLButtonElement | null;
            
            if (resumeDraftButton == null || deleteDraftButton == null) {
                console.log("resumeDraftButton or deleteDraftButton is null. Try again.")
                return;
            }
            else {
                resumeDraftButton.style.display = "inline-block";
                deleteDraftButton.style.display = "inline-block"; 
            }
        }
        if (boolPastDrafts == true) {
            let historalDraftButton: HTMLButtonElement | null = document.getElementById("historalDraftButton") as HTMLButtonElement | null;
            if (historalDraftButton == null) {
                console.log("historalDraftButton is null. Try again.")
            }
            else {
                historalDraftButton.style.display = "inline-block";

            }
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}


async function selectedStartNewDraft(): Promise<void> {
    if(await authenticateSession() == true) {
        let res = await fetch("http://localhost:80/api/teams/checkForCurrentDraft/?username="+getCookie("username"),{
            method: 'POST',})
        let boolForCurrentDraft : Boolean = await res.json();
        console.log(boolForCurrentDraft)

        if (boolForCurrentDraft == true) {
            alert("You already have a draft in progress. Please resume that draft or delete it before starting a new one.")
        }
        else {

            let draftModeSelectContainer: HTMLDivElement | null = document.getElementById("draftModeSelectContainer") as HTMLDivElement | null;
            let draftFormContainer: HTMLDivElement | null = document.getElementById("draftFormContainer") as HTMLDivElement | null;
            if (draftModeSelectContainer == null || draftFormContainer == null) {
                console.log("draftModeSelectContainer or draftFormContainer is null. Try again.")
                return;
            }
            else {
                draftModeSelectContainer.style.display = "none";
                draftFormContainer.style.display = "block";
            }
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function selectedResumeLastSavedDraft(): Promise<void> { 
    if(await authenticateSession() == true) {
        document.cookie = "draftType=resume;path=/";
        window.location.href = "draftpage.html";
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function selectedViewDraftHistory(): Promise<void> {
    if(await authenticateSession() == true) {
        window.location.href = "drafthistory.html";
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}

async function selectedDeleteCurrentDraft(): Promise<void> {
    if (await authenticateSession()) {
        let res = await fetch("http://localhost:80/api/teams/deleteThisDraft/?username=" + getCookie("username"), {
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
        } else {
            console.log("resumeDraftButton or deleteDraftButton is null. Try again.");
        }
    } else {
        console.log("User not logged in. Redirecting to login page.");
        deleteAllCookies();
    }
}

async function startDraft(): Promise<void> {

    function checkStartDraftInput(teamName: string, draftSize: number, draftPosition: number): boolean {
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

    if(await authenticateSession() == true) {

        let teamNameInput: HTMLInputElement | null = document.getElementById("teamNameInput") as HTMLInputElement | null;
        let sizeOfTeamsInput: HTMLInputElement | null = document.getElementById("sizeOfTeamsInput") as HTMLInputElement | null;
        let draftPositionInput: HTMLInputElement | null = document.getElementById("draftPositionInput") as HTMLInputElement | null;

        if (teamNameInput == null || sizeOfTeamsInput == null || draftPositionInput == null) {
            console.log("teamNameInput or sizeOfTeamsInput or draftPositionInput is null. Try again.")
            return;
        }
        
        else {

            let teamName: string = teamNameInput.value;
            let draftSizeString: string = sizeOfTeamsInput.value;
            let draftPositionString: string = draftPositionInput.value;

        
            let draftSize = parseInt(draftSizeString)
            let draftPosition = parseInt(draftPositionString)

            if (checkStartDraftInput(teamName,draftSize,draftPosition) == true) {
                let res = await 
                fetch(("http://localhost:80/api/teams/startDraft/?username="+getCookie("username")+"&teamName="+teamName+"&draftSize="+draftSize+"&draftPosition="+draftPosition), {
                    method: 'POST',
                })
                let data = await res.json()
                console.log(data)

                document.cookie = "teamName=" + teamName + "; path=/";
                document.cookie = "draftPosition=" + draftPositionString + "; path=/";
            }
            document.cookie = "draftType=new; path=/";
            window.location.href = "draftpage.html"
        }
    }
    else {
        console.log("User not logged in. Redirecting to login page.")
        deleteAllCookies();
    }
}