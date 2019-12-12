//We can use this canvas in both the game screens and main menu, since they will be the same.

var canvas,canvasData, ctx, flag = false,
    prevX = 0,
    currX = 0,
    prevY = 0,
    currY = 0,
    dot_flag = false;

var x = "black",
    y = 2;

function init() {
    canvas = document.getElementById('can');
    ctx = canvas.getContext("2d");
    canvasData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    var w = canvas.width;
    var h = canvas.height;

    canvas.addEventListener("mousemove", function (e) {
        findxy('move', e)
    }, false);
    canvas.addEventListener("mousedown", function (e) {
        findxy('down', e)
    }, false);
    canvas.addEventListener("mouseup", function (e) {
        findxy('up', e)
    }, false);
    canvas.addEventListener("mouseout", function (e) {
        findxy('out', e)
    }, false);
}


function draw() {
    ctx.beginPath();
    ctx.moveTo(prevX, prevY);
    ctx.lineTo(currX, currY);
    ctx.strokeStyle = x;
    ctx.lineWidth = y;
    ctx.stroke();
    ctx.closePath();
}


var pointArray = [];

function findxy(res, e) {
    if (res === 'down') {
        prevX = currX;
        prevY = currY;
        currX = e.clientX;
        currY = e.clientY;
        pointArray.push([currX,currY]);

        flag = true;
        dot_flag = true;
        if (dot_flag) {
            ctx.beginPath();
            ctx.fillStyle = x;
            ctx.fillRect(currX, currY, 2, 2);
            ctx.closePath();
            dot_flag = false;
        }
    }
    if (res === 'up' || res === "out") {
        console.log(JSON.stringify(pointArray));
        sendCanvasData(1);
        flag = false;
    }
    if (res === 'move') {
        if (flag) {
            pointArray.push([currX, currY]);
            prevX = currX;
            prevY = currY;
            currX = e.clientX;
            currY = e.clientY;
            draw();
        }
    }

}


function drawPixel(x, y, r, g, b, a) {
    var index = (x + y * canvas.width) * 30;
    canvasData.data[index] = 0;//r
    canvasData.data[index + 1] = 0;//g
    canvasData.data[index + 2] = 0;//b
    canvasData.data[index + 3] = 255;//opacity?

}


function drawLineToPrevPoint(currPoint,prevPoint){
    ctx.moveTo(currPoint[0], currPoint[1]);
    ctx.lineTo(prevPoint[0], prevPoint[1]);
    ctx.stroke();

}

function updateCanvas() {
    ctx.putImageData(canvasData, 0, 0);
}