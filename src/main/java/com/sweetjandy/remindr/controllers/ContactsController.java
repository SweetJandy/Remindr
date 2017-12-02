package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Remindr;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.GooglePeopleService;
import com.sweetjandy.remindr.services.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@Controller
public class ContactsController {
    private final ContactsRepository contactsRepository;
    private final UsersRepository usersRepository;
    private final GooglePeopleService googlePeopleService;
    private PhoneService phoneService;

    @Autowired
    public ContactsController(ContactsRepository contactsRepository, UsersRepository usersRepository, GooglePeopleService googlePeopleService, PhoneService phoneService) {
        this.contactsRepository = contactsRepository;
        this.usersRepository = usersRepository;
        this.googlePeopleService = googlePeopleService;
        this.phoneService = phoneService;
    }

    private boolean isInContacts(User user, long contactId) {
        return user.getContacts().stream().filter(c -> c.getId() == contactId).count() > 0;
    }

    @GetMapping("/contacts/{id}")
    public String viewIndividualContact(@PathVariable long id, Model viewModel, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        user = usersRepository.findOne(user.getId());
        // send back Http unauthorized if not one of user's contacts (accessing directly from url)
        if (!isInContacts(user, id)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        // use the contacts repository to find one contact by its id
        Contact contact = contactsRepository.findOne(id);


        // save the result in a variable contact
        viewModel.addAttribute("contact", contact); // replace null with the variable contact
        return "users/view-contact";
    }

    @GetMapping("/contacts")
    public String viewAllContacts(Model viewModel, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        user = usersRepository.findOne(user.getId());

        viewModel.addAttribute("contacts", user.getContacts());
        return "users/contacts";
    }

    @GetMapping("/contacts/add")
    public String showAddContactsForm(Model viewModel, HttpServletResponse response, @RequestParam(required=false) Long from) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        String authorizationUrl = googlePeopleService.setUp();
        viewModel.addAttribute("authorizationUrl", authorizationUrl);

        viewModel.addAttribute("contact", new Contact());
        viewModel.addAttribute("from", from);

        return "users/add-contacts";
    }

    @PostMapping("/contacts/add")
    public String addContactForm(@Valid final Contact contact, Errors validation, Model viewModel, HttpServletResponse response, @RequestParam(required=false, name="from") Long from) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        user = usersRepository.findOne(user.getId());


//        returns amount of contacts that are duplicated by phone number
        long duplicates = user.getContacts().stream().filter(c -> c.getPhoneNumber().equals(contact.getPhoneNumber())).count();

        if (duplicates > 0) {
            validation.rejectValue(
                    "phoneNumber",
                    "phoneNumber",
                    "Phone number is already in your contacts"
            );
        }

        // setting to random number to avoid defaulting to 0, since field is unique
        contact.setGoogleContact("" + (long) (Math.random() * (double) Long.MAX_VALUE));
        contact.setOutlookContact("" + (long) (Math.random() * (double) Long.MAX_VALUE));

        boolean validated = phoneService.validatePhoneNumber(contact.getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "phoneNumber",
                    "phoneNumber",
                    "Invalid format: (xxx) xxx-xxxx"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("contact", contact);
            return "users/add-contacts";
        }


        contact.setUser(user);
        user.getContacts().add(contact);
        usersRepository.save(user);

        if(from != null) {
            return "redirect:/remindrs/" + from + "/add-contacts";
        }

        return "redirect:/contacts";
    }

    @GetMapping("/contacts/{id}/edit")
    public String editContact(Model model, @PathVariable Long id, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if (!isInContacts(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "This contact is not in your contact list.";
        }

        model.addAttribute("contact", contactsRepository.findOne(id));

        return "users/edit-contact";
    }

    @PostMapping("/contacts/{id}/edit")
    public String editContact(@Valid Contact contact, Errors validation, Model viewModel, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = usersRepository.findOne(user.getId());

        if (!isInContacts(user, contact.getId())) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "This contact is not in your contact list.";
        }

//        returns amount of contacts that are duplicated by phone number
        Contact sameContact = contactsRepository.findOne(contact.getId());


        if (!sameContact.stillHasSameNumber(contact) && isDuplicated(contact, user)) {
            validation.rejectValue(
                    "phoneNumber",
                    "phoneNumber",
                    "Phone number is already in your contacts"
            );
        }

        boolean validated = phoneService.validatePhoneNumber(contact.getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "phoneNumber",
                    "phoneNumber",
                    "Invalid format: (xxx) xxx-xxxx"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("contact", contact);
            return "users/edit-contact";
        }
        contact.setUser(user);
        contactsRepository.save(contact);

        return "redirect:/contacts";
    }

    private boolean isDuplicated(@Valid Contact contact, User user) {
        return user.getContacts().stream().filter(c -> c.getPhoneNumber().equals(contact.getPhoneNumber())).count() > 0;
    }

    @RequestMapping(value = "/contacts/{id}/delete", method = RequestMethod.POST)
    public String deleteContact(@PathVariable long id, HttpServletResponse response) throws IOException {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if (!isInContacts(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "This contact is not in your contact list.";
        }

        user.getContacts().remove(contactsRepository.findOne(id));
        usersRepository.save(user);
        contactsRepository.delete(id);


        return "redirect:/contacts";
    }
}