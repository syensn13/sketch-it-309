var stompClient = null;
var urlParams = new URLSearchParams(window.location.search);
var roomId = urlParams.get("room");
var roomType;
var canvy;
localStorage.setItem("team" ,"0"); //set team to 0 by default

function test() {
    var socket = new SockJS('/ws-connect');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);

}
test();

function onConnected() {
    stompClient.subscribe("/room/" + roomId + "/chat", onMessageReceived);
    stompClient.subscribe("/room/" + roomId + "/roundEnd", onRoundEnd);
    stompClient.subscribe("/room/" + roomId + "/gameEnd", onGameEnd);
    stompClient.subscribe("/room/" + roomId + "/startGame", onStartGame);
    stompClient.subscribe("/room/" + roomId + "/countdown", onCountDown);
    stompClient.subscribe("/room/" + roomId + "/canvas", onCanvasChange);
    stompClient.send("/app/room/" + roomId + "/chat", {}, JSON.stringify({
        username: localStorage.getItem("username"),
        roomId: roomId,
        messageType: "join",
        message: "User " + localStorage.getItem("username") + " has joined.",
        team: localStorage.getItem("team")
    }));
    stompClient.send("/app/room/" + roomId + "/join", {}, JSON.stringify({username: localStorage.getItem("username")}));
    getRoomType();
    getUsers();
}

function onError() {
    console.log("ERROR");
}

function sendMessage() {
    var chatMessage = {
        message: document.getElementById("chattext").value,
        username: localStorage.getItem("username"),
        roomId: roomId,
        messageType: "message",
        team : localStorage.getItem("team")
    };
    stompClient.send("/app/room/" + roomId + "/chat", {}, JSON.stringify(chatMessage));
    document.getElementById("chattext").value = "";
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    if(message.team === localStorage.getItem("team")) {
        var p = document.createElement("p");
        if (message.messageType === "message") {
            p.innerHTML = message.username + ": " + message.message;
        } else {
            p.innerHTML = message.message;
            getUsers();
        }
        p.id = "usermsg";
        document.getElementById("chatbox").appendChild(p);
        document.getElementById("chatbox").scrollTop = document.getElementById("chatbox").scrollHeight - 10;
    }
}

function voteStart() {
    stompClient.send("/app/room/" + roomId + "/voteStart", {}, JSON.stringify({username: localStorage.getItem("username"), gameType : roomType}));
    document.getElementById("voteStart").hidden = true;
}

function onStartGame(payload) {
    var playerRole;
    var playerTeam;
    payload.body = JSON.parse(payload.body);
    console.log(payload.body.length);
    if(payload.body.users !== undefined) {
        playerRole = payload.body.users[payload.body.users.map(function (i) {
            return i.userListUsername
        }).indexOf(localStorage.getItem("username"))].userType;
        localStorage.setItem("playerRole", playerRole);
        playerTeam = payload.body.users[payload.body.users.map(function (i) {
            return i.userListUsername
        }).indexOf(localStorage.getItem("username"))].team;
        if(roomType === "Classic") {
            document.getElementById("gameStatus").innerHTML = "Game Started! User is a " + playerRole;
        } else {
            document.getElementById("gameStatus").innerHTML = "Game Started! User is on team:" + playerTeam + " and is a " + playerRole;
        }
        localStorage.setItem("team",playerTeam);
        if (playerRole === "drawer") {
            //getWordChoices();
            stompClient.subscribe("/room/" + roomId + "/wordChoice", onWordChoice);
        }
    }
    localStorage.setItem("round", payload.body.numRounds);
}

function onWordChoice(payload) {
    document.getElementById("wordChoicesDiv").innerHTML = "The word to draw is: " + payload.body;
}

function compare(a, b) {
    var comparison = 0;
    if (a.points > b.points) {
        comparison = 1;
    } else if (a.points < b.points) {
        comparison = -1;
    }
    return comparison * -1;
}

function onRoundEnd(payload) {
    stompClient.unsubscribe("/room/" + roomId + "/wordChoice");
    payload.body = JSON.parse(payload.body);
    document.getElementById("usersList").innerHTML = "";
    document.getElementById("afterRoundPoints").innerHTML = "";
    payload.body.users.forEach(function(user) {
        var li = document.createElement("li");
        li.innerHTML = user.userListUsername + ", Points: " + user.points;
        document.getElementById("usersList").appendChild(li);
    });
    payload.body.rounds[payload.body.roundNum].forEach(function(user) {
        var li = document.createElement("li");
        li.innerHTML = user.roundUsername + ", Points: " + user.points;
        document.getElementById("afterRoundPoints").appendChild(li);
    });
    document.getElementById("wordChoicesDiv").innerHTML = "";
    document.getElementById("gameStatus").innerHTML = "Round ended, waiting for next to start!";
    document.getElementById("pointsModalLabel").innerHTML = "Round " + (payload.body.roundNum + 1) + " Points";
    $('#pointsModal').modal("show");
    setTimeout(function() {
        $('#pointsModal').modal("hide");
    }, 5000);
}

function onGameEnd(payload) {
    payload.body = JSON.parse(payload.body);
    payload.body.users.sort(compare);
    payload.body.users.forEach(function(user, i) {
        var li = document.createElement("li");
        li.innerHTML = (i+1) + ": " + user.userListUsername + ", Points: " + user.points;
        document.getElementById("gameOverPoints").appendChild(li);
    });
    document.getElementById("wordChoicesDiv").innerHTML = "";
    document.getElementById("gameStatus").innerHTML = "Game Over!";
    $('#gameOverModal').modal("show");
}

function onCountDown(payload) {
    var countdown = JSON.parse(payload.body);
    if (countdown.countdown === "beforeRound" && countdown.time >= 0) {
        document.getElementById("countdown").innerHTML = "Round will begin in: " + countdown.time;
    } else if(countdown.countdown === "round" && countdown.time >= 0) {
        document.getElementById("countdown").innerHTML = "Time left in round: " + countdown.time;
        if(countdown.time <= 0) {
            document.getElementById("countdown").innerHTML = "";
        }
    }
}

function sendCanvasData() {//if playing classic there is only a team 0 and everyone is technically on it
    pointArray.push([-1, parseInt(localStorage.getItem("team"),10)]);
    console.log(JSON.stringify(pointArray));
    stompClient.send("/app/room/" + roomId + "/canvas", {}, JSON.stringify(pointArray));
    pointArray = [];
}

function onCanvasChange(payload) {
    var points = JSON.parse(payload.body);
    var pointTuples = points.body;
    if(pointTuples[pointTuples.length - 1][1] === parseInt(localStorage.getItem("team"),10)) { //only show stuff from the same team
        pointTuples.pop(); //remove -1 element, since it doesnt make sense to draw.
        for (var i = 0; i < pointTuples.length; i++) {
            if (i > 0) {
                drawPixel(pointTuples[i][0], pointTuples[i][1], pointTuples[i][2], pointTuples[i][3], pointTuples[i][4], 0);
                drawLineToPrevPoint(pointTuples[i], pointTuples[i - 1], pointTuples[i][2], pointTuples[i][3], pointTuples[i][4]);
            } else {
                drawPixel(pointTuples[i][0], pointTuples[i][1], 0, 0, 0, 0);
            }
        }
    }
}

/*function chooseWord(button) {
    $.ajax({
        url: "/room/game/wordToGuess?roomId=" + roomId + "&wordToGuess=" + button.innerText + "&team=" + localStorage.getItem("team"),
        type: "POST",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            document.getElementById("wordChoicesDiv").innerHTML = "The word to draw is: " + button.innerText;
            if(localStorage.getItem("team") === "1"){
                team1Choose = true;
                stompClient.send("/app/room/" + roomId + "/teamChoose", {}, JSON.stringify({team: 1}));
            }
            if(localStorage.getItem("team") === "2"){
                team2Choose = true;
                stompClient.send("/app/room/" + roomId + "/teamChoose", {}, JSON.stringify({team: 2}));
            }
        },
        error: function (e) {
            console.log(e);
        }
    });
}*/

/*function getWordChoices() {
    $.ajax({
        url: "/room/library/wordChoices?roomId=" + roomId,
        type: "POST",
        success: function (wordChoices) {
            document.getElementById("wordChoicesDiv").innerHTML = "Choose a word to draw: ";
            $.each(wordChoices, function (index, word) {
                document.getElementById("wordChoicesDiv").innerHTML += "<button onclick='chooseWord(this)'>" + word + "</button>";
            });
        },
        error: function (e) {

        }
    });
}*/

function getUsers() {
    $.ajax({
        url: "/room/getUsersInRoom?roomId=" + roomId,
        type: "GET",
        success: function (users) {
            console.log(users);
            document.getElementById("usersList").innerHTML = "";
            users.forEach(function(user) {
                var li = document.createElement("li");
                li.innerHTML = user.userListUsername + ", Points: " + user.points;
                document.getElementById("usersList").appendChild(li);
            })
        },
        error: function (e) {
            console.log(e);
        }
    });
}

function getRoomType() {
    $.ajax({
        url: "/room/roomType?roomId=" + roomId,
        type: "GET",
        success: function (roomTypeReturn) {
            console.log(roomTypeReturn);
            roomType = roomTypeReturn;
            document.getElementById("roomGameType").innerText = roomType;
        },
        error: function (e) {

        }
    });
}

/**
 * Retrieves users from database and put them in a drop down menu for reporting
 */
function getUsersForReport() {
    $.ajax({
        url: "/room/getUsersInRoom?roomId=" + roomId,
        type: "GET",
        success: function (accounts) {
            $.each(accounts, function (index, account) {
                document.getElementById("userSelection").innerHTML += "<option>" + account.userListUsername + "</option>";
            });
        },
        error: function (e) {
            document.getElementById("userSelection").innerHTML = "<option>Error getting users from table</option>";
        }
    });

}

function clearUsersForReport() {
    document.getElementById("userSelection").innerHTML = "<option></option>";
}

function sendReport() {
    $.ajax({
        url: "/room/sendReport",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            "username": document.getElementById("userSelection").value,
        }),
        success: function (resp) {
            window.location = "/gameroom?room=" + roomId;
        },
        error: function (e) {
            document.getElementById("exampleModal").innerHTML = "Error sending report!";
        }
    });
}


$(document).ready(function () {
    //document.getElementById("usernameLocationForTests").innerText = localStorage.getItem("username");
    document.getElementById("welcome").innerHTML = "Welcome, " + localStorage.getItem("username");
});

