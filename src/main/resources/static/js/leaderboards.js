function getUsers(){
    $.ajax({
        url: "/getleaders",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            console.log(resp);
            var table =  document.getElementById("boardBody");
            if(resp.length > 0) {
                var selectIndex = 0;
                resp.forEach(function (account, index) {
                    console.log(account);
                    var rank =index+1;
                    table.innerHTML += "<tr id = 'index'>";
                    var x = document.getElementById("index");
                    x.setAttribute("id","index" + selectIndex++);
                    x.innerHTML += "<td>" + rank +"</td>";
                    x.innerHTML += "<td>" + account.username + "</td>";
                    x.innerHTML += "<td>" + account.points + "</td>";
                    x.innerHTML += "</tr>";
                    if(index === resp.length-1)
                    {
                        $('#leaderboard').DataTable();
                    }
                })

            } else {
                document.getElementById("users").innerHTML = "No Users";
            }
        },
        error: function (e) {
            document.getElementById("users").innerHTML = "Error retrieving Users";
        }
    });
}
getUsers();


function getUser(){
    $.ajax({
        url: "/getUser?username=" + localStorage.getItem("username"),
        type: "GET",
        success: function (resp) {
            if(resp.length !== null) {
              document.getElementById("personalTotalPoints").innerHTML = resp.points;
              document.getElementById("personalTotalWins").innerHTML = resp.gamesWon;
                document.getElementById("personalTotalGames").innerHTML = resp.gamesPlayed;
            } else {
                document.getElementById("users").innerHTML = "No Users";
            }
        },
        error: function (e) {
            document.getElementById("users").innerHTML = "Error retrieving Users";
        }
    });
}
getUser();

function getAwards()
{
    $.ajax({
        url: "/getleaders",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            if(resp.length > 0) {
                var mostPoints = resp[0];
                var mostGames = resp[0];
                var mostWins = resp[0];
                var bestPercentage = resp[0];
                var mostPPG = resp[0];


                resp.forEach(function (account, index){
                    if(account.points > mostPoints.points)
                        mostPoints = account;
                    if(account.gamesPlayed >  mostGames.gamesPlayed)
                        mostGames = account;
                    if((account.gamesWon/account.gamesPlayed) > (bestPercentage.gamesWon/bestPercentage.gamesPlayed))
                        bestPercentage = account;
                    if(account.gamesWon > mostPoints.gamesWon)
                        mostWins = account;
                    if((account.points/account.gamesPlayed) > (mostPPG.points/mostPPG.gamesPlayed))
                        mostPPG=account;
                })
                var row = document.createElement("li");
                row.innerHTML = "Most Points:" + " " + mostPoints.username + " : " + mostPoints.points + " Points";
                row.setAttribute("class","list-group-item d-flex justify-content-between align-items-center");
                document.getElementById("Awards").appendChild(row);

                var row = document.createElement("li");
                row.setAttribute("class","list-group-item d-flex justify-content-between align-items-center");
                row.innerHTML = "Most Games Played: " + mostGames.username + " : " + mostGames.gamesPlayed + " Games Played";
                document.getElementById("Awards").appendChild(row);

                var row = document.createElement("li");
                row.setAttribute("class","list-group-item d-flex justify-content-between align-items-center");
                row.innerHTML = "Most Games Won: " + mostWins.username + " : " + mostWins.gamesWon + " Victories";
                document.getElementById("Awards").appendChild(row);

                var row = document.createElement("li");
                row.setAttribute("class","list-group-item d-flex justify-content-between align-items-center");
                row.innerHTML = "Best win Percentage: " + bestPercentage.username + " : " + 100*(bestPercentage.gamesWon/bestPercentage.gamesPlayed) + "%";
                document.getElementById("Awards").appendChild(row);

                var row = document.createElement("li");
                row.setAttribute("class","list-group-item d-flex justify-content-between align-items-center");
                row.innerHTML = "Most Points Earned Per Game: " + mostPPG.username + " : " + (mostPPG.points/mostPPG.gamesPlayed) + " Points Per Game";
                document.getElementById("Awards").appendChild(row);
            } else {
                document.getElementById("Awards").innerHTML = "No Users";
            }
        },
        error: function (e) {
            document.getElementById("users").innerHTML = "Error retrieving Users";
        }
    });
}

function showAdminLink() {
    if (localStorage.getItem("username").substr(0, 5).toLowerCase() === "guest" || localStorage.getItem("username") === null) {
        document.getElementById("navbardrop").style.display = "none";
    } else {
        $.ajax({
            url: "/isAdmin?username=" + localStorage.getItem("username"),
            type: "GET",
            success: function (resp) {
                if (resp === false) {
                    document.getElementById("navbardrop").style.display = "none";
                }
            },
            error: function (e) {
                document.getElementById("navbardrop").style.display = "none";
            }
        });
    }
}


$(document).ready(function () {
    getAwards();
    showAdminLink();
});




