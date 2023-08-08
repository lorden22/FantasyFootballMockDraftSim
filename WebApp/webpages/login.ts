async function addUser(): Promise<void> {
    let userNameEle: HTMLInputElement | null = document.getElementById("username") as HTMLInputElement | null;
    let passwordEle: HTMLInputElement | null = document.getElementById("password") as HTMLInputElement | null;

    if(userNameEle == null || passwordEle == null) {
        alert("Username or password is null. Try again.")
        return;
    }

    let username: string = userNameEle.value;
    let password: string = passwordEle.value;

    let checkUserRes = await 
    fetch("http://localhost:80/api/login/checkUser/?username="+username, {
        method: 'GET',
    })
    let checkUserData: Boolean = await checkUserRes.json()

    if (checkUserData == true)
        alert("Username already taken. Try again.")
    else {
        let addUser = await 
        fetch("http://localhost:80/api/login/addUser/?username="+username+"&password="+password, {
            method: 'PUT',
        })
        let addUserRes: Response = await addUser.json()
        
        let setUpUserRes = await fetch("http://localhost:80/api/teams/initaizeUserAccountSetup/?username="+username, {
            method: 'POST',
        })

        let setUpUserData : Response = await setUpUserRes.json()

        console.log(addUserRes);
        console.log(setUpUserData);

        alert("Account created. Please login.")
        window.location.href = "loginpage.html"
    }
}

async function attemptLogin() {
    let userNameEle: HTMLInputElement | null = document.getElementById("username") as HTMLInputElement | null;
    let passwordEle: HTMLInputElement | null = document.getElementById("password") as HTMLInputElement | null;

    if(userNameEle == null || passwordEle == null) {
        alert("Username or password is null. Try again.")
        return;
    }

    let username: string = userNameEle.value;
    let password: string = passwordEle.value;

    let attemptLoginRes = await
    fetch("http://localhost:80/api/login/attemptLogin/?username="+username+"&password="+password, {
        method: 'GET',
    })

    let attemptLoginData = await attemptLoginRes.json()

    if (attemptLoginData == true) {

        let generateSessionID = await fetch("http://localhost:80/api/login/generateSessionID/?username="+username, {
            method: 'GET',
        })

        console.log(generateSessionID)
        let generateSessionIDData = await generateSessionID.text()


        console.log(generateSessionIDData)
        alert("Login successful.")
        document.cookie = "username="+username + "; path=/";
        document.cookie = "sessionID="+generateSessionIDData + "; path=/";
        window.location.href = "drafthomepage.html"
    }
    else
        alert("Login failed. Try different credentials.")
}