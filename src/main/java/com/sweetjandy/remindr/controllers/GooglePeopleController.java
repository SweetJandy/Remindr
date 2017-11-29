package com.sweetjandy.remindr.controllers;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.GooglePeopleService;

import com.sweetjandy.remindr.services.GooglePeopleService;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class GooglePeopleController {

    private final GooglePeopleService googlePeopleSvc;
    private PeopleService peopleService;
    private UsersRepository usersRepository;
    private ContactsRepository contactsRepository;

    public GooglePeopleController(GooglePeopleService googlePeopleSvc, UsersRepository usersRepository, ContactsRepository contactsRepository) {

        this.googlePeopleSvc = googlePeopleSvc;
        this.usersRepository = usersRepository;
        this.contactsRepository = contactsRepository;
    }

    @GetMapping("/confirm")
    public String confirm (Model viewModel, HttpServletResponse response) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        String authorizationUrl = googlePeopleSvc.setUp();
        viewModel.addAttribute("authorizationUrl", authorizationUrl);
        viewModel.addAttribute("contact", new Contact());

        return "users/add-contacts";
    }

    @GetMapping("/google/contacts")
    public String viewContacts(@RequestParam(name = "token") String token, HttpServletResponse response) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        List<Person> persons = googlePeopleSvc.contacts(token);

        List<Contact> contacts = new ArrayList<>();

        for (Person person: persons) {
            if (person.getPhoneNumbers() != null && person.getPhoneNumbers().size()>0 && person.getNames() != null && person.getNames().size()>0 && person.getNames().get(0).getGivenName() != null && !person.getNames().get(0).getGivenName().trim().equals("")) {
                final String phoneNumber = formatPhoneNumber(person.getPhoneNumbers().get(0).getValue());

                if (phoneNumber != null) {
                    //        returns amount of contacts that are duplicated by phone number
                    long duplicates = user.getContacts().stream().filter(c -> c.getPhoneNumber().equals(phoneNumber)).count();
                    if (duplicates == 0) {
                        Contact contact = new Contact();
                        contact.setFirstName(person.getNames().get(0).getGivenName());
                        contact.setLastName(person.getNames().get(0).getFamilyName());
                        contact.setPhoneNumber(phoneNumber);
//                        contact.setGoogleContact(person.getEtag());
                        contacts.add(contact);
                    }
                }
            }
        }

        contactsRepository.save(contacts);
        user.getContacts().addAll(contacts);
        usersRepository.save(user);

        return "redirect:/contacts";
    }

    String formatPhoneNumber (String phoneNumber) {
        String correctFormat = phoneNumber.replaceAll("[^0-9]", "");

        if(correctFormat.length() == 10 || (correctFormat.length() == 11 && correctFormat.charAt(0) == '1')){
            if(correctFormat.length() == 11) {
                correctFormat = correctFormat.substring(1, correctFormat.length());
            }

            correctFormat = "(" + correctFormat.substring(0, 3) + ") " + correctFormat.substring(3,6) + "-" + correctFormat.substring(6,correctFormat.length());

            return correctFormat;
        } else {
            return null;
        }

    }

}


