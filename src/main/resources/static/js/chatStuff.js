var me = {};
var stompClient = null;
var urlParams = new URLSearchParams(window.location.search);
var roomId = urlParams.get("room");

function formatAMPM(date) {
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12; // the hour '0' should be '12'
    minutes = minutes < 10 ? '0'+minutes : minutes;
    var strTime = hours + ':' + minutes + ' ' + ampm;
    return strTime;
}

//-- No use time. It is a javaScript effect.
function insertChat(who, text, time){

    if (time === undefined){
        time = 0;
    }
    var control = "";
    var date = formatAMPM(new Date());
/*
    if (who == "me"){
        control = '<li style="width:100%">' +
            '<div class="msj macro">' +
            '<div class="text text-l">' +
            '<p>'+ text +'</p>' +
            '<p><small>'+date+'</small></p>' +
            '</div>' +
            '</div>' +
            '</li>';
    }else{
        control = '<li style="width:100%;">' +
            '<div class="msj-rta macro">' +
            '<div class="text text-r">' +
            '<p>'+text+'</p>' +
            '<p><small>'+date+'</small></p>' +
            '</div>' +
            '</li>';
    }
    */
    setTimeout(
        function(){
            $("ul").append(control);
        }, time);

}


$(".mytext").on("keyup", function(e){
    if (e.which == 13){
        var text = $(this).val();
        var chatMessage = {
            message: text,
            username: localStorage.getItem("username"),
            roomId: roomId,
        };
        if (text !== ""){
            insertChat("me", stompClient.send("/app/room/global/chat", {}, JSON.stringify(chatMessage)));
            $(this).val('');
        }
    }
});


function test() {
    //document.getElementById("chatsubmit").onclick = sendMessage(roomId);
    var socket = new SockJS('/ws-connect');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

test();

function onConnected() {
    stompClient.subscribe("/room/global/chat", onMessageReceived);
    stompClient.send("/app/room/global/chat", {}, JSON.stringify({
        username: "Server",
        message: "User " + localStorage.getItem("username") + " has joined."
    }));
    stompClient.send("/app/room/global/join", {}, JSON.stringify({username: localStorage.getItem("username")}));
}

function onError() {
    console.log("ERROR");
}

/*
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
*/


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var p = document.createElement("p");
    p.innerHTML = message.username + ": " + message.message;
    p.id = "usermsg";
    document.getElementById("chatbox").appendChild(p);
    document.getElementById("chatbox").scrollTop = document.getElementById("chatbox").scrollHeight - 10;
}