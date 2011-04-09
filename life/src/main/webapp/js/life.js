const size = 47;
try {
    if (!"WebSocket" in window) {
        alert("Your browser does not support web sockets.  Please try another browser.")
    } else {
        socket = new WebSocket("ws://localhost:8080/life");
        socket.onmessage = function (evt) {
            eval(evt.data);
        };
        socket.onclose = function (evt) {
            connectionProblems();
        }
    }
} catch(err) {
    connectionProblems(err);
}
function connectionProblems(err) {
    document.getElementById("welcome").innerHTML
        = "<span class=\"error\">We're having problems connecting to the server.  Reload to try reconnecting. "
        + err + "</span>";
}

function create(height, width, delay) {
    for(var y = 0; y < height; y++) {
        for(var x = 0; x < width; x++) {
            document.getElementById(y + "-" + x).innerHTML = "&nbsp;";
        }
    }
    setValue("delay", delay);
}

function setValue(id, value) {
    document.getElementById(id).value = value;
}
function send(message, element) {
    socket.send(message + ":" + element.value);
}

function set(x, y, on) {
    var id = x + "-" + y;
    var value;
    if (on) {
        value = '*';
    } else {
        value = '&nbsp;';
    }
    try {
        document.getElementById(id).innerHTML = value;
    } catch(err) {
        alert("id = " + id + "\n" + err);
    }
}