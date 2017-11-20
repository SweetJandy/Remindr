package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;


@Controller
public class ContactsController {
    private final ContactsRepository contactsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ContactsController(ContactsRepository contactsRepository, UsersRepository usersRepository) {
        this.contactsRepository = contactsRepository;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/contacts")
    public String viewAllContacts(Model viewModel) {
        Iterable<Contact> contacts = contactsRepository.findAll();
        viewModel.addAttribute("contact", contacts);
        return "users/contacts";
    }

    @GetMapping("/contacts/add")
    public String showAddContactsForm(Model viewModel) {
        viewModel.addAttribute("contact", new Contact());
        return "users/add-contacts";
    }

    @PostMapping("/contacts/add")
    public String addContactForm(@Valid Contact contact, Errors validation, Model viewModel) {
//hardcode it until security measures are placed.
        User user = usersRepository.findOne(1L);
        //contact.setUser(user);
        user.setContact(contact);


        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("contact", contact);
            return "users/add-contacts";
        }
        contactsRepository.save(contact);
        return "redirect:contacts";
    }
}