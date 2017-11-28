var x = document.getElementById("myFile");
var txt = "";
if ('files' in x) {
    if (x.files.length == 0) {
        txt = "Select an image file";
    } else {
        for (var i = 0; i < x.files.length; i++) {
            txt += "<br><strong>" + (i+1) + ". file</strong><br>";
            var file = x.files[i];
            if ('name' in file) {
                txt += "name: " + file.name + "<br>";
            }
            if ('size' in file) {
                txt += "size: " + file.size + " bytes <br>";
            }
        }
    }
}

document.getElementById ("demo").innerHTML = txt;