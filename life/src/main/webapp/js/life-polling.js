
var life = {
      'poll' : function() {
         new Ajax.Request('http://localhost:8081/poll', {
            method : 'GET',
            onSuccess : life.update
         });
      },
      "post" : function(value) {
         new Ajax.Request('http://localhost:8081/poll', {
            method : 'POST',
            postMessage : value
         });
      },
      'update' : function(res) {
//          alert(res);
         eval(res.response);
         life.poll();
      }
};

var rules = {
      '#delay': function(element) {
         element.onclick = function() {
            life.post("delay:" + element.value);
         };
      },
      '#randomize': function(element) {
         element.onclick = function() {
            life.post("randomize");
         };
      }
};

Behaviour.register(rules);
Behaviour.addLoadEvent(life.poll);

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