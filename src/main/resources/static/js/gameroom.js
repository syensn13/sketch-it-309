var stompClient = null;
var urlParams = new URLSearchParams(window.location.search);
var roomId = urlParams.get("room");
var roomType;
var canvy;
var intermission = 1;
var canvO;
var canvOData;
var ctx;
var mayhemLocal = [];
var gameStarted = false;
localStorage.setItem("team" ,"0"); //set team to 0 by default

function webSocketSetup() {
    var socket = new SockJS('/ws-connect');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}
webSocketSetup();

function onConnected() {
    stompClient.subscribe("/room/" + roomId + "/chat", onMessageReceived);
    stompClient.subscribe("/room/" + roomId + "/roundEnd", onRoundEnd);
    stompClient.subscribe("/room/" + roomId + "/gameEnd", onGameEnd);
    stompClient.subscribe("/room/" + roomId + "/startGame", onStartGame);
    stompClient.subscribe("/room/" + roomId + "/countdown", onCountDown);
    stompClient.subscribe("/room/" + roomId + "/canvas", onCanvasChange);
    stompClient.subscribe("/room/" + roomId + "/clearCanvas", clearCanvas);
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

$('#myCanvas').mouseup(function (e) {
    if(gameStarted !== undefined && gameStarted === true && localStorage.getItem("playerRole") === "drawer") {
        sendCanvasData();
        visionApi();
    }
    painting = false;
});

function visionApi() {
    if(gameStarted !== undefined && gameStarted === true) {
        var image = canvas.toDataURL();
        var b = JSON.stringify({
            "requests": [
                {
                    "image": {
                        "content": image.slice(22, image.length)
                    },
                    "features": [
                        {
                            "type": "DOCUMENT_TEXT_DETECTION",
                            "model": "builtin/latest"
                        }
                    ],
                    "imageContext": {
                        "languageHints" : ["en-t-i0-handwrit"]
                    }
                }
            ]
        });
        var e = new XMLHttpRequest;
        e.onload = function () {
            var response = JSON.parse(e.responseText);
            response.responses[0].fullTextAnnotation.pages[0].blocks.forEach(function(word) {
                var letter_arr = [];
                word.paragraphs.forEach(function(words) {
                    words.words.forEach(function(letters) {
                        letters.symbols.forEach(function(letter){
                            if(letter.confidence >= 0.5) {
                                letter_arr.push(letter.text);
                            }
                            if (fiftypercent(localStorage.getItem("wordToDraw"), letter_arr)) {
                                clearCanvas();
                                stompClient.send("/app/room/" + roomId + "/clearCanvas", {}, {});
                            }
                        });
                    });
                });
            });
        };
        e.open("POST", "https://vision.googleapis.com/v1p4beta1/images:annotate?key=AIzaSyDXaOXstAh1CYhMQPzKy42tBz9qpJVI6wU", !0);
        e.send(b);
    }
}

function fiftypercent(word, letters) {
    var fiftypercent = false;
    var sum = 0;
    letters.forEach(function(letter) {
        if(word.indexOf(letter) > -1) {
            sum += 1;
        }
    });
    if(sum > word.length/2) {
        fiftypercent = true;
    }
    return fiftypercent;
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
    if(message.team === localStorage.getItem("team") || intermission === 1) {
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
    payload.body = JSON.parse(payload.body);
    gameStarted = true;
    var context = document.getElementById('myCanvas').getContext("2d");
    context.clearRect(0, 0, context.canvas.width, context.canvas.height);
    pointArray = [];
    var playerRole;
    var playerTeam;
    document.getElementById("otherCanvas").style.display = "none";
    if(payload.body.users !== undefined) {
        playerRole = payload.body.users[payload.body.users.map(function (i) {
            return i.userListUsername
        }).indexOf(localStorage.getItem("username"))].userType;
        localStorage.setItem("playerRole", playerRole);
        playerTeam = payload.body.users[payload.body.users.map(function (i) {
            return i.userListUsername
        }).indexOf(localStorage.getItem("username"))].team;
        if(roomType === "Classic" || roomType === "Mayhem") {
            document.getElementById("gameStatus").innerHTML = "Game Started! User is a " + playerRole;
        } else {
            document.getElementById("gameStatus").innerHTML = "Game Started! User is on team:" + playerTeam + " and is a " + playerRole;
        }
        localStorage.setItem("team",playerTeam);
        if (playerRole === "drawer") {
            document.getElementById("selectorButtons").hidden = false;
            if(roomType === "Mayhem") {
                for (var i = 0; i < Math.floor(Math.random() * (10-5) + 5); i++) {
                    mayhemLocal.push({
                        x: Math.floor(Math.random() * 600),
                        y: Math.floor(Math.random() * 600),
                        r: Math.floor(Math.random() * (100 - 25) + 25)
                    });
                }
            }
            if (playerTeam === 0) {
                getWordChoices();
            }
            stompClient.subscribe("/room/" + roomId + "/wordChoice", onWordChoice);
        } else {
            document.getElementById("selectorButtons").hidden = true;
        }
    }
    localStorage.setItem("round", payload.body.numRounds);
    intermission = 0;
}

function onWordChoice(payload) {
    if(localStorage.getItem("playerRole") === "drawer") {
        document.getElementById("wordChoicesDiv").innerHTML = "The word to draw is: " + payload.body;
        localStorage.setItem("wordToDraw", payload.body);
    }
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
    clearCanvas();
    intermission = 1;
    mayhemLocal = [];
    localStorage.setItem("wordToDraw", "");
    if(roomType === "Teams") {
        document.getElementById("otherCanvas").style.display = "block";
    }
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
    gameStarted = false;
    payload.body = JSON.parse(payload.body);
    payload.body.users.sort(compare);
    payload.body.users.forEach(function(user, i) {
        var li = document.createElement("li");
        li.innerHTML = (i+1) + ": " + user.userListUsername + ", Points: " + user.points;
        document.getElementById("gameOverPoints").appendChild(li);
    });
    updateAccounts();
    document.getElementById("wordChoicesDiv").innerHTML = "";
    document.getElementById("gameStatus").innerHTML = "Game Over!";
    $('#gameOverModal').modal("show");
}

function updateAccounts() {
    $.ajax({
        url: "/room/updateValues?roomId=" + roomId,
        type: "GET",
        success: function(){},
        error: function (e) {}
    });
}

function onCountDown(payload) {
    var countdown = JSON.parse(payload.body);
    if (countdown.countdown === "beforeRound" && countdown.time >= 0) {
        if(localStorage.getItem("playerRole") === "drawer") {
            document.getElementById("chattext").disabled = true;
            document.getElementById("submitmsg").disabled = true;
        }
        document.getElementById("countdown").innerHTML = "Round will begin in: " + countdown.time;
    } else if(countdown.countdown === "round" && countdown.time >= 0) {
        if(localStorage.getItem("playerRole") === "drawer") {
            document.getElementById("disableCanvas").hidden = true;
        }
        document.getElementById("countdown").innerHTML = "Time left in round: " + countdown.time;
        if(countdown.time <= 0) {
            if(localStorage.getItem("playerRole") === "drawer") {
                document.getElementById("chattext").disabled = false;
                document.getElementById("submitmsg").disabled = false;
                document.getElementById("disableCanvas").hidden = false;
            }
            document.getElementById("countdown").innerHTML = "";
        }
    }
}

function sendCanvasData() {//if playing classic there is only a team 0 and everyone is technically on it
    pointArray.push([-1, parseInt(localStorage.getItem("team"),10)]);
    stompClient.send("/app/room/" + roomId + "/canvas", {}, JSON.stringify({pointArray: pointArray, mayhem: mayhemLocal}));
    pointArray = [];
}

function clearCanvas() {
    contxt.putImageData(initalData,0,0);
    canvData  = contxt.getImageData(0,0,canvas.width,canvas.height);
    clickX = [];
    clickY = [];
    clickDrag = [];
    clickedColor = [];
    clickedColor.push(currentColor);
    pointArray = [];
}

function onCanvasChange(payload) {
    var points = JSON.parse(payload.body);
    var pointTuples = points.body.pointArray;
    if(pointTuples[pointTuples.length - 1][1] === parseInt(localStorage.getItem("team"),10)) { //only show stuff from the same team
        pointTuples.pop(); //remove -1 element, since it doesnt make sense to draw.
        for (var i = 0; i < pointTuples.length; i++) {
            if (i > 0) {
                drawPixel(pointTuples[i][0], pointTuples[i][1], pointTuples[i][2],0);
                drawLineToPrevPoint(pointTuples[i], pointTuples[i - 1], pointTuples[i][2]);
            } else {
                drawPixel(pointTuples[i][0], pointTuples[i][1], pointTuples[i][2], 0);
            }
        }
        if(roomType === "Mayhem") {
            points.body.mayhem.forEach(function(circle) {
                drawCircle(circle.x, circle.y, circle.r, "white");
            });
        }
    }
    else{//use these points to draw on the other canvas
        pointTuples.pop(); //remove -1 element, since it doesnt make sense to draw.
        for (var i = 0; i < pointTuples.length; i++) {
            if (i > 0) {
                drawPixelOther(pointTuples[i][0], pointTuples[i][1], pointTuples[i][2], 0);
                drawLineToPrevPointOther(pointTuples[i], pointTuples[i - 1], pointTuples[i][2]);
            } else {
                drawPixelOther(pointTuples[i][0], pointTuples[i][1], 0, 0);
            }
        }
    }
}

function drawPixelOther(x, y, hex, a) {
    var index = ((x + y) * canvO.width) * 30;
    var rgbArr = hexToRgb(hex);
    canvOData.data[index] = rgbArr.r;//r
    canvOData.data[index + 1] = rgbArr.g;//g
    canvOData.data[index + 2] = rgbArr.b;//b
    canvOData.data[index + 3] = 255;//opacity?

}

function drawLineToPrevPointOther(currPoint, prevPoint,hex) {
    ctx.beginPath();
    ctx.moveTo(currPoint[0], currPoint[1]);
    ctx.lineTo(prevPoint[0], prevPoint[1]);
    ctx.strokeStyle = hex;
    ctx.lineWidth = 5;
    ctx.stroke();

}

function chooseWord(button) {
    $.ajax({
        url: "/room/game/wordToGuess?roomId=" + roomId + "&wordToGuess=" + button.innerText + "&team=" + localStorage.getItem("team"),
        type: "POST",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            document.getElementById("wordChoicesDiv").innerHTML = "The word to draw is: " + button.innerText;
            localStorage.setItem("wordToDraw", button.innerText);
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
}

function getWordChoices() {
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
}

function getUsers() {
    $.ajax({
        url: "/room/getUsersInRoom?roomId=" + roomId,
        type: "GET",
        success: function (users) {
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

function showAdminLink() {
    if(localStorage.getItem("username").substr(0,5).toLowerCase() === "guest" || localStorage.getItem("username") === null){
        document.getElementById("navbardrop").style.display = "none";
    }
    else {
        $.ajax({
            url: "/isAdmin?username=" + localStorage.getItem("username"),
            type: "GET",
            success: function (resp) {
                if(resp === false){
                    document.getElementById("navbardrop").style.display = "none";
                }
            },
            error: function (e) {
                document.getElementById("librarySelection").innerHTML = "<option>Error getting categories from table</option>";
            }
        });
    }
}

$(document).ready(function () {
    //document.getElementById("usernameLocationForTests").innerText = localStorage.getItem("username");
    document.getElementById("welcome").innerHTML = "Welcome, " + localStorage.getItem("username");
    canvO = document.getElementById("otherCanvas");
    canvO.style.display = "none";
    ctx = canvO.getContext("2d");
    canvOData = ctx.getImageData(0, 0, canvO.width, canvO.height);
    showAdminLink();
});

