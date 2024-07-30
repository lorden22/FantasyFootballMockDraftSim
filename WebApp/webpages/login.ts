async function addUser(): Promise<void> {
    const userNameEle = document.getElementById("username") as HTMLInputElement;
    const passwordEle = document.getElementById("password") as HTMLInputElement;
    const userNameEleValue: string | null = userNameEle.value || null;
    const passwordEleValue: string | null = passwordEle.value || null;

    if (userNameEleValue === null || passwordEleValue === null) {
        showMessage("Username or password is null. Try again.", "error");
        return;
    }

    const username: string = userNameEleValue;
    const password: string = passwordEleValue;

    try {
        const checkUserRes = await fetch(`http://localhost:80/api/login/checkUser/?username=${username}`, {
            method: 'GET',
        });
        const checkUserData: boolean = await checkUserRes.json();

        if (checkUserData) {
            showMessage("Username already taken. Try again.", "error");
        } else {
            const addUserRes = await fetch(`http://localhost:80/api/login/addUser/?username=${username}&password=${password}`, {
                method: 'PUT',
            });
            const addUserData = await addUserRes.json();

            await fetch(`http://localhost:80/api/teams/initaizeUserAccountSetup/?username=${username}`, {
                method: 'POST',
            });

            showMessage("Account created. Please login.", "success");
            setTimeout(() => {
                window.location.href = "loginpage.html";
            }, 2000);
        }
    } catch (error) {
        showMessage("An error occurred. Please try again.", "error");
    }
}

async function attemptLogin(): Promise<void> {
    const userNameEle = document.getElementById("username") as HTMLInputElement;
    const passwordEle = document.getElementById("password") as HTMLInputElement;
    const userNameEleValue: string | null = userNameEle.value || null;
    const passwordEleValue: string | null = passwordEle.value || null;

    if (userNameEleValue === null || passwordEleValue === null) {
        showMessage("Username or password is null. Try again.", "error");
        return;
    }

    const username: string = userNameEleValue;
    const password: string = passwordEleValue;

    try {
        const attemptLoginRes = await fetch(`http://localhost:80/api/login/attemptLogin/?username=${username}&password=${password}`, {
            method: 'GET',
        });
        const attemptLoginData = await attemptLoginRes.json();

        if (attemptLoginData) {
            const generateSessionIDRes = await fetch(`http://localhost:80/api/login/generateSessionID/?username=${username}`, {
                method: 'GET',
            });
            const generateSessionIDData = await generateSessionIDRes.text();

            showMessage("Login successful.", "success");
            document.cookie = `username=${username}; path=/`;
            document.cookie = `sessionID=${generateSessionIDData}; path=/`;
            setTimeout(() => {
                window.location.href = "drafthomepage.html";
            }, 2000);
        } else {
            showMessage("Login failed. Try different credentials.", "error");
        }
    } catch (error) {
        showMessage("An error occurred. Please try again.", "error");
    }
}

function showMessage(message: string, type: string): void {
    const messageContainer = document.getElementById("message-container") as HTMLElement;
    messageContainer.innerHTML = `<div class="message ${type}">${message}</div>`;
    messageContainer.classList.add("show");
    setTimeout(() => {
        messageContainer.classList.remove("show");
    }, 3000);
}
