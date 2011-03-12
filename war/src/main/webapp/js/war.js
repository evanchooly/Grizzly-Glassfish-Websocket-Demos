var oppReady = false;
var meReady = false;
const size = 25;
function connect() {
    try {
        if (!"WebSocket" in window) {
            alert("Your browser does not support web sockets.  Please try another browser.")
        } else {
            socket = new WebSocket("ws://localhost:8080/war");
            socket.onopen = function (evt) {
                document.getElementById("welcome").innerHTML
                    = "We're live!";
                document.getElementById("connectLink").style.display = "none";
                document.getElementById("board").style.display = "";
                disableMap("fire-map", !(oppReady && meReady));
                socket.send("place:AA:0:0");
                socket.send("place:HQ:5:5");
                socket.send("place:TANK:15:15");
                socket.send("place:PLATOON:0:15");
            };
            socket.onmessage = function (evt) {
                process(evt.data);
            };
            socket.onclose = function (evt) {
                connectionProblems();
            }
        }
    } catch(err) {
        connectionProblems();
    }
}
function disableMap(elementId, disabled) {
    items = document.getElementById(elementId).getElementsByTagName("input");
    for (i = 0; i < items.length; i++) {
        items[i].disabled = disabled;
    }
}
function fire(x, y) {
    socket.send("strike:" + x + ":" + y);
}
function place(x, y) {
    var piece = getCheckedValue("units");
    socket.send("place:" + piece + ":" + x + ":" + y);
    disableMap("place-map", true);
    document.getElementById(piece).disabled = true;
    document.getElementById(piece).checked = false;
}
function connectionProblems() {
    document.getElementById("welcome").innerHTML
        = "<span class=\"error\">We're having problems connecting to the server.  Reload to try reconnecting.</span>";
}
function placePiece(pieces) {
    var type = pieces[1];
    var x = parseInt(pieces[2]);
    var y = parseInt(pieces[3]);
    var xDim = parseInt(pieces[4]);
    var yDim = parseInt(pieces[5]);
    for (i = 0; i < xDim; i++) {
        for (j = 0; j < yDim; j++) {
            var id = "place-" + (i + x) + "-" + (j + y);
            var node = document.getElementById(id);
            node.parentNode.className = "placed";
            node.parentNode.id = id;
            node.parentNode.innerText = "*";
        }
    }
}
function process(data) {
    var pieces = data.split(":");
    if (pieces[0] == "ready") {
        if (pieces[1] == "opponent") {
            oppReady = true;
            document.getElementById("oppstatus").innerHTML = "ready!"
        } else if (pieces[1] == "you") {
            meReady = true;
            document.getElementById("mystatus").innerHTML = "ready!";
            disableMap("place-map", true)
        }
        disableMap("fire-map", !(oppReady && meReady))
    } else if (pieces[0] == "placed") {
        placePiece(pieces);
    } else if (pieces[0] == "boom") {
        var x = parseInt(pieces[1]);
        var y = parseInt(pieces[2]);
        registerHit("fire", x, y);
        var node = document.getElementById("fire-" + x + "-" + y);
        node.parentNode.innerText = "*";
    } else if (pieces[0] == "ouch") {
        registerHit("place", parseInt(pieces[1]), parseInt(pieces[2]))
    } else if (pieces[0] == "whiff") {
        registerMiss("fire", parseInt(pieces[1]), parseInt(pieces[2]))
    } else if (pieces[0] == "whew") {
        registerMiss("place", parseInt(pieces[1]), parseInt(pieces[2]))
    } else if (pieces[0] == "you win") {
        document.getElementById("oppstatus").innerHTML = "DEAD!";
        document.getElementById("mystatus").innerHTML = "VICTORIOUS!";
        disableMap("fire-map", true)
    } else if (pieces[0] == "you lose") {
        document.getElementById("oppstatus").innerHTML = "VICTORIOUS!";
        document.getElementById("mystatus").innerHTML = "DEAD!";
        disableMap("fire-map", true)
    } else {
        alert("back from server: " + data);
    }
}

function registerHit(name, x, y) {
    var node = document.getElementById(name + "-" + x + "-" + y);
    node.className = "hit";
}

function registerMiss(name, x, y) {
    var node = document.getElementById(name + "-" + x + "-" + y);
    node.parentNode.className = "miss";
    node.parentNode.innerText = "*";
}

function getCheckedValue(name) {
    var radioObj = document.getElementsByName(name);
    if (!radioObj) {
        return "";
    }
    var radioLength = radioObj.length;
    if (radioLength == undefined) {
        if (radioObj.checked) {
            return radioObj.value;
        }
        else {
            return "";
        }
    }
    for (var i = 0; i < radioLength; i++) {
        if (radioObj[i].checked) {
            return radioObj[i].value;
        }
    }
    return "";
}