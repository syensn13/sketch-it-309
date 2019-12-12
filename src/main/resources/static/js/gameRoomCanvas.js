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
var painting;
var myCanv = document.getElementById("myCanvas");
context = document.getElementById('myCanvas').getContext("2d");
myCanv.style.marginLeft = '30px';
myCanv.style.marginTop = '30px';


$('#myCanvas').mousedown(function(e){
    var mouseX = e.pageX - this.offsetLeft;
    var mouseY = e.pageY - this.offsetTop;
    painting = true;
    addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop);
    drawCanvas();
});

$('#myCanvas').mousemove(function(e){
    if(painting){
        addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop, true);
        drawCanvas();
    }
});

$('#myCanvas').mouseup(function(e){
    painting = false;
    sendCanvasData();
});

$('#myCanvas').mouseleave(function(e){
    painting = false;
    sendCanvasData();
});


function addClick(x, y, dragging) {
    clickX.push(x);
    clickY.push(y);
    clickDrag.push(dragging);
    if(currentSelection == "eraser"){
        clickedColor.push("white");
    }else{
        clickedColor.push(currentColor);
    }
    clickSize.push(curSize);
}


function drawCanvas(){
    context.lineJoin = "round";
    context.lineWidth = 5;
    for(var i=0; i < clickX.length; i++) {
        context.beginPath();
        if(clickDrag[i] && i) {
            context.moveTo(clickX[i-1], clickY[i-1]);
        } else {
            context.moveTo(clickX[i]-1, clickY[i]);
        }
        context.lineTo(clickX[i], clickY[i]);
        context.closePath();
        context.strokeStyle = clickedColor[i];
        context.lineWidth = curSize;
        context.stroke();
    }
}

function startPencil(){
    currentSelection = "pencil";
    console.log(currentSelection);
    drawCanvas();
}

function startEraser(){
    currentSelection = "eraser";
    console.log(currentSelection);
}

$( "#colorPurple" ).click(function() {
    currentColor = purpleColor;
    console.log("Color changed to Purple.");
});

$( "#colorGreen" ).click(function() {
    currentColor = greenColor;
    console.log("Color changed to green.");
});

$( "#colorYellow" ).click(function() {
    currentColor = yellowColor;
    console.log("Color changed to Yellow.");
});

$( "#colorBlue" ).click(function() {
    currentColor = blueColor;
    console.log("Color changed to blue.");
});

$( "#colorBlack" ).click(function() {
    currentColor = blackColor;
    console.log("Color changed to black.");
});

$( "#colorRed" ).click(function() {
    currentColor = redColor;
    console.log("Color changed to red.");
});

$( "#clearCanvas" ).click(function() {
    clickX = new Array();
    clickY = new Array();
    clickDrag = new Array();
    context.clearRect(0, 0, context.canvas.width, context.canvas.height);
    console.log(currentSelection);
    clickedColor = [];
    clickedColor.push(currentColor);
    console.log(currentColor);
});

function updateCanvas() {
    context.putImageData(canvasData, 0, 0);
}


