package com.sweetjandy.remindr.controllers;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.services.GooglePeopleService;

import com.sweetjandy.remindr.services.GooglePeopleService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class GooglePeopleController {
    private final GooglePeopleService googlePeopleSvc;
    private PeopleService peopleService;

    public GooglePeopleController(GooglePeopleService googlePeopleSvc) {
        this.googlePeopleSvc = googlePeopleSvc;
    }

    @GetMapping("/confirm")
    public String confirm(Model viewModel) throws IOException {
        String authorizationUrl = googlePeopleSvc.setUp();
        viewModel.addAttribute("authorizationUrl", authorizationUrl);
        viewModel.addAttribute("contact", new Contact());

        return "users/add-contacts";
    }

    @GetMapping("/google/contacts")
    public @ResponseBody
    List<Person> viewContacts(@RequestParam(name = "token") String token) throws IOException {
        return googlePeopleSvc.contacts(token);
    }
}