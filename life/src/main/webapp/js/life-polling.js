var life = {
    'poll' : function() {
        new Ajax.Request('http://localhost:8080/comet', {
            method : 'GET',
            onSuccess : life.update
        });
    },
    'post' : function(value) {
        new Ajax.Request('http://localhost:8080/comet', {
            method : 'POST',
            postBody: value
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