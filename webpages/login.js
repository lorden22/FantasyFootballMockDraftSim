async function addUser() {
    let username = document.getElementsByName("username")[0].value
    let password = document.getElementsByName("password")[0].value


    let checkUserRes = await 
    fetch("http://localhost:8080/api/login/checkUser/?username="+username, {
        method: 'GET',
    })
    let checkUserData = await checkUserRes.json()

    if (checkUserData == true)
        alert("Username already taken. Try again.")
    else {
        let addUser = await 
        fetch("http://localhost:8080/api/login/addUser/?username="+username+"&password="+password, {
            method: 'PUT',
        })
        let addUserRes = await addUser.json()
        
        let setUpUserRes = await fetch("http://localhost:8080/api/teams/initaizeUserAccountSetup/?username="+username, {
            method: 'POST',
        })

        let setUpUserData = await setUpUserRes.json()

        console.log(addUserRes);
        console.log(setUpUserData);

        alert("Account created. Please login.")
        window.location.href = "loginpage.html"
    }
}

async function attemptLogin() {
    let username = document.getElementsByName("username")[0].value
    let password = document.getElementsByName("password")[0].value

    let attemptLoginRes = await
    fetch("http://localhost:8080/api/login/attemptLogin/?username="+username+"&password="+password, {
        method: 'GET',
    })

    let attemptLoginData = await attemptLoginRes.json()

    if (attemptLoginData == true) {

        let generateSessionID = await fetch("http://localhost:8080/api/login/generateSessionID/?username="+username, {
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