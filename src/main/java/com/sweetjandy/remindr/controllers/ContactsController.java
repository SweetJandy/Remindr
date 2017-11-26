package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
//import com.sweetjandy.remindr.services.GooglePeopleService;
import com.sweetjandy.remindr.services.GooglePeopleService;
import com.sweetjandy.remindr.services.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.IOException;


@Controller
public class ContactsController {
    private final ContactsRepository contactsRepository;
    private final UsersRepository usersRepository;
    private final GooglePeopleService googlePeopleService;

    //
    @Autowired
    public ContactsController(ContactsRepository contactsRepository, UsersRepository usersRepository, GooglePeopleService googlePeopleService) {
        this.contactsRepository = contactsRepository;
        this.usersRepository = usersRepository;
        this.googlePeopleService = googlePeopleService;
    }

    @GetMapping("/contact/{id}")
    public String viewIndividualContact(@PathVariable long id, Model viewModel) {
        User user = usersRepository.findOne(3L);

        // use the contacts repository to find one contact by its id
        Contact contact = contactsRepository.findOne(id);

        Contact usersContact = contactsRepository.findContactFor(user.getId(), contact.getId());
        if (usersContact == null) {
            return "redirect:/";
        }

        // save the result in a variable contact
        viewModel.addAttribute("contact", contact); // replace null with the variable contact
        //        viewModel.addAttribute("contact", contact);
        return "users/view-contact";
    }

    @GetMapping("/contacts")
    public String viewAllContacts(Model viewModel) {

        User user = usersRepository.findOne(1L);

        Iterable<Contact> usersContacts = contactsRepository.findAllContactsFor(user.getId());
        if (usersContacts == null) {
            return "redirect:/";
        }
        viewModel.addAttribute("contacts", usersContacts);
        return "users/contacts";
    }

    @GetMapping("/contacts/add")
    public String showAddContactsForm(Model viewModel) throws IOException {
        String authorizationUrl = googlePeopleService.setUp();
        viewModel.addAttribute("authorizationUrl", authorizationUrl);

        viewModel.addAttribute("contact", new Contact());

        return "users/add-contacts";
    }

    @PostMapping("/contacts/add")
    public String addContactForm(@Valid Contact contact, Errors validation, Model viewModel) {
        //hardcoded until security measures are placed.
        User user = usersRepository.findOne(2L);
        //contact.setUser(user);

        Contact existingPhoneNumber = contactsRepository.findByPhoneNumber(contact.getPhoneNumber());
        // setting to random number to avoid defaulting to 0, since field is unique
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
        boolean validated = PhoneService.validatePhoneNumber(user.getContact().getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "phoneNumber",
                    "phoneNumber",
                    "Invalid format: (xxx)xxxxxxx"
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

    @GetMapping("/contacts.json")
    @ResponseBody
    public Iterable<Contact> viewAllContactsInJSONFormat() {
        return contactsRepository.findAll();
    }

    @GetMapping("/contacts/ajax")
    public String viewAllContactsWithAjax() {
        return "users/ajax";
    }
}