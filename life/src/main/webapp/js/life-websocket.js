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

var rules = {
    '#delay': function(element) {
        element.onblur = function() {
            socket.send("delay", element.value);
        };
    },
    '#randomize': function(element) {
        element.onclick = function() {
            socket.send("randomize");
        };
    }
};