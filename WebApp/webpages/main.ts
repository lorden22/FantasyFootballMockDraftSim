"use strict";

function loadGifWhileRenderPage(functionToLoad: () => void): void {
    setTimeout(() => {
        const loadingContainer: HTMLElement | null = document.getElementById("loading-container");
        const contentContainer: HTMLElement | null = document.getElementById("content-container");
        if (loadingContainer && contentContainer) {
            loadingContainer.style.display = "none";
            contentContainer.style.display = "block";
        }
    }, 300);
    functionToLoad();
}

function loadGifWhileBigPageChange(functionToLoad: () => void): void {
    setTimeout(() => {
        const loadingContainer: HTMLElement | null = document.getElementById("loading-container");
        const contentContainer: HTMLElement | null = document.getElementById("content-container");
        if (loadingContainer && contentContainer) {
            loadingContainer.style.display = "none";
            contentContainer.style.display = "block";
        }
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
    console.log("UserName = " + getCookie("username"));
    if (getCookie("username") == "") {
        alert("You must be logged in to view this page.");
        window.location.href = "loginpage.html";
    }
    else {
        const userNameSpan: HTMLElement | null = document.getElementById("userNameSpan");
        if (userNameSpan) {
            userNameSpan.innerHTML = getCookie("username");
        }
    }
}

async function authenticateSession(): Promise<boolean> {
    let authenticateSessionRes: Response;
    try {
        authenticateSessionRes = await fetch("http://localhost:80/api/login/attemptSession/?username=" + getCookie("username") + "&sessionID=" + getCookie("sessionID"), {
            method: 'GET',
        });
    }
    catch {
        showMessage("Server is down. Please try again later. Returning to login page.", "error");
        setTimeout(() => {
            window.location.href = "loginpage.html";
        }, 2000);
        return false;
    }
    let authenticateSessionData: boolean = await authenticateSessionRes.json();
    if (authenticateSessionData == true) {
        console.log("Session authenticated.");
        return true;
    }
    else {
        return false;
    }
}

async function logoutServerSide(): Promise<void> {
    let logoutRes: Response = await fetch("http://localhost:80/api/login/logout/?username=" + getCookie("username") + "&sessionID=" + getCookie("sessionID"), {
        method: 'POST',
    });
    let logoutData: boolean = await logoutRes.json();
    if (logoutData == true) {
        showMessage("Logout successful. Redirecting to login page...", "error");
        setTimeout(() => {
            deleteAllCookies();
            window.location.href = "loginpage.html";
        }, 2000);
    }
    else {
        showMessage("Logout failed.", "error");
    }
}

function showMessage(message: string, type: 'error' | 'success'): void {
    const messageContainer: HTMLElement | null = document.getElementById('message-container');
    if (messageContainer) {
        const messageElement: HTMLDivElement = document.createElement('div');
        messageElement.className = `message ${type}`;
        messageElement.textContent = message;
        messageContainer.appendChild(messageElement);
        messageContainer.classList.add('show');
        setTimeout(() => {
            messageElement.remove();
            if (messageContainer.children.length === 0) {
                messageContainer.classList.remove('show');
            }
        }, 3000);
    }
}