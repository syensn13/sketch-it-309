var purpleColor = "purple";
var greenColor = "green";
var yellowColor = "yellow";
var blackColor = "black";
var blueColor = "blue";
var currentColor = "black";
var redColor = "red";
var clickedColor = [];
var currentSelection = "pencil";
var curSize = "normal";
var clickSize = [];
var clickX = [];
var clickY = [];
var clickDrag = [];
var pointArray = [];
var painting;
var prevX = 0,
    currX = 0,
    prevY = 0,
    currY = 0;
var canvas, contxt, canvData;
var hex = "#000000";
var initalData;


$('#myCanvas').mousedown(function (e) {
    var mouseX = e.pageX - this.offsetLeft;
    var mouseY = e.pageY - this.offsetTop;
    painting = true;
    addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop);
    prevX = currX;
    prevY = currY;
    currX = e.pageX - this.offsetLeft;
    currY = e.pageY - this.offsetTop;
    pointArray.push([currX, currY,hex]);
    drawCanvas();
});

$('#myCanvas').mousemove(function (e) {
    if (painting) {
        addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop, true);
        prevX = currX;
        prevY = currY;
        currX = e.pageX - this.offsetLeft;
        currY = e.pageY - this.offsetTop;
        pointArray.push([currX, currY,hex]);
        drawCanvas();
    }
});

$('#myCanvas').mouseup(function (e) {
    if(document.title === "Sketch.it Game") {
        sendCanvasData();
        visionApi();
    }
    //console.log(JSON.stringify(pointArray));
    painting = false;
});

$('#myCanvas').mouseleave(function (e) {
    painting = false;
});


function addClick(x, y, dragging) {
    clickX.push(x);
    clickY.push(y);
    clickDrag.push(dragging);
    if (currentSelection == "eraser") {
        clickedColor.push("white");
    } else {
        clickedColor.push(currentColor);
    }
    clickSize.push(curSize);
}

function drawCircle(x, y, r, color) {
    contxt.beginPath();
    contxt.arc(x, y, r, 0, 2 * Math.PI);
    contxt.fillStyle = color;
    contxt.fill();
}

function drawCanvas() {
    contxt.lineJoin = "round";
    contxt.lineWidth = 5;
    for (var i = 0; i < clickX.length; i++) {
        contxt.beginPath();
        if (clickDrag[i] && i) {
            contxt.moveTo(clickX[i - 1], clickY[i - 1]);
        } else {
            contxt.moveTo(clickX[i] - 1, clickY[i]);
        }
        contxt.lineTo(clickX[i], clickY[i]);
        contxt.closePath();
        contxt.strokeStyle = clickedColor[i];
        contxt.lineWidth = curSize;
        contxt.stroke();
    }
}

function drawPixel(x, y, hex, a) {
    var index = ((x + y) * canvas.width) * 30;
    var rgbArr = hexToRgb(hex);
    canvData.data[index] = rgbArr.r;//r
    canvData.data[index + 1] = rgbArr.g;//g
    canvData.data[index + 2] = rgbArr.b;//b
    canvData.data[index + 3] = 255;//opacity?

}

function drawLineToPrevPoint(currPoint, prevPoint,hex) {
    contxt.beginPath();
    contxt.moveTo(currPoint[0], currPoint[1]);
    contxt.lineTo(prevPoint[0], prevPoint[1]);
    contxt.strokeStyle = hex;
    contxt.lineWidth = 5;
    contxt.stroke();

}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}


function intToHex(integer){
    var hex  = Number(integer).toString(16);
    if(hex<2){
        hex = "0" + hex;
    }
    return hex;
}


function startPencil() {
    currentSelection = "pencil";
    hex = "#000000";
    console.log(currentSelection);
    drawCanvas();
}

function startEraser() {
    currentSelection = "eraser";
    hex = "#FFFFFF";
    console.log(currentSelection);
}

$("#colorPurple").click(function () {
    currentColor = purpleColor;
    hex = "#800080";
    console.log("Color changed to Purple.");
});

$("#colorGreen").click(function () {
    currentColor = greenColor;
    hex = "#008000";
    console.log("Color changed to green.");
});

$("#colorYellow").click(function () {
    currentColor = yellowColor;
    hex = "#FFFF00";
    console.log("Color changed to Yellow.");
});

$("#colorBlue").click(function () {
    currentColor = blueColor;
    hex = "#0000FF";
    console.log("Color changed to blue.");
});

$("#colorBlack").click(function () {
    currentColor = blackColor;
    hex = "#000000";
    console.log("Color changed to black.");
});

$("#colorRed").click(function () {
    currentColor = redColor;
    hex = "#FF0000";
    console.log("Color changed to red.");
});

$("#clearCanvas").click(function () {
    // contxt.clearRect(0, 0, contxt.canvas.width, contxt.canvas.height);
    // clickX = [];
    // clickY = [];
    // clickDrag = [];
    // console.log(currentSelection);
    // clickedColor = [];
    // clickedColor.push(currentColor);
    // console.log(currentColor);
    // pointArray = [];
    contxt.putImageData(initalData,0,0);
    canvData  = contxt.getImageData(0,0,canvas.width,canvas.height);
    clickX = [];
    clickY = [];
    clickDrag = [];
    console.log(currentSelection);
    clickedColor = [];
    clickedColor.push(currentColor);
    console.log(currentColor);
    pointArray = [];
    stompClient.send("/app/room/" + roomId + "/clearCanvas", {});
});

$(document).ready(function () {
    canvas = document.getElementById("myCanvas");
    contxt = canvas.getContext("2d");
    canvData = contxt.getImageData(0, 0, canvas.width, canvas.height);
    initalData = canvData;
    canvas.style.marginLeft = '30px';
    canvas.style.marginTop = '30px';
});






