package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.*;
import com.sweetjandy.remindr.repositories.AlertsRepository;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class RemindrController {
    private RemindrsRepository remindrsRepository;
    private UsersRepository usersRepository;
    private ContactsRepository contactsRepository;
    private AlertsRepository alertsRepository;
    private RemindrService remindrService = new RemindrService();

    @Autowired
    public RemindrController(RemindrsRepository remindrsRepository, UsersRepository usersRepository, ContactsRepository contactsRepository, AlertsRepository alertsRepository) {
        this.contactsRepository = contactsRepository;
        this.remindrsRepository = remindrsRepository;
        this.usersRepository = usersRepository;
        this.alertsRepository = alertsRepository;
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

        remindrsRepository.save(remindr);

        return "redirect:/remindrs/" + remindr.getId() + "/add-contacts";
    }

    @GetMapping("/remindrs/{id}/add-contacts")
    public String showAddContactsToRemindrs(Model model, @PathVariable Long id) {

        User user = usersRepository.findOne(1L);
        Remindr remindr = remindrsRepository.findOne(id);

        List<Contact> contacts = user.getContacts();
        model.addAttribute("contacts", contacts);
        model.addAttribute("remindr", remindr);

        return "/remindrs/add-contacts";
    }

    @PostMapping("/remindrs/{id}/add-contacts")
    public String addContactsToRemindrs (Model model, @PathVariable Long id, @RequestParam (name = "contacts") String[] contactIds) {
        Remindr remindr = remindrsRepository.findOne(id);
        List<Long> list = new ArrayList<Long>();

        for(String contactId: contactIds) {
            if(contactId.equals("")){
                continue;
            }
            list.add(Long.parseLong(contactId));
        }

        List<Contact> contacts = contactsRepository.findByIdIn(list);

        remindr.setContacts(contacts);

        remindrsRepository.save(remindr);

        return "redirect:/remindrs/" + remindr.getId() + "/add-alerts";
    }

    @GetMapping("/remindrs/{id}/add-alerts")
    public String showAddAlertsToRemindrs (Model model, @PathVariable Long id) {
        RemindrAlerts remindrAlerts = new RemindrAlerts();
        remindrAlerts.setId(id);

        model.addAttribute("remindr", remindrAlerts);

        return "/remindrs/add-alerts";
    }

    @PostMapping("/remindrs/{id}/add-alerts")
    public String addAlertsToRemindrs (RemindrAlerts alertTimes, @PathVariable Long id, Model model, @RequestParam(name="alerts")String[] alertValues) {

        List<Alert> currentAlerts = alertsRepository.findForRemindr(id);
        for (Alert alert : currentAlerts) {
            alertsRepository.delete(alert);
        }

        Remindr currentRemindr = remindrsRepository.findOne(id);
        currentRemindr.getAlerts().clear();

        for (String alert : alertValues) {
            if(alert.equals("")){
                continue;
            }
            Alert newAlert = new Alert();
            newAlert.setRemindr(currentRemindr);

            for (AlertTime alertTime : AlertTime.values()) {
                if (alertTime.name().equalsIgnoreCase(alert)) {
                    newAlert.setAlertTime(alertTime);
                }
            }

            currentRemindr.getAlerts().add(newAlert);
        }

//        String[] alertTimesArray = alertTimes.getAlertTimes().split(",");
//
//        for (int i = 0; i < alertTimesArray.length-1; i++) {
//            Alert alert = new Alert();
////            alert.setId(id);
//            alert.setRemindr(currentRemindr);
//
//            for (AlertTime alertTime1 : AlertTime.values()) {
//                if (alertTime1.name().equals(alertTimesArray[i])) {
//                    alert.setAlertTime(alertTime1);
//                    currentRemindr.getAlerts().add(alert);
//                    break;
//                }
//            }
//        }

        remindrsRepository.save(currentRemindr);

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
//        User user = usersRepository.findOne(2L);
//
//        if(!isYourRemindr(user, id)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return "You do not own this remindr.";
//        }

//        user.getRemindrs().remove(remindrsRepository.findOne(id));
//        usersRepository.save(user);
        remindrsRepository.delete(id);

        return "redirect:/remindrs";
    }
}
