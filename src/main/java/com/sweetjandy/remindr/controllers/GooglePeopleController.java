package com.sweetjandy.remindr.controllers;

import com.google.api.client.util.Key;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.sun.media.jfxmedia.logging.Logger;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
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
import java.lang.String;

@Controller
public class GooglePeopleController {

    private final GooglePeopleService googlePeopleSvc;
    private PeopleService peopleService;

    public GooglePeopleController(GooglePeopleService googlePeopleSvc) {
        this.googlePeopleSvc = googlePeopleSvc;
    }

//    @GetMapping("/contacts")
//    public String showAll (Model model) throws IOException {
//
//        String authorizationUrl = googlePeopleSvc.setUp();
//
//        model.addAttribute("authorizationUrl", authorizationUrl);
//        // show all contacts
//        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
//                .setPersonFields("names,emailAddresses")
//                .execute();
//        List<Person> connections = response.getConnections();
//        System.out.println(connections);
//
//        return "users/contacts";
//    }

    @GetMapping("/confirm")
    public String confirm (Model viewModel) throws IOException {

        String authorizationUrl = googlePeopleSvc.setUp();
        viewModel.addAttribute("authorizationUrl", authorizationUrl);
        viewModel.addAttribute("contact", new Contact());

//        StringBuffer requestURL = request.getRequestURL();
//        String s = requestURL.toString();
//        String pathInfo = request.getPathInfo();
//        String requestURI = request.getRequestURI();
//        Map<String, String[]> parameterMap = request.getParameterMap();
//
//        googlePeopleSvc.setUp();
//
//        // show all contacts
//        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
//                .setPersonFields("names,emailAddresses")
//                .execute();
//        List<Person> connections = response.getConnections();
//        System.out.println(connections);

        return "users/add-contacts";
    }

    @GetMapping("/google/contacts")
    public String   viewContacts(@RequestParam(name = "token") String token, Model model) throws IOException {
        List<Person> people = googlePeopleSvc.contacts(token);
//        people.get(0).getNames().get(0).getGivenName();
//        people.get(0).getNames().get(0).getFamilyName();
//        people.get(0).getPhoneNumbers().get(0).getValue();
        model.addAttribute("contacts", people);
        return "users/google-contacts";
    }
//    @GetMapping("/contacts/{id}")
//    public String viewIndividualContact(@PathVariable long id, Model viewModel) {
//        User user = usersRepository.findOne(2L);
//
//        // use the contacts repository to find one contact by its id
//        Contact contact = contactsRepository.findOne(id);
//
//        Contact usersContact = contactsRepository.findContactFor(user.getId(), contact.getId());
//        if (usersContact == null) {
//            return "redirect:/";
//        }
//    private List<Contact> personToContacts(List<Person> persons) {
//        for (Person person: persons) {
//            Contact contact = new Contact();
//            List<Name> names = person.getNames();
//            Name firstName = names.get(3);
//            Name lastName = names.get(4);
////            List phoneNumbers = person.getPhoneNumbers();
////            phoneNumber phoneNumber = phoneNumbers.get(3);
//            contact.setFirstName(firstName);
//            contact.setLastName(lastName);
////            contact.setPhoneNumber(phoneNumber);
//        }
//    }
}


