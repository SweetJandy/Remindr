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


        User user = usersRepository.findOne(2L);
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


        // parse into correct format for displaying in the view
        String startDateTime = remindr.getStartDateTime();
        String startYear = startDateTime.substring(0, 4);
        String startMonth = startDateTime.substring(5, 7);
        String startDate = startDateTime.substring(8, 10);

        // pass these to the view
        String startTime = startDateTime.substring(11);
        String finalStartDate = startMonth + "/" + startDate + "/" + startYear;


        String endDateTime = remindr.getEndDateTime();
        String endYear = endDateTime.substring(0, 4);
        String endMonth = endDateTime.substring(5, 7);
        String endDate = endDateTime.substring(8, 10);

        // pass these to the view
        String endTime = endDateTime.substring(11);
        String finalEndDate = endMonth + "/" + endDate + "/" + startYear;


        model.addAttribute("remindr", remindr);
        model.addAttribute("remindrId", id);
        model.addAttribute("startdate", finalStartDate);
        model.addAttribute("enddate", finalEndDate);
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
    public String editPost(@ModelAttribute Remindr remindr) {
        remindrsRepository.save(remindr);

        return "redirect:/remindrs";
    }

    @PostMapping("/remindrs/{id}/delete")
    public String deleteRemindr(@PathVariable Long id) {
        remindrsRepository.delete(id);

        return "redirect:/remindrs";
    }
}
