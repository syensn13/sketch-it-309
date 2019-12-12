/**
 * Retrieves and displays
 */
function getUsers(){
    $.ajax({
        url: "/retrieveusers",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            var table = document.getElementById("boardBody")
            if(resp.length > 0) {
                resp.forEach(function (account, index) {
                    console.log(account);
                    table.innerHTML += "<tr id = 'index'>";
                    var x = document.getElementById("index");
                    x.setAttribute("id","index" + index);
                    x.innerHTML += "<td>" + account.username + "</td>";
                    x.innerHTML += "<td>" + account.firstName +"</td>";
                    x.innerHTML += "<td>" + account.lastName + "</td>";
                    x.innerHTML += "<td>" + account.banned +"</td>";
                    x.innerHTML += "</tr>";
                    if(index === resp.length-1)
                    {
                        $('#userBoard').DataTable();
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

function getReports(){
    $.ajax({
        url: "/retrievereports",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            if(resp.length > 0) {
              var x = document.getElementById("reports");
              x.innerHTML += "There are " + resp.length + " reports";
            }else {
                document.getElementById("reports").innerHTML = "No Users";
            }
        },
        error: function (e) {
            document.getElementById("reports").innerHTML = "Error retrieving Reports";
        }
    });
}
getReports();

function search(){
    $.ajax({
        url: "/retrievesingleuser",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify( {
            username: document.getElementById("searchedUser").value}),
        success: function (resp) {
            document.getElementById("Usersearch").innerHTML = "";
            if(typeof resp.username !== null) {
                var row = document.createElement("lo");
                row.innerHTML ="user " + resp.username + " has " + resp.reportNumbers + " reports";
                row.setAttribute("class","list-group-item d-flex justify-content-between align-items-center");
                document.getElementById("Usersearch").appendChild(row);
            } else{
                document.getElementById("Usersearch").innerHTML = "No Users";
            }
        },
        error: function (e) {
            document.getElementById("Usersearch").innerHTML = "Error retrieving Users";
        }
    });
}
search();

function ban()
{
    $.ajax({
        url: "/ban",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            username: document.getElementById("searchedUser").value,
        }),
        success: function (resp) {
            document.getElementById("Usersearch").innerHTML = "";
            var row = document.createElement("lo");
            row.innerHTML ="user " + resp.username + " has been banned";
            document.getElementById("Usersearch").appendChild(row);
        },
        error: function (e) {
            document.getElementById("Usersearch").innerHTML = "User not found";
        }
    });
}
ban();

function unban()
{
    $.ajax({
        url: "/unban",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            username: document.getElementById("searchedUser").value,
        }),
        success: function (resp) {
            document.getElementById("Usersearch").innerHTML = "";
            var row = document.createElement("lo");
            row.innerHTML ="user " + resp.username + " has been unbanned";
            document.getElementById("Usersearch").appendChild(row);
        },
        error: function (e) {
            document.getElementById("Usersearch").innerHTML = "User not found";
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
    unban();
    showAdminLink();
});

