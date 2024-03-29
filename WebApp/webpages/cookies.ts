function getCookie(cname: string): string {
  let name = cname + "=";
  let decodedCookie = decodeURIComponent(document.cookie);
  let ca = decodedCookie.split(';');
  for(let i = 0; i <ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      console.log("Cookie found for cookie " + cname + " : " + c.substring(name.length, c.length))
      console.log(c.substring(name.length, c.length))
      return c.substring(name.length, c.length)
    }
  }
  console.log("No cookie found")
  return "";
}

function deleteCookie(cname: string): void {
  console.log(cname + " = " + getCookie(cname));
  console.log("Deleting cookie " + cname);
  document.cookie = cname + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
  console.log(cname + " = " + getCookie(cname));
}

function deleteAllCookies(): void {
  console.log(document.cookie);
  let cookies = document.cookie.split(";");
  console.log("Splitting cookies - " + cookies);

  let userName: string;

  for (let i = 0; i < cookies.length; i++) {
    if (cookies[i].split("=")[0] == "username") {
      userName = cookies[i].split("=")[1];
      alert("Logged out of " + userName + "'s account.");
    }
    console.log("Send this to Delete cookie -  " + cookies[i].split("=")[0]);
    deleteCookie(cookies[i].split("=")[0]);
  }
  window.location.href = "loginpage.html"
}