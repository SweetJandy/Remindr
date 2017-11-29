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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.String;

@Controller
public class GooglePeopleController {

    private final GooglePeopleService googlePeopleSvc;
    private ContactsRepository contactsRepository;
    private UsersRepository usersRepository;
//    private PeopleService peopleService;

    public GooglePeopleController(
        GooglePeopleService googlePeopleSvc,
        ContactsRepository contactsRepository,
        UsersRepository usersRepository
    ) {
        this.googlePeopleSvc = googlePeopleSvc;
        this.contactsRepository = contactsRepository;
        this.usersRepository = usersRepository;
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
    public String confirm(Model viewModel) throws IOException {

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
    public String viewContacts(@RequestParam(name = "token") String token, Model model) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Person> people = googlePeopleSvc.contacts(token);
        List<Contact> contacts = new ArrayList<>();
        for (Person person: people) {
            String phoneNumber = person.getPhoneNumbers().get(0).getValue();
            Contact contact = contactsRepository.findByPhoneNumber(phoneNumber);

            //making sure the contact doesn't already exist for the user
            if (contact == null) {
                contact = new Contact(
                        person.getNames().get(0).getGivenName(),
                        person.getNames().get(0).getFamilyName(),
                        phoneNumber
                );

//            contacts.add(new Contact(
//                    person.getNames().get(0).getGivenName(),
//                    person.getNames().get(0).getFamilyName(),
//                    person.getPhoneNumbers().get(0).getValue()));
//        }


//                //person.getResourceName();
// person.getResourceName() is the googlecontact id, but its a string with a few letters at the beginning.
//                contact.getUsers().add(user);
                contacts.add(contact);
            }
        }
                contactsRepository.save(contacts);
                user.getContacts().addAll(contacts);
                usersRepository.save(user);

                model.addAttribute("contacts", contacts);

//        return "users/google-contacts";
                return "redirect:/contacts";
            }
        }

//    @GetMapping("/google/contacts")
//    public String viewContacts(@RequestParam(name = "token") String token, Model model, List<Person> persons) throws IOException {
//        List<Person> people = googlePeopleSvc.contacts(token);
////        model.addAttribute("contacts", people);
////        personToContacts(people);
////        User user = usersRepository.findOne(1L);
//
//        List<Contact> contacts;
//
//        for(Person person : people) {
//            System.out.println(people.get(0).getNames().get(0).getGivenName());
//            System.out.println(people.get(0).getNames().get(0).getFamilyName());
//            System.out.println(people.get(0).getPhoneNumbers().get(0).getValue());

//            contacts.add(people.get(0).getNames().get(0).getGivenName(), people.get(0).getNames().get(0).getFamilyName(), people.get(0).getPhoneNumbers().get(0).getValue());

            //saves contact to database
//            contact.setFirstName(people.get(0).getNames().get(0).getGivenName());
//            contact.setLastName(people.get(0).getNames().get(0).getFamilyName());
//            contact.setPhoneNumber(people.get(0).getPhoneNumbers().get(0).getValue());

//            contactsRepository.save(contact);
//            user.getContacts().add(contact);
//            usersRepository.save(user);

//        model.addAttribute("contacts", people);
//
////        return "users/google-contacts";
//        return "redirect:/contacts";
//    }
//}

//    private List<Contact> personToContacts(List<Person> persons) {
//
//        User user = new User();
//        for (Person person: persons) {
//            Contact contact = new Contact();
//
//            //saves contact to database
//            contactsRepository.save(contact);
//            user.getContacts().add(contact);
//            usersRepository.save(user);
//
////            List<Name> names = person.getNames();
////            Name firstName = names.get(3);
////            Name lastName = names.get(4);
////            List phoneNumbers = person.getPhoneNumbers();
////            phoneNumber phoneNumber = phoneNumbers.get(3);
////            contact.setFirstName(firstName);
////            contact.setLastName(lastName);
////            contact.setPhoneNumber(phoneNumber);
//        }
//    return "redirect:/contacts";
//
//    }
//
//}
//
//contact contact = new contact
//contact.setfirstName(givenName)
//contact