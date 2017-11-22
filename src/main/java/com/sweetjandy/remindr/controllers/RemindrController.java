package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Remindr;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class RemindrController {
    private RemindrsRepository remindrsRepository;
    private UsersRepository usersRepository;
    private ContactsRepository contactsRepository;

    @Autowired
    public RemindrController(RemindrsRepository remindrsRepository, UsersRepository usersRepository) {
        this.remindrsRepository = remindrsRepository;
        this.usersRepository = usersRepository;
    }


    @GetMapping("/remindrs/create")
    public String showCreateRemindrForm(Model model) {
        model.addAttribute("remindr", new Remindr());

        return "remindrs/create";
    }

    @PostMapping("/remindrs/create")
    public String createRemindr(@Valid Remindr remindr, Errors validation, Model model) {


        User user = usersRepository.findOne(1L);
        Contact contact = user.getContact();

        remindr.setUser(user);

        model.addAttribute("contact", contact);

        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("remindr", remindr);
            return "/remindrs/create";
        }

        model.addAttribute("remindr", remindr);
        remindrsRepository.save(remindr);

        return "redirect:/remindrs";
    }

    @GetMapping("/remindrs")
    public String showAllRemindrs(Model model) {
        Iterable<Remindr> remindrs = remindrsRepository.findAll();
        model.addAttribute("remindrs", remindrs);

        return "/remindrs/show-all-remindrs";
    }

    @GetMapping("/remindrs/{id}")
    public String showRemindr(@PathVariable Long id, Model model) {
        Remindr remindr = remindrsRepository.findOne(id);


        model.addAttribute("remindr", remindr);
        model.addAttribute("remindrId", id);


        return "remindrs/show-remindr";
    }
}
