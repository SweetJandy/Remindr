package com.sweetjandy.remindr.controllers;
import com.sweetjandy.remindr.services.TwilioService;
import com.twilio.twiml.Body;
import com.twilio.twiml.MessagingResponse;
import org.springframework.stereotype.Controller;

import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class TwilioController {

    private final TwilioService twilioSvc;

    public TwilioController(TwilioService twilioservice) {
        this.twilioSvc = twilioservice;
    }

    @GetMapping ("/sendSMS")
    @ResponseBody
    public String sendSMS () {

        return twilioSvc.sendASMS(new PhoneNumber("+17203930339"), new PhoneNumber("+12104053232"), "https://cdn.pixabay.com/photo/2013/12/12/03/08/kitten-227009_960_720.jpg");
    }

    @RequestMapping(value ="/replySMS", produces = "text/xml")
    @ResponseBody
    public String replySMS() {
        Body body = new Body("Welcome to Remindr!");

        return twilioSvc.setResponse(body);

    }

    @RequestMapping(value ="/replySMS/{Body}", produces = "text/xml", method=RequestMethod.GET)
    @ResponseBody
    public String replySMS(
       @RequestParam (value="Body", required=false) String body) {
        System.out.println(body);

//        Body optIn = new Body("You have opted in to the Remindr.");
//        Body optOut = new Body("You have opted out of the Remindr.");
//
//        if (body.equalsIgnoreCase("yes")) {
//            return twilioSvc.setResponse(optIn);
//        } else if (body.equalsIgnoreCase("no")){
//            return twilioSvc.setResponse(optOut);
//        }

        return "";

    }


}
