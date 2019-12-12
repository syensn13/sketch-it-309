let dataAnalysisMap = new Map();
let libraryTypeClicks = new Map();
let numberofPlayersClicks = new Map();
let gameTypeClicks = new Map();
let array = [];

gameTypeClicks.set("classic",0);
gameTypeClicks.set("teams", 0);
gameTypeClicks.set("mayhem" , 0);

libraryTypeClicks.set("all",0);
libraryTypeClicks.set("nature",0);
libraryTypeClicks.set("animal",0);
libraryTypeClicks.set("technology",0);

numberofPlayersClicks.set(2,0);
numberofPlayersClicks.set(3,0);
numberofPlayersClicks.set(4,0);
numberofPlayersClicks.set(5,0);
numberofPlayersClicks.set(6,0);
numberofPlayersClicks.set(7,0);
numberofPlayersClicks.set(8,0);

let date = new Date();
let strDate = (date.getDate()) + " " + (date.getMonth() + 1) + " " + date.getFullYear();
dataAnalysisMap.set("date", strDate);
dataAnalysisMap.set("loginClicks", 0);
dataAnalysisMap.set("guestLoginClicks", 0);
dataAnalysisMap.set("passwordClicks", 0);

window.onbeforeunload = function sendData() {
    array.push(strMapToObj(dataAnalysisMap),strMapToObj(gameTypeClicks),strMapToObj(numberofPlayersClicks),strMapToObj(libraryTypeClicks));
    console.log(array);
    console.log(JSON.stringify(array));
    $.ajax({
        url: "/dataSend",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(array),
        success: function (resp) {
            console.log("Successfully Sent!");
        },
        error: function (e) {
            console.log("Error with data collection!");
        }
    });
};

function strMapToObj(strMap) {
    let obj = Object.create(null);
    for (let [k,v] of strMap) {
        // We don’t escape the key '__proto__'
        // which can cause problems on older engines
        obj[k] = v;
    }
    return obj;
}
