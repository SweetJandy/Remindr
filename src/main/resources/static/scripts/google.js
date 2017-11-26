if (window.location.hash) {
    var token = window.location.hash.split("=")[1].split("&").shift();
    window.location.href = "/google/contacts?token=" + token;
}

(function ($) {
    var request = $.ajax({'url': '/contacts.json'});
    request.done(function (contacts) {
        var html = '';
        contacts.forEach(function(contact) {
            html += '<div class="contact">';
            html += '<h2 id="contact-name">' + contact.firstName + " " + contact.lastName + '</h2>';
            html += '<h4 id="contact-number">' + contact.phoneNumber + '</h4>';
            html += '</div>';
        });
        console.log(html);
        $('#contacts').html(html);
    });
})(jQuery);