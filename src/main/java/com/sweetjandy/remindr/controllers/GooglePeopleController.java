package com.sweetjandy.remindr.controllers;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.sweetjandy.remindr.services.GooglePeopleService;
import com.sweetjandy.remindr.services.TwilioService;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class GooglePeopleController {

    private final GooglePeopleService googlePeopleSvc;

    public GooglePeopleController(GooglePeopleService googlePeopleSvc) {
        this.googlePeopleSvc = googlePeopleSvc;
    }

    @GetMapping("/contacts")
//    @ResponseBody
    public String authorize () throws IOException {

        googlePeopleSvc.setUp();

//        PeopleService peopleService;
//        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
//                .setPersonFields("names,emailAddresses")
//                .execute();
//        List<Person> connections = response.getConnections();
//        System.out.println(connections);

        return "users/contacts";
    }
}

