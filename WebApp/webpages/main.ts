function loadGifWhileRenderPage(functionToLoad: () => void): void {
    setTimeout(() => {
        document.getElementById("loading-container")!.style.display = "none";
        document.getElementById("content-container")!.style.display = "block";
    }, 300);
    functionToLoad();
}


function loadGifWhileBigPageChange(functionToLoad: () => void): void {
    setTimeout(() => {
        document.getElementById("loading-container")!.style.display = "none";
        document.getElementById("content-container")!.style.display = "block";
    }, 50);
    functionToLoad();
}

function goToHomePage(): void {
    deleteCookie("draftType");
    deleteCookie("draftPosition");
    deleteCookie("teamName");
    deleteCookie("draftIDToView");
    window.location.href = "drafthomepage.html";
}

function loadUserName(): void {
    console.log("UserName = " + getCookie("username"))
    if(getCookie("username") == "") {
        alert("You must be logged in to view this page.")
        window.location.href = "loginpage.html"
    }
    else {
        document.getElementById("userNameSpan")!.innerHTML = getCookie("username")
    }
}

async function authenticateSession(): Promise<boolean> {
    let authenticateSessionRes = await
    fetch("http://localhost:8080/api/login/attemptSession/?username="+getCookie("username")+"&sessionID="+getCookie("sessionID"), {
        method: 'GET',
    })

    let authenticateSessionData: Boolean = await authenticateSessionRes.json()

    if (authenticateSessionData == true) {
        console.log("Session authenticated.")
        return true;
    }
    else return false;
}

async function logoutServerSide(): Promise<void> {
     let logoutRes = await
    fetch("http://localhost:8080/api/login/logout/?username="+getCookie("username")+"&sessionID="+getCookie("sessionID"), {
        method: 'POST',
    })

    let logoutData: Boolean = await logoutRes.json()

    if (logoutData == true) {
        deleteAllCookies();
        window.location.href = "loginpage.html"
    }
    else alert("Logout failed.")
}