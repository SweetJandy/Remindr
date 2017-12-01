package com.sweetjandy.remindr.controllers;
import com.sweetjandy.remindr.services.TwilioService;
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

    public TwilioController(TwilioService twilioservice) {
        this.twilioSvc = twilioservice;
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

        String bodyParam = allRequestParams.get("Body");

        Body body = new Body("Sorry, try again.");
        Body optIn = new Body("Congratulations! You have opted in to receive remindrs. Text STOP to opt out at any time.");
        Body optOut = new Body("You have opted out of the Remindr.");

        if (bodyParam.equalsIgnoreCase("yes") || bodyParam.equalsIgnoreCase("y")) {

            return twilioSvc.setResponse(optIn);
        } else if (bodyParam.equalsIgnoreCase("no") || bodyParam.equalsIgnoreCase("stop") || bodyParam.equalsIgnoreCase("n")){
            return twilioSvc.setResponse(optOut);
        }

        return twilioSvc.setResponse(body);

    }

}
