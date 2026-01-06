"use strict";
async function addUser() {
    const userNameEle = document.getElementById("username");
    const passwordEle = document.getElementById("password");
    const emailEle = document.getElementById("email");
    const userNameEleValue = userNameEle.value || null;
    const passwordEleValue = passwordEle.value || null;
    const emailValue = emailEle?.value?.trim() || null;
    if (userNameEleValue === null || passwordEleValue === null) {
        showMessage("Username or password is null. Try again.", "error");
        return;
    }
    // Validate password length
    if (passwordEleValue.length < 8) {
        showMessage("Password must be at least 8 characters.", "error");
        return;
    }
    // Validate email format if provided
    if (emailValue && !/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(emailValue)) {
        showMessage("Please enter a valid email address.", "error");
        return;
    }
    const username = userNameEleValue;
    const password = passwordEleValue;
    try {
        const checkUserRes = await fetch(`/api/login/checkUser/?username=${encodeURIComponent(username)}`, {
            method: 'GET',
        });
        const checkUserData = await checkUserRes.json();
        if (checkUserData) {
            showMessage("Username already taken. Try again.", "error");
        }
        else {
            // Send credentials in request body, not URL
            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('password', password);
            const addUserRes = await fetch('/api/login/addUser/', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData,
            });
            const addUserData = await addUserRes.json();
            if (addUserData) {
                // If email was provided, save it
                if (emailValue) {
                    const emailFormData = new URLSearchParams();
                    emailFormData.append('username', username);
                    emailFormData.append('email', emailValue);
                    await fetch('/api/login/setEmail/', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: emailFormData,
                    });
                }
                await fetch(`/api/teams/initaizeUserAccountSetup/?username=${encodeURIComponent(username)}`, {
                    method: 'POST',
                });
                showMessage("Account created. Please login.", "success");
                setTimeout(() => {
                    window.location.href = "loginpage.html";
                }, 2000);
            }
            else {
                showMessage("Failed to create account. Please try again.", "error");
            }
        }
    }
    catch (error) {
        showMessage("An error occurred. Please try again.", "error");
    }
}
async function attemptLogin() {
    const userNameEle = document.getElementById("username");
    const passwordEle = document.getElementById("password");
    const userNameEleValue = userNameEle.value || null;
    const passwordEleValue = passwordEle.value || null;
    if (userNameEleValue === null || passwordEleValue === null) {
        showMessage("Username or password is null. Try again.", "error");
        return;
    }
    const username = userNameEleValue;
    const password = passwordEleValue;
    try {
        // Send credentials in request body, not URL (security best practice)
        const formData = new URLSearchParams();
        formData.append('username', username);
        formData.append('password', password);
        const attemptLoginRes = await fetch('/api/login/attemptLogin/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData,
        });
        const attemptLoginData = await attemptLoginRes.json();
        if (attemptLoginData) {
            const generateSessionIDRes = await fetch(`/api/login/generateSessionID/?username=${encodeURIComponent(username)}`, {
                method: 'GET',
            });
            const generateSessionIDData = await generateSessionIDRes.text();
            showMessage("Login successful.", "success");
            // Set cookies with Secure flag for HTTPS
            document.cookie = `username=${encodeURIComponent(username)}; path=/; SameSite=Strict`;
            document.cookie = `sessionID=${generateSessionIDData}; path=/; SameSite=Strict`;
            setTimeout(() => {
                window.location.href = "drafthomepage.html";
            }, 2000);
        }
        else {
            showMessage("Login failed. Try different credentials.", "error");
        }
    }
    catch (error) {
        showMessage("An error occurred. Please try again.", "error");
    }
}
