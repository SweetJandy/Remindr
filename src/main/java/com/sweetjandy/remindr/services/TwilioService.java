package com.sweetjandy.remindr.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


@Service("twilioSvc")
public class TwilioService {

    @Value("${twilio-account-sid}")
    private String twiliosid;

    @Value("${twilio-auth-token}")
    private String twiliotoken;

    public void sendASMS (PhoneNumber phoneNumberTo, PhoneNumber phoneNumberFrom, String mediaURL) {
        Twilio.init(this.twiliosid, this.twiliotoken);

        Message message = Message
                .creator(phoneNumberTo, phoneNumberFrom,
                        "Hi Amy! It works!")
                .setMediaUrl(mediaURL)
                .create();

        System.out.println(message.getSid());

    }


}
