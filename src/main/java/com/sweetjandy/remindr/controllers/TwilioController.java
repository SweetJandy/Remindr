package com.sweetjandy.remindr.controllers;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.InviteStatus;
import com.sweetjandy.remindr.models.RemindrContact;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.services.AppointmentUtility;
import com.sweetjandy.remindr.services.PhoneService;
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

import java.util.List;
import java.util.Map;

import static ognl.DynamicSubscript.all;


@Controller
public class TwilioController {

    private final TwilioService twilioSvc;
    private ContactsRepository contactsRepository;
    private PhoneService phoneService;

    public TwilioController(TwilioService twilioservice, ContactsRepository contactsRepository, PhoneService phoneService) {
        this.twilioSvc = twilioservice;
        this.contactsRepository = contactsRepository;
        this.phoneService = phoneService;
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
    ) {

        String phoneNumber = allRequestParams.get("From");
        phoneNumber = phoneService.formatPhoneNumber(phoneNumber);
        Contact contact = contactsRepository.findByPhoneNumber(phoneNumber);

        String bodyParam = allRequestParams.get("Body");

        Body body = new Body("Sorry, try again.");
        Body optOut = new Body("No problem. We won't send you any reminders.");
        Body noInvites = new Body("Sorry, you don't have any event invites.");

        String response = twilioSvc.setResponse(body);

        if (bodyParam.equalsIgnoreCase("yes") || bodyParam.equalsIgnoreCase("y")) {
            // if they don't have any invites
            if(contact.getPending() == null) {
                response = twilioSvc.setResponse(noInvites);
            } else {
                Body optIn = new Body("Thanks, we'll send you reminders for '" + contact.getPending().getTitle() + "'.");
                response = twilioSvc.setResponse(optIn);
                RemindrContact remindrContact = contact.getRemindrContacts().stream().filter(r -> r.getRemindr().equals(contact.getPending())).findFirst().get();
                remindrContact.setInviteStatus(InviteStatus.ACCEPTED);
                contact.setPending(null);
                contactsRepository.save(contact);
            }
        } else if (bodyParam.equalsIgnoreCase("no") || bodyParam.equalsIgnoreCase("n")){
            response = twilioSvc.setResponse(optOut);
            RemindrContact remindrContact = contact.getRemindrContacts().stream().filter(r -> r.getRemindr().equals(contact.getPending())).findFirst().get();
            remindrContact.setInviteStatus(InviteStatus.DECLINED);
            contact.setPending(null);
            contactsRepository.save(contact);
        }

        return response;

    }

}
