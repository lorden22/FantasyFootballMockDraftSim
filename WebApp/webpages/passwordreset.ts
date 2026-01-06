// Password Reset functionality

// Declare function from main.js
declare function showMessage(message: string, type: 'error' | 'success'): void;

async function requestPasswordReset(): Promise<void> {
    const emailEle = document.getElementById("email") as HTMLInputElement;
    const email = emailEle?.value?.trim();

    if (!email) {
        showMessage("Please enter your email address.", "error");
        return;
    }

    // Basic email validation
    const emailPattern = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
    if (!emailPattern.test(email)) {
        showMessage("Please enter a valid email address.", "error");
        return;
    }

    try {
        const formData = new URLSearchParams();
        formData.append('email', email);

        const response = await fetch('/api/login/requestPasswordReset/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData,
        });

        if (response.status === 429) {
            showMessage("Too many password reset requests. Please try again later.", "error");
            return;
        }

        if (response.ok) {
            showMessage("If an account with that email exists, a password reset link has been sent.", "success");
            // Clear the email field
            emailEle.value = '';
        } else {
            showMessage("An error occurred. Please try again.", "error");
        }
    } catch (error) {
        showMessage("An error occurred. Please try again.", "error");
    }
}

function getTokenFromUrl(): string | null {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('token');
}

async function validateToken(): Promise<void> {
    const token = getTokenFromUrl();

    if (!token) {
        showMessage("Invalid or missing reset token. Please request a new password reset.", "error");
        disableResetForm();
        return;
    }

    // Validate token format (64 hex characters)
    if (!/^[a-f0-9]{64}$/.test(token)) {
        showMessage("Invalid token format. Please request a new password reset.", "error");
        disableResetForm();
        return;
    }

    try {
        const response = await fetch(`/api/login/validateResetToken/?token=${encodeURIComponent(token)}`, {
            method: 'GET',
        });

        const isValid = await response.json();

        if (!isValid) {
            showMessage("This password reset link has expired or is invalid. Please request a new one.", "error");
            disableResetForm();
        }
    } catch (error) {
        showMessage("An error occurred validating the reset link. Please try again.", "error");
        disableResetForm();
    }
}

function disableResetForm(): void {
    const passwordInput = document.getElementById("password") as HTMLInputElement;
    const confirmPasswordInput = document.getElementById("confirmPassword") as HTMLInputElement;
    const submitButton = document.querySelector("button[onclick='resetPassword()']") as HTMLButtonElement;

    if (passwordInput) passwordInput.disabled = true;
    if (confirmPasswordInput) confirmPasswordInput.disabled = true;
    if (submitButton) submitButton.disabled = true;
}

async function resetPassword(): Promise<void> {
    const passwordEle = document.getElementById("password") as HTMLInputElement;
    const confirmPasswordEle = document.getElementById("confirmPassword") as HTMLInputElement;
    const password = passwordEle?.value;
    const confirmPassword = confirmPasswordEle?.value;
    const token = getTokenFromUrl();

    if (!token) {
        showMessage("Invalid reset token. Please request a new password reset.", "error");
        return;
    }

    if (!password || !confirmPassword) {
        showMessage("Please fill in all fields.", "error");
        return;
    }

    if (password.length < 8) {
        showMessage("Password must be at least 8 characters long.", "error");
        return;
    }

    if (password.length > 100) {
        showMessage("Password must be 100 characters or less.", "error");
        return;
    }

    if (password !== confirmPassword) {
        showMessage("Passwords do not match.", "error");
        return;
    }

    // Check for control characters
    for (const char of password) {
        if (char.charCodeAt(0) < 32) {
            showMessage("Password contains invalid characters.", "error");
            return;
        }
    }

    try {
        const formData = new URLSearchParams();
        formData.append('token', token);
        formData.append('password', password);

        const response = await fetch('/api/login/resetPassword/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData,
        });

        if (response.ok) {
            const result = await response.json();
            if (result) {
                showMessage("Password reset successful! Redirecting to login...", "success");
                setTimeout(() => {
                    window.location.href = "loginpage.html";
                }, 2000);
            } else {
                showMessage("Password reset failed. The link may have expired.", "error");
            }
        } else {
            const errorData = await response.json().catch(() => ({}));
            showMessage(errorData.message || "An error occurred. Please try again.", "error");
        }
    } catch (error) {
        showMessage("An error occurred. Please try again.", "error");
    }
}
