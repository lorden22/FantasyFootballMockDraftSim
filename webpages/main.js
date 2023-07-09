function loadGifWhileRenderPage(functionToLoad) {
    setTimeout(function() {
        document.getElementById("loading-container").style.display = "none";
        document.getElementById("content-container").style.display = "block";
    }, 300);
    functionToLoad();
}

function loadGifWhileBigPageChange(functionToLoad) {
    document.getElementById("loading-container").style.display =  setTimeout(function() {
        document.getElementById("loading-container").style.display = "none";
        document.getElementById("content-container").style.display = "block";
    }, 50);
    functionToLoad();
}

function goToHomePage() {
    deleteCookie("draftType");
    deleteCookie("draftPosition");
    deleteCookie("teamName");
    deleteCookie("draftIDToView");
    window.location.href = "drafthomepage.html";
}

function loadUserName() {
    console.log("UserName = " + getCookie("username"))
    if(getCookie("username") == "") {
        alert("You must be logged in to view this page.")
        window.location.href = "loginpage.html"
    }
    else {
        document.getElementById("userNameSpan").innerHTML = getCookie("username")
    }
}