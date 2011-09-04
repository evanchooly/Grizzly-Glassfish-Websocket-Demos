var life = {
    "poll" : function() {
        new Ajax.Request('http://localhost:8080/async', {
            method : 'GET',
            onComplete : life.update
        });
    },
    'post' : function(value) {
        new Ajax.Request('http://localhost:8080/async', {
            method : 'POST',
            postBody: value
        });
    },
    'update' : function(res) {
//        alert(res.responseText);
        eval(res.response);
        life.poll();
    }
};
var rules = {
    '#delay': function(element) {
        element.onblur = function() {
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