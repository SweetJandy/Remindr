(function($) {
    var request = $.ajax({'url': '/contacts.json'});
    request.done(function (contacts) {
        var html = '';
        contacts.forEach(function(contact) {
            html += '<div class="contact">';
            html += '<h2 id="contact-name">' + contact.firstName + " " + contact.lastName + '</h2>';
            html += '<h4 id="contact-number">' + contact.phoneNumber + '</h4>';
            // html += '<p>Published by ' + post.user.username + '</p>';
            html += '</div>';
        });
        $('#contacts').html(html);
    });
})(jQuery);