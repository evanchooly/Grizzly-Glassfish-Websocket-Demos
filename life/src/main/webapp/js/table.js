function setValue(id, value) {
    document.getElementById(id).value = value;
}
function create(height, width, delay) {
    for (var y = 0; y < height; y++) {
        for (var x = 0; x < width; x++) {
            document.getElementById(y + "-" + x).innerHTML = "&nbsp;";
        }
    }
    setValue("delay", delay);
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