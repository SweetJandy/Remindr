if (window.location.hash) {
    var token = window.location.hash.split("=")[1].split("&").shift();
    window.location.href = "/google/contacts?token=" + token;
}

// $.ajax("/https://people.googleapis.com/v1/people/me/connections?sortOrder=LAST_NAME_ASCENDING").done(function(data, status, jqXhr) {
//     alert("AJAX call completed successfully!");
//     console.log("Request status: " + status);
//     console.log("Data returned from server:");
//     console.log(data);
// });


$.ajax({

    // The 'type' property sets the HTTP method.
    // A value of 'PUT' or 'DELETE' will trigger a preflight request.
    type: 'GET',

    // The URL to make the request to.
    url: 'http://html5rocks-cors.s3-website-us-east-1.amazonaws.com/index.html',

    // The 'contentType' property sets the 'Content-Type' header.
    // The JQuery default for this property is
    // 'application/x-www-form-urlencoded; charset=UTF-8', which does not trigger
    // a preflight. If you set this value to anything other than
    // application/x-www-form-urlencoded, multipart/form-data, or text/plain,
    // you will trigger a preflight request.
    contentType: 'text/plain',

    xhrFields: {
        // The 'xhrFields' property sets additional fields on the XMLHttpRequest.
        // This can be used to set the 'withCredentials' property.
        // Set the value to 'true' if you'd like to pass cookies to the server.
        // If this is enabled, your server must respond with the header
        // 'Access-Control-Allow-Credentials: true'.
        withCredentials: false
    },

    headers: {
        // Set any custom headers here.
        // If you set any non-simple headers, your server must include these
        // headers in the 'Access-Control-Allow-Headers' response header.
    },

    success: function() {
        // Here's where you handle a successful response.
    },

    error: function() {
        // Here's where you handle an error response.
        // Note that if the error was due to a CORS issue,
        // this function will still fire, but there won't be any additional
        // information about the error.
    }
});


$.get("/https://people.googleapis.com/v1/people/me/connections?sortOrder=LAST_NAME_ASCENDING", {
    APPID: "AIzaSyAEaUdEARCOwNfhqaGkzRKOXM_efyPXF-4"
}).done(function(data) {
    console.log(data);
});
// (function () {
//     "use strict";
//     $(document).ready(function () {
        // function setListeners() {
        //     $("#geoSubmit").on("click", function () {
        //         $("#forecasts").empty();
        //         $("#coordinatesCity").empty();
        //         $("#geoSubmit").off("click");
        //         getCoordinates();
        //     });
        // }

        // var latitude = $("#lat");
        // var longitude = $("#lon");
        //
        // function getCoordinates() {
        //     if (latitude.val() !== "" && longitude.val() !== "") {
        //         var lat = $("#lat").val();
        //         var lon = $("#lon").val();
        //         getWeather(lat, lon)
        //     } else {
        //         alert("You must enter your coordinates!")
        //     }
        // }

        function getContacts() {
            $.get('/https://people.googleapis.com/v1/people/me/connections?sortOrder=LAST_NAME_ASCENDING', {
                APPID: "1d9259fc21b4fb3a6976934537609e66"
            }).done(function (data) {
                console.log(data);
            })}
        //         var city = data.city.name;
        //         var cityHtml = "";
        //         var html = "";
        //         cityHtml += "<h2>" + city + "</h2>";
        //         $("#coordinatesCity").html(cityHtml);
        //         data.list.forEach(function (day) {
        //             var icon = day.weather[0].icon;
        //             var url = "http://openweathermap.org/img/w/" + icon + ".png";
        //             var img = "<img src='" + url + "'>";
        //             html += "<div class='col-xs-4 weatherInfo'>";
        //             html += "<h2>" + Math.round(day.main.temp_max) + "°/";
        //             html += Math.round(day.main.temp_min) + "°</h2>";
        //             html += "<span>" + img + "</span>";
        //             html += "<p><strong>" + day.weather[0].main + ": </strong>";
        //             html += day.weather[0].description + "</p><p><strong>";
        //             html += "Humidity: </strong>" + day.main.humidity;
        //             html += "</p><p><strong>" + "Wind: </strong>" + day.wind.speed;
        //             html += "</p><p><strong>" + "Pressure: </strong>";
        //             html += day.main.pressure + "</p></div>";
        //         });
        //         $("#forecasts").html(html);
        //         // setListeners();
        //     });
        // }