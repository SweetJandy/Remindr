package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Remindr;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RemindrController {
    private RemindrsRepository repository;

    @Autowired
    public RemindrController(RemindrsRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/remindrs/create")
    public String showCreateRemindrForm(Model model) {
        model.addAttribute("remindr", new Remindr());

        return "remindrs/create";
    }

    @PostMapping("/remindrs/create")
    public String createRemindr(@Valid Remindr remindr, Model model) {
        model.addAttribute("remindr", remindr);

        repository.save(remindr);

        return "redirect:/remindrs/showall";
    }

    @GetMapping("/remindrs/showall")
    public String showAllRemindrs() {


        return "/remindrs/show-all-remindrs";
    }
}
