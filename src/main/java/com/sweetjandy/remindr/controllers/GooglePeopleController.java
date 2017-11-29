package com.sweetjandy.remindr.controllers;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.GooglePeopleService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.lang.String;

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
    public String confirm(Model viewModel, HttpServletResponse response) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        String authorizationUrl = googlePeopleSvc.setUp();
        viewModel.addAttribute("authorizationUrl", authorizationUrl);
        viewModel.addAttribute("contact", new Contact());

        return "users/add-contacts";
    }

    @GetMapping("/google/contacts")
    public String viewContacts(@RequestParam(name = "token") String token) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = usersRepository.findOne(user.getId());

        List<Person> persons = googlePeopleSvc.contacts(token);
        List<Contact> contacts = user.getContacts();


        for (Person person : persons) {
//            //created own method name from previous logic by using command-option-m
            if (hasPhoneNumber(person) && hasName(person) && hasNonEmptyName(person)) {

                final String phoneNumber = formatPhoneNumber(person.getPhoneNumbers().get(0).getValue());
                if (isNotDuplicated(user, phoneNumber)) {
                    Contact contact = new Contact();
                    contact.setFirstName(person.getNames().get(0).getGivenName());
                    contact.setLastName(person.getNames().get(0).getFamilyName());
                    contact.setPhoneNumber(phoneNumber);
                    contacts.add(contact);
                    contact.setUser(user);
                }
            }
        }

        usersRepository.save(user);

        return "redirect:/contacts";
    }

    //        returns amount of contacts that are duplicated by phone number
    //created own method name from previous logic by using command-shift-m (isNotDuplicated)
    private boolean isNotDuplicated(User user, String phoneNumber) {
        return phoneNumber != null && user.getContacts().stream().filter(contact -> contact.getPhoneNumber().equals(phoneNumber)).count() == 0;
    }

    private boolean hasNonEmptyName(Person person) {
        return person.getNames().get(0).getGivenName() != null && !person.getNames().get(0).getGivenName().trim().equals("");
    }

    private boolean hasName(Person person) {
        return person.getNames() != null && person.getNames().size() > 0;
    }

    private boolean hasPhoneNumber(Person person) {
        return person.getPhoneNumbers() != null && person.getPhoneNumbers().size() > 0;
    }

    String formatPhoneNumber(String phoneNumber) {
        String correctFormat = phoneNumber.replaceAll("[^0-9]", "");

        if (correctFormat.length() == 10 || (correctFormat.length() == 11 && correctFormat.charAt(0) == '1')) {
            if (correctFormat.length() == 11) {
                correctFormat = correctFormat.substring(1, correctFormat.length());
            }

            correctFormat = "(" + correctFormat.substring(0, 3) + ") " + correctFormat.substring(3, 6) + "-" + correctFormat.substring(6, correctFormat.length());

            return correctFormat;
        } else {
            return null;
        }

    }

}


