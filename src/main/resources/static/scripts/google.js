if (window.location.hash) {
    var token = window.location.hash.split("=")[1].split("&").shift();
    window.location.href = "/google/contacts?token=" + token;
}
