(function ($) {
    var request = $.ajax({'url': '/contacts.json'});
    request.done(function (contacts) {
        var html = '';
        contacts.forEach(function(contact) {
            html += '<div class="contact">';
            html += '<h4 id="contact-name">' + '<a>' + contact.firstName + " " + contact.lastName + '</a>' + '</h4>';
            // html += '<h4 id="contact-number">' + contact.phoneNumber + '</h4>';
            html += '</div>';
        });
        console.log(html);
        $('#contacts').html(html);
    });
})(jQuery);