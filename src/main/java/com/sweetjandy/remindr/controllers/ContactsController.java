package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
//import com.sweetjandy.remindr.services.GooglePeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;


@Controller
public class ContactsController {
    private final ContactsRepository contactsRepository;
    private final UsersRepository usersRepository;
//    private final GooglePeopleService googlePeopleService;
//
    @Autowired
    public ContactsController(ContactsRepository contactsRepository, UsersRepository usersRepository)
//            , GooglePeopleService googlePeopleService)
    {
        this.contactsRepository = contactsRepository;
        this.usersRepository = usersRepository;
//        this.googlePeopleService = googlePeopleService;
    }

    @GetMapping("/contact/{id}")
    public String viewIndividualContact(@PathVariable long id, Model viewModel) {
        User user = usersRepository.findOne(1L);
        // use the contacts repository to find one contact by its id
        Contact contact = contactsRepository.findOne(id);

        // save the result in a variable contact
        viewModel.addAttribute("contact", contact); // replace null with the variable contact
//        viewModel.addAttribute("contact", contact);
        return "users/view-contact";
    }



    @GetMapping("/contacts")
    public String viewAllContacts(Model viewModel) {
        Iterable<Contact> contacts = contactsRepository.findAll();
        viewModel.addAttribute("contact", contacts);
        return "users/contacts";
    }

    @GetMapping("/contacts/add")
    public String showAddContactsForm(Model viewModel) throws IOException {
//        String authorizationUrl = googlePeopleService.setUp();
//        viewModel.addAttribute("authorizationUrl", authorizationUrl);

        viewModel.addAttribute("contact", new Contact());

        return "users/add-contacts";
    }

    @PostMapping("/contacts/add")
    public String addContactForm(@Valid Contact contact, Errors validation, Model viewModel) {
    //hardcoded until security measures are placed.
        User user = usersRepository.findOne(2L);
        //contact.setUser(user);


        Contact existingPhoneNumber = contactsRepository.findByPhoneNumber(contact.getPhoneNumber());
        contact.setGoogleContact((long) (Math.random() * (double) Long.MAX_VALUE));
        contact.setOutlookContact((long) (Math.random() * (double) Long.MAX_VALUE));
        user.setContact(contact);


        if (existingPhoneNumber != null) {
            validation.rejectValue(
                    "phoneNumber",
                    "contact.phoneNumber",
                    "Phone number is already taken"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("contact", contact);
            return "users/add-contacts";
        }
        contactsRepository.save(contact);
        return "users/contacts";
    }
}