var life = {
    'get' : function() {
        new Ajax.Request('http://localhost:8080/async', {
            method : 'GET',
            onSuccess : life.update
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
        eval(res.responseText);
//        life.get();
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
Behaviour.addLoadEvent(life.get);