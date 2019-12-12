
function loginValidation(){
    $.ajax({
        url: "/login",
        type: "POST",
        contentType : "application/json; charset=utf-8",
        data : JSON.stringify({
           username : document.getElementById("usernameInput").value,
           password : document.getElementById("passwordInput").value
        }),
        success: function (resp) {
            if(resp.banned == true)
            {
                document.getElementById("loginErrorDiv").innerHTML = "<h2>This account has been banned</h2>";
            }
            else {
                dataAnalysisMap.set("loginClicks", dataAnalysisMap.get("loginClicks") + 1);
                localStorage.setItem("username", document.getElementById("usernameInput").value);
                window.location = '/mainmenu';
            }
        },
        error: function () {
            document.getElementById("loginErrorDiv").innerHTML = "<h2>The username or password is not correct. Please try again.</h2>";
        }
    });
}

function registrationSubmit(){
    $.ajax({
        url: "/register",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            username: document.getElementById("usernameInput").value,
            firstName: document.getElementById("firstNameInput").value,
            lastName: document.getElementById("lastNameInput").value,
            password: document.getElementById("passwordInput").value
        }),
        success: function () {
            window.location = '/';
        },
        error: function (e) {
            let errorMsg = e.responseText;
            document.getElementById("registerErrorDiv").innerHTML = "<h2>" + errorMsg + "</h2>";

        }
    });

}

function createGuestName() {
    var name = "Guest";
    for(var i = 0; i<5 ; i++){
        name += Math.floor(Math.random() * (9));
    }
    dataAnalysisMap.set("guestLoginClicks", dataAnalysisMap.get("guestLoginClicks") + 1);
    localStorage.setItem("username", name);
    window.location='/mainmenu';
}

$(document).ready(function () {
    if(document.title === "Sketch.it"){
       document.getElementById("usernameLocationForTests").innerText = localStorage.getItem("username");
    }
});



