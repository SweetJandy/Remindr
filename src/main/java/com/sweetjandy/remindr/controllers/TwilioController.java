package com.sweetjandy.remindr.controllers;
import com.sweetjandy.remindr.services.TwilioService;
import org.springframework.stereotype.Controller;

import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class TwilioController {

    private final TwilioService twilioSvc;

    public TwilioController(TwilioService twilioservice) {
        this.twilioSvc = twilioservice;
    }

    @GetMapping ("/sendSMS")
    @ResponseBody
    public String sendSMS () {

        twilioSvc.sendASMS(new PhoneNumber(""), new PhoneNumber("+12104053232"), "https://cdn.pixabay.com/photo/2013/12/12/03/08/kitten-227009_960_720.jpg");

        return "";
    }
}
