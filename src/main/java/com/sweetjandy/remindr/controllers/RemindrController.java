package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.*;
import com.sweetjandy.remindr.repositories.AlertsRepository;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.RemindrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public String showCreateRemindrForm(Model model, HttpServletResponse response) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Unauthorized";
        }


        model.addAttribute("remindr", new Remindr());

        return "remindrs/create";
    }

    @PostMapping("/remindrs/create")
    public String createRemindr(@Valid Remindr remindr, Errors validation, Model model, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

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
    public String showAddContactsToRemindrs(Model model, @PathVariable Long id, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        Remindr remindr = remindrsRepository.findOne(id);

        List<Contact> contacts = user.getContacts();
        model.addAttribute("contacts", contacts);
        model.addAttribute("remindr", remindr);

        return "/remindrs/add-contacts";
    }

    @PostMapping("/remindrs/{id}/add-contacts")
    public String addContactsToRemindrs (Model model, @PathVariable Long id, @RequestParam (name = "contacts") String[] contactIds, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

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
    public String showAddAlertsToRemindrs (Model model, @PathVariable Long id, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        RemindrAlerts remindrAlerts = new RemindrAlerts();
        remindrAlerts.setId(id);

        model.addAttribute("remindr", remindrAlerts);

        return "/remindrs/add-alerts";
    }

    @PostMapping("/remindrs/{id}/add-alerts")
    public String addAlertsToRemindrs (RemindrAlerts alertTimes, @PathVariable Long id, Model model, @RequestParam(name="alerts")String[] alertValues, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

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

        remindrsRepository.save(currentRemindr);

        return "redirect:/remindrs";
    }

    @GetMapping("/remindrs/{id}/edit-alerts")
    public String showEditAlerts (Model model, @PathVariable Long id, HttpServletResponse response) {
//        RemindrAlerts remindrAlerts = new RemindrAlerts();
//        remindrAlerts.setId(id);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }


        Remindr remindr = remindrsRepository.findOne(id);

        model.addAttribute("remindr", remindr);

        return "/remindrs/edit-alerts";
    }


    @PostMapping("/remindrs/{id}/edit-alerts")
    public String editAlerts(RemindrAlerts alertTimes, @PathVariable Long id, Model model, @RequestParam(name="alerts")String[] alertValues, HttpServletResponse response) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        Remindr remindr = remindrsRepository.findOne(id);
        remindr.getAlerts().clear();

        for (String alert : alertValues) {
            Alert newAlert = new Alert();
            newAlert.setRemindr(remindr);

            for (AlertTime alertTime : AlertTime.values()) {
                if (alertTime.name().equalsIgnoreCase(alert)) {
                    newAlert.setAlertTime(alertTime);
                }
            }

            remindr.getAlerts().add(newAlert);
        }

        remindrsRepository.save(remindr);

        return "redirect:/remindrs/" + remindr.getId();
    }

    @GetMapping("/remindrs")
    public String showAllRemindrs(Model model, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        user = usersRepository.findOne(user.getId());

        Iterable<Remindr> remindrs = user.getRemindrs();
        model.addAttribute("remindrs", remindrs);

        return "/remindrs/show-all-remindrs";
    }

    @GetMapping("/remindrs/{id}")
    public String showRemindr(@PathVariable Long id, Model model, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        Remindr remindr = remindrsRepository.findOne(id);

        // parse into correct format for displaying in the view
        String startDate = remindrService.getFinalDate(remindr.getStartDateTime());
        String endDate = remindrService.getFinalDate(remindr.getEndDateTime());
        String startTime = remindrService.getTime(remindr.getStartDateTime());
        String endTime = remindrService.getTime(remindr.getEndDateTime());

        model.addAttribute("remindr", remindr);
        model.addAttribute("remindrId", id);

        // get number of existing alerts for remindr
        List<Alert> alertList =  remindr.getAlerts();
        String[] readableAlerts = {"At time of event", "15 minutes before", "30 minutes before", "1 hour before", "1 day before", "1 week before"};
        List<String> alertsToView = new ArrayList<String>();
        int numberOfAlerts = alertList.size();

        // convert alerts to choices
        for (Alert alert : alertList) {
            switch (alert.getAlertTime().toString()) {
                case "ZERO":
                    alertsToView.add(readableAlerts[0]);
                    break;
                case "FIFTEEN":
                    alertsToView.add(readableAlerts[1]);
                    break;
                case "THIRTY":
                    alertsToView.add(readableAlerts[2]);
                    break;
                case "HOUR":
                    alertsToView.add(readableAlerts[3]);
                    break;
                case "DAY":
                    alertsToView.add(readableAlerts[4]);
                    break;
                case "WEEK":
                    alertsToView.add(readableAlerts[5]);
                    break;
            }
        }

        // pass into view
        model.addAttribute("startdate", startDate);
        model.addAttribute("enddate", endDate);
        model.addAttribute("starttime", startTime);
        model.addAttribute("endtime", endTime);
        model.addAttribute("numberOfAlerts", numberOfAlerts);
        model.addAttribute("alerts", alertsToView);

        return "remindrs/show-remindr";
    }

    @GetMapping("/remindrs/{id}/edit")
    public String editPost(Model model, @PathVariable Long id, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        model.addAttribute("remindr", remindrsRepository.findOne(id));

        return "remindrs/edit";
    }

    @PostMapping("/remindrs/{id}/edit")
    public String editPost(@Valid Remindr remindr, Errors validation, Model model, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, remindr.getId())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        Remindr remindr = remindrsRepository.findOne(id);

        user.getRemindrs().remove(remindrsRepository.findOne(id));
        usersRepository.save(user);

        remindrsRepository.delete(id);

        return "redirect:/remindrs";
    }
}
