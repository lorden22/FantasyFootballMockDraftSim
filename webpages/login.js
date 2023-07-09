async function addUser() {
    var username = document.getElementsByName("username")[0].value
    var password = document.getElementsByName("password")[0].value


    var checkUserRes = await 
    fetch("http://localhost:8080/api/login/checkUser/?username="+username, {
        method: 'GET',
    })
    var checkUserData = await checkUserRes.json()

    if (checkUserData == true)
        alert("Username already taken. Try again.")
    else {
        var addUser = await 
        fetch("http://localhost:8080/api/login/addUser/?username="+username+"&password="+password, {
            method: 'PUT',
        })
        var addUserRes = await addUser.json()
        
        var setUpUserRes = await fetch("http://localhost:8080/api/teams/initaizeUserAccountSetup/?username="+username, {
            method: 'POST',
        })

        var setUpUserData = await setUpUserRes.json()

        console.log(addUserRes);
        console.log(setUpUserData);

        alert("Account created. Please login.")
        window.location.href = "loginpage.html"
    }
}

async function attemptLogin() {
    var username = document.getElementsByName("username")[0].value
    var password = document.getElementsByName("password")[0].value

    var attemptLoginRes = await
    fetch("http://localhost:8080/api/login/attemptLogin/?username="+username+"&password="+password, {
        method: 'GET',
    })

    var attemptLoginData = await attemptLoginRes.json()

    if (attemptLoginData == true) {
        alert("Login successful.")
        document.cookie = "username="+username + "; path=/";
        window.location.href = "drafthomepage.html"
    }
    else
        alert("Login failed. Try different credentials.")
}