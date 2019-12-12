var stompClient = null;
var roomId = "";

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
    document.getElementById("welcome").innerHTML = "Welcome, " + localStorage.getItem("username");
    showAdminLink();
});

function webSocketSetup() {
    var socket = new SockJS('/ws-connect');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

webSocketSetup();

function onConnected() {
    stompClient.subscribe("/room/global/chat", onMessageReceived);
    stompClient.subscribe("/room/menu/roomChange", onRoomChange);
    stompClient.send("/app/room/global/chat", {}, JSON.stringify({
        username: "Server",
        message: "User " + localStorage.getItem("username") + " has joined."
    }));
    stompClient.send("/app/room/global/join", {}, JSON.stringify({username: localStorage.getItem("username")}));
}

function onError() {
    console.log("ERROR");
}


function sendMessage() {
    var chatMessage = {
        message: document.getElementById("chattext").value,
        username: localStorage.getItem("username"),
        roomId: roomId,
        countdown: localStorage.getItem("countdown")
    };
    stompClient.send("/app/room/global/chat", {}, JSON.stringify(chatMessage));
    document.getElementById("chattext").value = "";
}

function onRoomChange(payload) {
    getOpenRooms();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var p = document.createElement("p");
    p.innerHTML = message.username + ": " + message.message;
    p.id = "usermsg";
    document.getElementById("chatbox").appendChild(p);
    document.getElementById("chatbox").scrollTop = document.getElementById("chatbox").scrollHeight - 10;
}

var isChecked = false;
var pwCheckbox = document.getElementById("generatePassword");

function random_password_generate(max, min) {
    var passwordChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz?!";
    var randPwLen = Math.floor(Math.random() * (max - min + 1)) + min;
    var randPassword = Array(randPwLen).fill(passwordChars).map(function (x) {
        return x[Math.floor(Math.random() * x.length)]
    }).join('');
    return randPassword;
}

document.getElementById("generatePassword").addEventListener("click", function () {
    var random_password = random_password_generate(10, 5);
    if (document.getElementById('generateText').disabled == false) {
        document.getElementById("generateText").value = random_password;
    } else {
        document.getElementById("generateText").value = "";
    }
});

function getCategories() {
    $.ajax({
        url: "/categories",
        type: "GET",
        success: function (categories) {
            $.each(categories, function (index, category) {
                document.getElementById("librarySelection").innerHTML += "<option>" + category + "</option>";
            });
        },
        error: function (e) {
            document.getElementById("librarySelection").innerHTML = "<option>Error getting categories from table</option>";
        }
    });

}

$('#gameType').change(function () {
    if (document.getElementById("gameType").value === "Teams") {
        document.getElementById("librarySelection").innerHTML = "<option>All</option>";
        document.getElementById("maxNumOfPlayers").innerHTML = "<option>4</option>" +
            "<option>5</option>" +
            "<option>6</option>" +
            "<option>7</option>" +
            "<option>8</option>"
    } else {
        getCategories();
        document.getElementById("maxNumOfPlayers").innerHTML = "<option>2</option>\n" +
            "<option>3</option>" +
            "<option>4</option>" +
            "<option>5</option>" +
            "<option>6</option>" +
            "<option>7</option>" +
            "<option>8</option>"
    }
})

function clearCategories() {
    document.getElementById("librarySelection").innerHTML = "<option>All libraries</option>";
}

function getOpenRooms() {
    $.ajax({
        url: "/rooms/open",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            document.getElementById("roomCanvas").innerHTML = "";
            if (resp.length > 0) {
                resp.forEach(function (room, index) {
                    var usernames = "";
                    room.users.forEach(function (user) {
                        usernames += user.userListUsername + " ";
                    });
                    var row = document.createElement("li");
                    if (!room.gameStarted && room.users.length !== room.maxNumberOfPlayers) {
                        var link = document.createElement("a");
                        if (room.password !== "") {
                            link.innerHTML = "Room: " + room.roomName + ", Library: " + room.library + ", Max # of players: " + room.maxNumberOfPlayers + ", Current Players: " + usernames + "[" + "PASSWORD PROTECTED" + "]";
                        } else {
                            link.innerHTML = "Room: " + room.roomName + ", Library: " + room.library + ", Max # of players: " + room.maxNumberOfPlayers + ", Current Players: " + usernames;
                        }
                        link.href = "";
                        link.onclick = function () {
                            return false
                        };
                        row.appendChild(link);
                    } else {
                        if (room.password !== "") {
                            row.innerHTML = "Room: " + room.roomName + ", Library: " + room.library + ", Max # of players: " + room.maxNumberOfPlayers + ", Current Players: " + usernames + "[" + "PASSWORD PROTECTED" + "]";
                        } else {
                            row.innerHTML = "Room: " + room.roomName + ", Library: " + room.library + ", Max # of players: " + room.maxNumberOfPlayers + ", Current Players: " + usernames;
                        }
                    }
                    row.onclick = function () {
                        joinRoom(room)
                    };
                    document.getElementById("roomCanvas").appendChild(row);
                });

            } else {
                document.getElementById("roomCanvas").innerHTML = "No open rooms, create one to begin playing!";
            }
        },
        error: function (e) {
            document.getElementById("roomCanvas").innerHTML = "Error retrieving open rooms!";
        }

    });

}

getOpenRooms();

function joinRoom(room) {
    if (!room.gameStarted) {
        if (room.password !== "" && room.users.length !== room.maxNumberOfPlayers) {
            $('#passwordModal').modal("show");
            setInterval(function () {
                if (document.getElementById("passwordInput").value === room.password) {
                    document.getElementById("passwordInput").value = "";
                    if (room.users.length + 1 <= room.maxNumberOfPlayers) {
                        window.location = "/gameroom?room=" + room.roomId;
                    }
                }
            }, 10);
        } else {
            if (room.users.length + 1 <= room.maxNumberOfPlayers) {
                window.location = "/gameroom?room=" + room.roomId;
            }
        }
    }
}

function createRoom() {
    var library = document.getElementById("librarySelection").value;
    var password = document.getElementById("generateText").value;
    var roomName = document.getElementById("roomName").value;
    var maxNumOfPlayers = document.getElementById("maxNumOfPlayers").value;
    var gameType = document.getElementById("gameType").value;
    dataAnalysisMap.set("username", localStorage.getItem("username"));
    if (password !== "") {//if there is a password in the field increment it in the map
        dataAnalysisMap.set("passwordClicks", dataAnalysisMap.get("passwordClicks") + 1);
    }
    libraryTypeClicks.set(library.toLowerCase(), libraryTypeClicks.get(library.toLowerCase()) + 1);
    gameTypeClicks.set(gameType.toLowerCase(), gameTypeClicks.get(gameType.toLowerCase()) + 1);
    numberofPlayersClicks.set(parseInt(maxNumOfPlayers), numberofPlayersClicks.get(parseInt(maxNumOfPlayers)) + 1);
    $.ajax({
        url: "/room",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            username: localStorage.getItem("username"),
            library: library,
            password: password,
            roomName: roomName,
            maxNumberOfPlayers: maxNumOfPlayers,
            roomType: gameType
        }),
        success: function (resp) {
            localStorage.setItem("roomType", document.getElementById("gameType").value);
            window.location = "/gameroom?room=" + resp.roomId;
        },
        error: function (e) {
            document.getElementById("endedgames").innerHTML = "Error retrieving ended games!";
        }
    });
}

document.getElementById("gameStart").onclick = function () {
    createRoom();
};



