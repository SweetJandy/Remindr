package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.Valid;


@Controller
public class ContactsController {
    private final ContactsRepository contactsDao;

    @Autowired
    public ContactsController(ContactsRepository contactsDao) {
        this.contactsDao = contactsDao;
    }

    @GetMapping("/register")
    public String registerContact(Model viewModel) {
        viewModel.addAttribute("contact", new Contact());
        return "users/register";
    }
}
getMapping /contacts

return users/contacts