$(document).ready(function () {
    getData();
    showAdminLink();
});

function getData() {
    $.ajax({
        url: "/getData",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            console.log(JSON.stringify(resp));
            console.log(resp["numberofPlayersClicks"]);
            makeLoginChart(resp);
            makeNumofPlayersChart(resp);
            makeLibrarySelectChart(resp);
            makeGameTypeSelectChart(resp);


        },
        error: function (e) {
            alert("Error getting data-set!");
        }
    });
}

function getFilteredData() {
    let firstDate = $('#firstDateInput').val();
    console.log(firstDate);
    let secondDate = $('#secondDateInput').val();
    console.log(firstDate);
    let fDateObj = new Date(firstDate);
    console.log(fDateObj);
    let secondDateData;
    if (secondDate !== '' && secondDate !== ' ') {
        let sDateObj = new Date(secondDate);
        console.log(sDateObj);
        secondDateData = (sDateObj.getDate()) + " " + (sDateObj.getMonth() + 1) + " " + sDateObj.getFullYear();
        console.log(secondDate);
    }
    else{
        secondDateData = "null";
    }
    let firstDateData = (fDateObj.getDate()) + " " + (fDateObj.getMonth() + 1) + " " + fDateObj.getFullYear();

    $.ajax({
        url: "/getData?firstDate="+firstDateData + "&secondDate=" + secondDateData,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function (resp) {
            console.log(JSON.stringify(resp));
            console.log(resp["numberofPlayersClicks"]);
            makeLoginChart(resp);
            makeNumofPlayersChart(resp);
            makeLibrarySelectChart(resp);
            makeGameTypeSelectChart(resp);


        },
        error: function (e) {
            alert("Error getting data-set!");
        }
    });
}

function makeLibrarySelectChart(resp) {
    $('#libraryCanvas').remove(); // this is my <canvas> element
    $('#libraryContainer').append('<canvas id="libraryCanvas" width="500" height="500"><canvas>');
    console.log(resp["libraryTypeClicks"]);
    let labels = Object.keys(resp["libraryTypeClicks"]);
    let data = [resp["libraryTypeClicks"]["all"], resp["libraryTypeClicks"]["nature"], resp["libraryTypeClicks"]["animal"], resp["libraryTypeClicks"]["technology"]];
    let colors = ['rgba(255, 0, 0, 0.5)', 'rgba(0, 0, 255, 0.5)','rgba(0, 255, 0, 0.5)', 'rgba(127, 0, 127, 0.5)'];
    let ctx = document.getElementById("libraryCanvas");
    let config = {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '# of selections',
                data: data,
                backgroundColor: colors
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            },
            title: {
                display: true,
                text: 'Library Choices'
            },
            responsive: false
        }
    };

    let chart = new Chart(ctx, config);
}

function makeGameTypeSelectChart(resp) {
    console.log(resp["gameTypeSelectClicks"]);
    $('#gameTypeCanvas').remove(); // this is my <canvas> element
    $('#gameTypeContainer').append('<canvas id="gameTypeCanvas" width="500" height="500"><canvas>');
    let labels = Object.keys(resp["gameTypeSelectClicks"]);
    let data = [resp["gameTypeSelectClicks"]["classic"], resp["gameTypeSelectClicks"]["teams"], resp["gameTypeSelectClicks"]["mayhem"]];
    let colors = ['rgba(255, 0, 0, 0.5)', 'rgba(0, 0, 255, 0.5)','rgba(0, 255,0 , 0.5)'];
    let ctx = document.getElementById("gameTypeCanvas");
    let config = {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '# of selections',
                data: data,
                backgroundColor: colors
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            },
            title: {
                display: true,
                text: 'Game Types'
            },
            responsive: false
        }
    };

    let chart = new Chart(ctx, config);
}

function makeLoginChart(resp){
    $('#loginsCanvas').remove(); // this is my <canvas> element
    $('#loginContainer').append('<canvas id="loginsCanvas" width="500" height="500"><canvas>');
    let labels = ["Logins","Guest Logins"];
    let data = [resp["loginClicks"],resp["guestLoginClicks"]];
    let colors = ['rgba(255, 0, 0, 0.5)', 'rgba(0, 0, 255, 0.5)'];
    let ctx = document.getElementById("loginsCanvas");
    let config = {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                label: '# of logins',
                data: data,
                backgroundColor : colors
            }],

        },
        options: {
            title: {
                display: true,
                text: 'Logins'
            },
            responsive: false
        }
    };

    let chart = new Chart(ctx, config);
}

function makeNumofPlayersChart(resp) {
    $('#numofPlayersCanvas').remove(); // this is my <canvas> element
    $('#playerNumContainer').append('<canvas id="numofPlayersCanvas" width="500" height="500"><canvas>');
    let labels = Object.keys(resp["numberofPlayersClicks"]);
    let data = [resp["numberofPlayersClicks"][2],resp["numberofPlayersClicks"][3],resp["numberofPlayersClicks"][4],resp["numberofPlayersClicks"][5],resp["numberofPlayersClicks"][6],resp["numberofPlayersClicks"][7],resp["numberofPlayersClicks"][8]]
    let colors = ['rgba(255, 0, 0, 0.5)', 'rgba(0, 0, 255, 0.5)','rgba(0, 255,0 , 0.5)','rgba(127, 0, 127, 0.5)','rgba(0, 127, 127, 0.5)','rgba(200, 50, 0, 0.5)','rgba(0, 0, 0, 0.5)'];
    let ctx = document.getElementById("numofPlayersCanvas");
    let config = {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '# of selections',
                data: data,
                backgroundColor: colors
            }]
        },
        options: {scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            },
            title: {
                display: true,
                text: 'Max Number of Players per Game'
            },
            responsive: false
        }
    };

    let chart = new Chart(ctx, config);

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

