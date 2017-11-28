package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Remindr;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.RemindrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private RemindrService remindrService = new RemindrService();

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


        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        remindr.setUser(user);

        Contact contact = user.getContact();
        String fullName = contact.getFirstName() + contact.getLastName();

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


        // parse into correct format for displaying in the view
        String startDate = remindrService.getFinalDate(remindr.getStartDateTime());
        String endDate = remindrService.getFinalDate(remindr.getEndDateTime());
        String startTime = remindrService.getTime(remindr.getStartDateTime());
        String endTime = remindrService.getTime(remindr.getEndDateTime());

        model.addAttribute("remindr", remindr);
        model.addAttribute("remindrId", id);

        model.addAttribute("startdate", startDate);
        model.addAttribute("enddate", endDate);
        model.addAttribute("starttime", startTime);
        model.addAttribute("endtime", endTime);

        return "remindrs/show-remindr";
    }

    @GetMapping("/remindrs/{id}/edit")
    public String editPost(Model model, @PathVariable Long id) {
        model.addAttribute("remindr", remindrsRepository.findOne(id));

        return "remindrs/edit";
    }

    @PostMapping("/remindrs/{id}/edit")
    public String editPost(@Valid Remindr remindr, Errors validation, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        remindr.setUser(user);

        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("remindr", remindr);
            return "/remindrs/edit";
        }

        remindrsRepository.save(remindr);

        return "redirect:/remindrs";
    }

    @PostMapping("/remindrs/{id}/confirm-delete")
    public String confirmDeleteRemindr(@PathVariable Long id, Model model) {
        Iterable<Remindr> remindrs = remindrsRepository.findAll();
        model.addAttribute("remindrs", remindrs);

        return "/remindrs/confirm-delete";
    }

    @PostMapping("/remindrs/{id}/delete")
    public String deleteRemindr(@PathVariable Long id) {
        remindrsRepository.delete(id);

        return "redirect:/remindrs";
    }
}
