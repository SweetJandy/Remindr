package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Remindr;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.RemindrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
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

    private boolean isYourRemindr (User user, long remindrId) {
        return user.getRemindrs().stream().filter(r -> r.getId() == remindrId).count() > 0;
    }

    @GetMapping("/remindrs/create")
    public String showCreateRemindrForm(Model model) {
        model.addAttribute("remindr", new Remindr());

        return "remindrs/create";
    }

    @PostMapping("/remindrs/create")
    public String createRemindr(@Valid Remindr remindr, Errors validation, Model model) {


        User user = usersRepository.findOne(1L);
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

        return "redirect:/remindrs/add-contacts";
    }

    @GetMapping("/remindrs/add-contacts")
    public String showAddContactsToRemindrs(Model model, Remindr remindr) {

        User user = usersRepository.findOne(2L);

        List<Contact> contacts = user.getContacts();
        model.addAttribute("contacts", contacts);
        model.addAttribute("remindr", remindr);

        return "/remindrs/add-contacts";
    }

    @PostMapping("/remindrs/add-contacts")
    public String addContactsToRemindrs (Model model) {
        Remindr remindr = remindrsRepository.findOne(11L);
        model.addAttribute("remindr", remindr);
        remindrsRepository.save(remindr);

        return "redirect: /remindrs";
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
        User user = usersRepository.findOne(2L);
        remindr.setUser(user);

        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("remindr", remindr);
            return "/remindrs/edit";
        }

        remindrsRepository.save(remindr);

        return "redirect:/remindrs";
    }


    @RequestMapping(value = "/remindrs/{id}/delete", method = RequestMethod.POST)
    public String deleteRemindr(@PathVariable long id, HttpServletResponse response) throws IOException {
        Remindr remindr = remindrsRepository.findOne(id);
        User user = usersRepository.findOne(1L);

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

//        Removes from the user's list of remindrs
        user.getRemindrs().remove(remindrsRepository.findOne(id));
//        So the db knows you removed the reminder from the user’s list of reminders
        usersRepository.save(user);
//        Deletes the remindr from the db
        remindrsRepository.delete(id);

        return "redirect:/remindrs";
    }
}
