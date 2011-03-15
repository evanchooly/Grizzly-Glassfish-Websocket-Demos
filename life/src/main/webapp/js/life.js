const size = 47;
try {
    if (!"WebSocket" in window) {
        alert("Your browser does not support web sockets.  Please try another browser.")
    } else {
        socket = new WebSocket("ws://localhost:8080/life");
        socket.onmessage = function (evt) {
            process(evt.data);
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
function process(data) {
    eval(data);
}
