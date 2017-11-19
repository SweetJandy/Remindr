package com.sweetjandy.remindr.services;


//import com.twilio.twiml.Message;
//import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.Body;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServlet;

@Service("twilioSvc")
public class TwilioService extends HttpServlet {

    @Value("${twilio-account-sid}")
    private String twiliosid;

    @Value("${twilio-auth-token}")
    private String twiliotoken;

    public String sendASMS (PhoneNumber phoneNumberTo, PhoneNumber phoneNumberFrom, String mediaURL) {
        Twilio.init(this.twiliosid, this.twiliotoken);

        com.twilio.rest.api.v2010.account.Message message = com.twilio.rest.api.v2010.account.Message
                    .creator(phoneNumberTo, phoneNumberFrom,
                            "You have been added to a Remindr! Would you like to receive alerts? Reply YES/NO.")
                    .setMediaUrl(mediaURL)
                    .create();

        return message.getSid();

    }

    public String setResponse (Body body) {
        try {

        com.twilio.twiml.Message message = new com.twilio.twiml.Message
                .Builder()
                .body(body)
                .build();

        MessagingResponse response = new MessagingResponse
                .Builder()
                .message(message)
                .build();

            System.out.println(response.toXml());
            return response.toXml();

        } catch (TwiMLException e) {
            e.printStackTrace();
        }

        return "";
    }


}
