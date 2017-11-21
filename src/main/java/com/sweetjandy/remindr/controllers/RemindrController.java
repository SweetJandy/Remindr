package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Remindr;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
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
    public String createRemindr(@Valid Remindr remindr, Errors validation, Model model) {
        //validation.rejectvalue("")

        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("remindr", remindr);
            return "/remindrs/create";
        }

        model.addAttribute("remindr", remindr);
        repository.save(remindr);

        return "redirect:/remindrs";
    }

    @GetMapping("/remindrs")
    public String showAllRemindrs(Model model) {
        Iterable<Remindr> remindrs = repository.findAll();
        model.addAttribute("remindrs", remindrs);

        return "/remindrs/show-all-remindrs";
    }

    @GetMapping("/remindrs/{id}")
    public String showRemindr(@PathVariable Long id, Model model) {
        Remindr remindr = repository.findOne(id);

        model.addAttribute("remindr", remindr);
        model.addAttribute("remindrId", id);


        return "remindrs/show-remindr";
    }
}
