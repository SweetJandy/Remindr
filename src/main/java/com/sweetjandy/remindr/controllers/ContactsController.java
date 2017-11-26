package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Remindr;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.GooglePeopleService;
import com.sweetjandy.remindr.services.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;


@Controller
public class ContactsController {
    private final ContactsRepository contactsRepository;
    private final UsersRepository usersRepository;
    private final GooglePeopleService googlePeopleService;

    @Autowired
    public ContactsController(ContactsRepository contactsRepository, UsersRepository usersRepository, GooglePeopleService googlePeopleService)
    {
        this.contactsRepository = contactsRepository;
        this.usersRepository = usersRepository;
        this.googlePeopleService = googlePeopleService;
    }

    @GetMapping("/contacts/{id}")
    public String viewIndividualContact(@PathVariable long id, Model viewModel) {
        User user = usersRepository.findOne(2L);

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
        User user = usersRepository.findOne(2L);

        Iterable<Contact> usersContacts = contactsRepository.getContactList(user.getId());
                if(usersContacts == null) {
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


    Contact existingPhoneNumber = contactsRepository.findByPhoneNumber(contact.getPhoneNumber());
        // setting to random number to avoid defaulting to 0, since field is unique
        contact.setGoogleContact((long) (Math.random() * (double) Long.MAX_VALUE));
        contact.setOutlookContact((long) (Math.random() * (double) Long.MAX_VALUE));

        Contact existingPhoneNumberInContacts = contactsRepository.findByPhoneNumber(contact.getPhoneNumber());

        if (existingPhoneNumberInContacts != null) {
            validation.rejectValue(
                    "phoneNumber",
                    "phoneNumber",
                    "Phone number is already in your contacts"
            );
        }

        boolean validated = PhoneService.validatePhoneNumber(contact.getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "phoneNumber",
                    "phoneNumber",
                    "Invalid format: (xxx)xxx-xxxx"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("contact", contact);
            return "users/add-contacts";
        }

        contact = contactsRepository.save(contact);
        contactsRepository.addContactToList(user.getId(), contact.getId());


        return "redirect:/contacts";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editPost(Model model, @PathVariable Long id) {
        model.addAttribute("contact", contactsRepository.findOne(id));

        return "users/edit-contact";
    }

    @PostMapping("/contacts/{id}/edit")
    public String editPost(@ModelAttribute Contact contact) {
        contactsRepository.save(contact);

        return "redirect:/contacts";
    }
}
