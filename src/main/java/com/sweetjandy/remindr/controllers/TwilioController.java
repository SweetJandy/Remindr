package com.sweetjandy.remindr.controllers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sweetjandy.remindr.models.*;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.RemindrsRepository;
import com.sweetjandy.remindr.services.AppointmentUtility;
import com.sweetjandy.remindr.services.PhoneService;
import com.sweetjandy.remindr.services.ScheduleService;
import com.sweetjandy.remindr.services.TwilioService;
import com.twilio.rest.chat.v1.service.channel.Invite;
import com.twilio.twiml.Body;
import com.twilio.twiml.MessagingResponse;
import org.springframework.stereotype.Controller;

import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ognl.DynamicSubscript.all;


@Controller
public class TwilioController {

    private final TwilioService twilioSvc;
    private ContactsRepository contactsRepository;
    private PhoneService phoneService;
    private ScheduleService scheduleService;
    private AppointmentUtility appointmentUtility;
    private RemindrsRepository remindrsRepository;

    public TwilioController(TwilioService twilioservice, ContactsRepository contactsRepository, PhoneService phoneService, ScheduleService scheduleService, AppointmentUtility appointmentUtility, RemindrsRepository remindrsRepository) {
        this.twilioSvc = twilioservice;
        this.contactsRepository = contactsRepository;
        this.phoneService = phoneService;
        this.scheduleService = scheduleService;
        this.appointmentUtility = appointmentUtility;
        this.remindrsRepository = remindrsRepository;
    }

//    @GetMapping ("/sendSMS")
//    @ResponseBody
//    public String sendSMS () {
//
//        return twilioSvc.sendInitialSMS(new PhoneNumber("+17203930339"), new PhoneNumber("+12104053232"), "https://cdn.pixabay.com/photo/2013/12/12/03/08/kitten-227009_960_720.jpg");
//
//    }

    @RequestMapping(value ="/replySMS", produces = "text/xml")
    @ResponseBody
    public String replySMS(
        @RequestParam Map<String, String> allRequestParams, ModelMap model
    ) throws JsonProcessingException, ParseException {

        String phoneNumber = allRequestParams.get("From");
        phoneNumber = phoneService.formatPhoneNumber(phoneNumber);
        List<Contact> contacts = contactsRepository.findByPhoneNumber(phoneNumber);
//        Contact contact = contacts.stream().filter(c -> c.getPending() != null).findFirst().get();
        Contact contact = null;

        // get all events
        List<Remindr> remindrList = new ArrayList<>();
        for (Remindr remindr : remindrsRepository.findAll()) {
            remindrList.add(remindr);
        }
        Collections.reverse(remindrList);

        for(Remindr remindr : remindrList) {
            for (Contact contact1 : contacts) {
                if (contact1.getPending() != null && contact1.getPending().getId() == remindr.getId()) {
                    contact = contact1;
                    break;
                }
            }
            if (contact != null) {
                break;
            }
        }

        String bodyParam = allRequestParams.get("Body");

        Body body = new Body("Sorry, try again.");
        Body optOut = new Body("No problem. We won't send you any reminders.");
        Body noInvites = new Body("Sorry, you don't have any event invites.");

        String response = twilioSvc.setResponse(body);

        final Contact contactFinal = contact;
        if (bodyParam.trim().equalsIgnoreCase("yes") || bodyParam.trim().equalsIgnoreCase("y")) {
            // if they don't have any invites
            if(contact == null || contact.getPending() == null) {
                response = twilioSvc.setResponse(noInvites);
            } else {
                Body optIn = new Body("Thanks, we'll send you reminders for '" + contact.getPending().getTitle() + "'.");
                response = twilioSvc.setResponse(optIn);
                RemindrContact remindrContact = contact.getRemindrContacts().stream().filter(r -> r.getRemindr().equals(contactFinal.getPending())).findFirst().get();
                remindrContact.setInviteStatus(InviteStatus.ACCEPTED);

                for(Alert alert : contact.getPending().getAlerts()) {
                    Appointment appointment = appointmentUtility.convertAlertAndContactToAppointment(alert, contact);
                    scheduleService.scheduleJob(appointment);
                }

                contact.setPending(null);
                contactsRepository.save(contact);
            }
        } else if (bodyParam.trim().equalsIgnoreCase("no") || bodyParam.trim().equalsIgnoreCase("n")){
            if(contact == null || contact.getPending() == null) {
                response = twilioSvc.setResponse(noInvites);
            } else {
                response = twilioSvc.setResponse(optOut);
                RemindrContact remindrContact = contact.getRemindrContacts().stream().filter(r -> r.getRemindr().equals(contactFinal.getPending())).findFirst().get();
                remindrContact.setInviteStatus(InviteStatus.DECLINED);
                contact.setPending(null);
                contactsRepository.save(contact);
            }
        }

        return response;

    }

}
