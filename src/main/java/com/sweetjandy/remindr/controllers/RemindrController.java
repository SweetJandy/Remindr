package com.sweetjandy.remindr.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.sweetjandy.remindr.models.*;
import com.sweetjandy.remindr.repositories.AlertsRepository;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.AppointmentUtility;
import com.sweetjandy.remindr.services.RemindrService;
import com.sweetjandy.remindr.services.ScheduleService;
import com.sweetjandy.remindr.services.TwilioService;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.RegEx;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.validation.Validator;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class
RemindrController {
    private RemindrsRepository remindrsRepository;
    private UsersRepository usersRepository;
    private ContactsRepository contactsRepository;
    private AlertsRepository alertsRepository;
    private RemindrService remindrService;
    private ScheduleService scheduleService;
    private TwilioService twilioService;
    private AppointmentUtility appointmentUtility;

    @Autowired
    public RemindrController(RemindrsRepository remindrsRepository, UsersRepository usersRepository, ContactsRepository contactsRepository, AlertsRepository alertsRepository, ScheduleService scheduleService, RemindrService remindrService, TwilioService twilioService, AppointmentUtility appointmentUtility) {
        this.contactsRepository = contactsRepository;
        this.remindrsRepository = remindrsRepository;
        this.usersRepository = usersRepository;
        this.alertsRepository = alertsRepository;
        this.scheduleService = scheduleService;
        this.remindrService = remindrService;
        this.twilioService = twilioService;
        this.appointmentUtility = appointmentUtility;
    }


    private boolean isYourRemindr (User user, long remindrId) {
        return user.getRemindrs().stream().filter(r -> r.getId() == remindrId).count() > 0;
    }

    @GetMapping("/remindrs/create")
    public String showCreateRemindrForm(Model model, HttpServletResponse response) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Unauthorized";
        }

        model.addAttribute("remindr", new Remindr());

        return "remindrs/create";
    }

    @PostMapping("/remindrs/create")
    public String createRemindr(
            @Valid Remindr remindr,
            Errors validation,
            Model model,
            HttpServletResponse response,
            @RequestParam(name="timezone") String timezoneValue,
            @RequestParam ("startDateTime") String startDateTime,
            @RequestParam("endDateTime") String endDateTime
    ) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        user = usersRepository.findOne(user.getId());
        remindr.setUser(user);

        Contact contact = user.getContact();
        String fullName = contact.getFirstName() + contact.getLastName();

        model.addAttribute("contact", contact);


//        // check for empty date and time
        if (Strings.isNullOrEmpty(startDateTime)) {
            validation.rejectValue(
                    "startDateTime",
                    "remindr.startDateTime",
                    "The date cannot be empty"
            );
            return "/remindrs/create";
        }


        Date curTime = new Date();

        String startDateMonth = remindrService.getMonth(startDateTime);
        String startDateDay = remindrService.getDate(startDateTime);
        String startDateYear = remindrService.getYear(startDateTime);
        String startTime = remindrService.getTime(startDateTime);

        // start date
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, Integer.parseInt(startDateYear));
        start.set(Calendar.MONTH, Integer.parseInt(startDateMonth)-1);
        start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDateDay));
        start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.substring(0,2)));
        start.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3)));

        Date userStartTime = start.getTime();

        if (!curTime.before(userStartTime)) {
            validation.rejectValue(
                    "startDateTime",
                    "remindr.startDateTime",
                    "Event can't be in the past"
            );
            return "remindrs/create";
        }

        if (!Strings.isNullOrEmpty(endDateTime)) {

            // TIME VALIDATION

            String endDateMonth = remindrService.getMonth(endDateTime);
            String endDateDay = remindrService.getDate(endDateTime);
            String endDateYear = remindrService.getYear(endDateTime);

            startTime = remindrService.getTime(startDateTime);
            String endTime = remindrService.getTime(endDateTime);

            // remove colon
            startTime = startTime.replace(":", "");
            endTime = endTime.replace(":", "");

            // check if end date is before start date
            if (
                    (endDateYear != null && startDateYear != null) &&
                    (Integer.parseInt(endDateYear) - Integer.parseInt(startDateYear) < 0)) {
                validation.rejectValue(
                        "endDateTime",
                        "remindr.endDateTime",
                        "The end date can't be before the start date"
                );

                return "remindrs/create";
            } else if (
                    (endDateYear != null && startDateYear != null) &&
                    (Integer.parseInt(endDateYear) - Integer.parseInt(startDateYear)) == 0) {

                // if can't validate with year, validate with MONTH
                if (
                        (endDateMonth != null && startDateMonth != null) &&
                        (Integer.parseInt(endDateMonth) - Integer.parseInt(startDateMonth) < 0)) {
                    validation.rejectValue(
                            "endDateTime",
                            "remindr.endDateTime",
                            "The end date can't be before the start date"
                    );

                    return "remindrs/create";
                } else if (
                        (endDateMonth != null && startDateMonth != null) &&
                            (Integer.parseInt(endDateMonth) - Integer.parseInt(startDateMonth) == 0)
                        ) {

                    // if can't validate with month, validate with day
                    if (
                            (endDateDay != null && startDateDay != null) &&
                                    (Integer.parseInt(endDateDay) - Integer.parseInt(startDateDay) < 0)) {
                        validation.rejectValue(
                                "endDateTime",
                                "remindr.endDateTime",
                                "The end date can't be before the start date"
                        );
                        return "remindrs/create";
                    } else if ((endDateDay != null && startDateDay != null) &&
                            (Integer.parseInt(endDateDay) - Integer.parseInt(startDateDay) == 0)) {
                        if (Integer.parseInt(endTime) - Integer.parseInt(startTime) < 0) {

                            validation.rejectValue(
                                    "endDateTime",
                                    "remindr.endDateTime",
                                    "The end date/time can't be before the start date"
                            );
                            return "remindrs/create";
                        }
                    }

                }

            }

            // end date
            Calendar end = Calendar.getInstance();
            end.set(Calendar.YEAR, Integer.parseInt(endDateYear));
            end.set(Calendar.MONTH, Integer.parseInt(endDateDay));
            end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDateMonth)-1);
            start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.substring(0,2)));
            start.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3)));
            Date userEndTime = end.getTime();


            if (!curTime.before(userEndTime)) {
                validation.rejectValue(
                        "endDateTime",
                        "remindr.endDateTime",
                        "Event can't be in the past"
                );
                return "remindrs/create";
            }

        }


        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("remindr", remindr);
            return "/remindrs/create";
        }


        // save timezone to remindr
        remindr.setTimeZone(timezoneValue);

        // SAVE REMINDR
        remindrsRepository.save(remindr);


        return "redirect:/remindrs/" + remindr.getId() + "/add-contacts";
    }

    @GetMapping("/remindrs/{id}/add-contacts")
    public String showAddContactsToRemindrs(Model model, @PathVariable Long id, HttpServletResponse response) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

//        old and new contacts
        List<Contact> contacts = contactsRepository.findByIdIn(list);

        List<Contact> newContacts = new ArrayList<>();

        for(Contact contact : contacts) {
        //if a contact's pending remindr is not the current remindr, or if the contact has no pending remindrs, or if the pending invite is for the current event and haven't responded yet
            if(contact.getPending() == null || !contact.getPending().equals(remindr) || contact.getRemindrContacts().stream().filter(r -> r.getRemindr().equals(contact.getPending())).findFirst().get().getInviteStatus() == null){

                if(!contact.isStop()) {
                    newContacts.add(contact); //sends invite
                }
            }
        }

        remindr.setContacts(contacts);

        remindrsRepository.save(remindr);

        for(Contact newContact : newContacts) {
            try {
                twilioService.sendInitialSMS(remindr, newContact);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



        return "redirect:/remindrs/" + remindr.getId() + "/edit-alerts";
    }

    @GetMapping("/remindrs/{id}/edit-alerts")
    public String showAddAlertsToRemindrs (Model model, @PathVariable Long id, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        RemindrAlerts remindrAlerts = new RemindrAlerts();
        remindrAlerts.setId(id);
        Remindr remindr = remindrsRepository.findOne(id);
        remindrAlerts.setAlertList(remindr.getAlerts());

        // List of alerts to alertTimes
        List<Alert> alerts = remindr.getAlerts();
        List<AlertTime> alertTimes = new ArrayList<>();
        for(Alert alert: alerts) {
            alertTimes.add(alert.getAlertTime());
        }

        remindrAlerts.setAlertTimes("Hello");

        remindrAlerts.ZERO = alertTimes.contains(AlertTime.ZERO);
        remindrAlerts.FIFTEEN = alertTimes.contains(AlertTime.FIFTEEN);
        remindrAlerts.THIRTY = alertTimes.contains(AlertTime.THIRTY);
        remindrAlerts.HOUR = alertTimes.contains(AlertTime.HOUR);
        remindrAlerts.DAY = alertTimes.contains(AlertTime.DAY);
        remindrAlerts.WEEK = alertTimes.contains(AlertTime.WEEK);

        model.addAttribute("remindr", remindrAlerts);

        return "/remindrs/edit-alerts";
    }

    @PostMapping("/remindrs/{id}/edit-alerts")
    public String addAlertsToRemindrs (RemindrAlerts alertTimes, @PathVariable Long id, Model model, @RequestParam(name="alerts")String[] alertValues, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
        currentRemindr = remindrsRepository.findLastInserted();
        for (Alert alert : currentRemindr.getAlerts()) {
            scheduleService.schedule(alert);
        }


        return "redirect:/remindrs/" + id;
    }

    @GetMapping("/remindrs")
    public String showAllRemindrs(Model model, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

        user = usersRepository.findOne(user.getId());

        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        if(!isYourRemindr(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        Remindr remindr = remindrsRepository.findOne(id);

        // parse into correct format for displaying in the view
        String startDate = remindrService.getFinalDate(remindr.getStartDateTime());
        String endDate = remindrService.getFinalDate(remindr.getEndDateTime());

        String startTime = remindrService.getTime(remindr.getStartDateTime());
        String finalStartTime = remindrService.convertTo12HrTime(startTime);
        String endTime = remindrService.getTime(remindr.getEndDateTime());
        String finalEndTime = remindrService.convertTo12HrTime(endTime);

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

        // CONTACTS
        List<Contact> contacts = remindr.getContacts();
        int contactSize = contacts.size();


        // PASSING INTO VIEW

        // REMINDR
        model.addAttribute("remindr", remindr);
        model.addAttribute("remindrId", id);
        // CONTACTS
        model.addAttribute("contacts", contacts);
        model.addAttribute("contactSize", contactSize);

        // TIME FORMATTING
        model.addAttribute("startdate", startDate);
        model.addAttribute("enddate", endDate);
        model.addAttribute("starttime", finalStartTime);
        model.addAttribute("endtime", finalEndTime);

        // ALERTS
        model.addAttribute("numberOfAlerts", numberOfAlerts);
        model.addAttribute("alerts", alertsToView);

        return "remindrs/show-remindr";
    }

    @GetMapping("/remindrs/{id}/edit")
    public String editPost(Model model, @Valid Remindr remindr, Errors validation, @PathVariable Long id, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        model.addAttribute("remindr", remindrsRepository.findOne(id));


        return "remindrs/edit";
    }

    @PostMapping("/remindrs/{id}/edit")
    public String editPost(@Valid Remindr remindr,
                           Errors validation,
                           @PathVariable Long id,
                           Model model,
                           HttpServletResponse response,
                           @RequestParam(name="timezone")String timezoneValue,
                           @RequestParam(name="startDateTime") String startDateTime,
                           @RequestParam(name="endDateTime") String endDateTime,
                           @RequestParam(name="title") String title,
                           @RequestParam(name="description") String description,
                           @RequestParam(name="location") String location,
                           @RequestParam (name="pic-input") String url)

    {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, remindr.getId())) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }


        // TIME VALIDATION
        // check for empty date and time
        if (Strings.isNullOrEmpty(startDateTime)) {
            validation.rejectValue(
                    "startDateTime",
                    "remindr.startDateTime",
                    "The date cannot be empty"
            );
            return "/remindrs/edit";
        }

        remindr = remindrsRepository.findOne(id);
        model.addAttribute("remindr", remindr);


        Date curTime = new Date();

        String startDateMonth = remindrService.getMonth(startDateTime);
        String startDateDay = remindrService.getDate(startDateTime);
        String startDateYear = remindrService.getYear(startDateTime);
        String startTime = remindrService.getTime(startDateTime);

        // start date
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, Integer.parseInt(startDateYear));
        start.set(Calendar.MONTH, Integer.parseInt(startDateMonth) - 1);
        start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDateDay));
        start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.substring(0, 2)));
        start.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3)));

        Date userStartTime = start.getTime();

        if (!curTime.before(userStartTime)) {
            validation.rejectValue(
                    "startDateTime",
                    "remindr.startDateTime",
                    "Event can't be in the past"
            );
            return "/remindrs/edit";
        }

        if (!Strings.isNullOrEmpty(endDateTime)) {

            // TIME VALIDATION

            String endDateMonth = remindrService.getMonth(endDateTime);
            String endDateDay = remindrService.getDate(endDateTime);
            String endDateYear = remindrService.getYear(endDateTime);

            startTime = remindrService.getTime(startDateTime);
            String endTime = remindrService.getTime(endDateTime);

            // remove colon
            startTime = startTime.replace(":", "");
            endTime = endTime.replace(":", "");

            // check if end date is before start date
            if (
                    (endDateYear != null && startDateYear != null) &&
                            (Integer.parseInt(endDateYear) - Integer.parseInt(startDateYear) < 0)) {
                validation.rejectValue(
                        "endDateTime",
                        "remindr.endDateTime",
                        "The end date can't be before the start date"
                );

                return "/remindrs/edit";
            } else if (
                    (endDateYear != null && startDateYear != null) &&
                            (Integer.parseInt(endDateYear) - Integer.parseInt(startDateYear)) == 0) {

                // if can't validate with year, validate with MONTH
                if (
                        (endDateMonth != null && startDateMonth != null) &&
                                (Integer.parseInt(endDateMonth) - Integer.parseInt(startDateMonth) < 0)) {
                    validation.rejectValue(
                            "endDateTime",
                            "remindr.endDateTime",
                            "The end date can't be before the start date"
                    );

                    return "/remindrs/edit";
                } else if (
                        (endDateMonth != null && startDateMonth != null) &&
                                (Integer.parseInt(endDateMonth) - Integer.parseInt(startDateMonth) == 0)
                        ) {

                    // if can't validate with month, validate with day
                    if (
                            (endDateDay != null && startDateDay != null) &&
                            (Integer.parseInt(endDateDay) - Integer.parseInt(startDateDay) < 0)) {
                        validation.rejectValue(
                                "endDateTime",
                                "remindr.endDateTime",
                                "The end date can't be before the start date"
                        );
                        return "/remindrs/edit";
                    } else if ((endDateDay != null && startDateDay != null) &&
                            (Integer.parseInt(endDateDay) - Integer.parseInt(startDateDay) == 0))
                    {
                        if (Integer.parseInt(endTime) - Integer.parseInt(startTime) < 0) {

                            validation.rejectValue(
                                    "endDateTime",
                                    "remindr.endDateTime",
                                    "The end date/time can't be before the start date"
                            );
                            return "/remindrs/edit";
                        }
                    }

                }
            }
            // end date
            Calendar end = Calendar.getInstance();
            end.set(Calendar.YEAR, Integer.parseInt(endDateYear));
            end.set(Calendar.MONTH, Integer.parseInt(endDateMonth)-1);
            end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDateDay));
            start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.substring(0, 2)));
            start.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3)));
            Date userEndTime = end.getTime();


            if (!curTime.before(userEndTime)) {
                validation.rejectValue(
                        "endDateTime",
                        "remindr.endDateTime",
                        "Event can't be in the past"
                );
                return "/remindrs/edit";
            }

            remindr.setEndDateTime(endDateTime);
        }


        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("remindr", remindr);
            return "/remindrs/edit";
        }

        remindr.setTimeZone(timezoneValue);
        remindr.setId(id);
        remindr.setUser(user);
        if (!Strings.isNullOrEmpty(description)) {
            remindr.setDescription(description);
        }

        // save timezone to remindr
            remindr.setId(id);
            remindr.setUser(user);
            remindr.setTitle(title);
            remindr.setStartDateTime(startDateTime);
            remindr.setTimeZone(timezoneValue);
            remindr.setLocation(location);
            remindr.setUploadPath(url);


        // SAVE REMINDR
        remindrsRepository.save(remindr);
        return "redirect:/remindrs/" + id;

    }

    @GetMapping("/remindrs/{id}/send")
    public String sendNow(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = usersRepository.findOne(user.getId());

        Remindr remindr = remindrsRepository.findOne(id);

        if(!isYourRemindr(user, remindr.getId())) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        Alert alert = new Alert();
        alert.setRemindr(remindr);
        alert.setAlertTime(AlertTime.ZERO);
        List<Appointment> appointments = appointmentUtility.convertAlertToAppointments(alert);

        for(Appointment appointment : appointments) {
            try {
                DateTimeZone zone = DateTimeZone.forID(appointment.getTimeZone());
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
                String d = formatter.print(DateTime.now().withZone(zone));
                d = appointmentUtility.convertDate(d, appointment.getTimeZone());
                appointment.setDate(d);
                scheduleService.scheduleJob(appointment);
            }
            catch(ParseException ex) {
                ex.printStackTrace();
            }
            catch(JsonProcessingException jx) {
                jx.printStackTrace();
            }
        }


        return "redirect:/remindrs/{id}";
    }

    @PostMapping("/remindrs/{id}/edit-pic")
    public String postPic (@PathVariable Long id, @RequestParam(name="pic-input") String url) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = usersRepository.findOne(user.getId());

        Remindr remindr = remindrsRepository.findOne(id);

        if(!isYourRemindr(user, remindr.getId())) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        remindr.setUploadPath(url);
        remindrsRepository.save(remindr);

        return "redirect:/remindrs/create";
    }


    @RequestMapping(value = "/remindrs/{id}/delete", method = RequestMethod.POST)
    public String deleteRemindr(@PathVariable long id, HttpServletResponse response) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        if(!isYourRemindr(user, id)) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "You do not own this remindr.";
        }

        Remindr remindr = remindrsRepository.findOne(id);

        user.getRemindrs().remove(remindrsRepository.findOne(id));
        usersRepository.save(user);

        remindrsRepository.delete(id);

        return "redirect:/remindrs";
    }
}
