package com.sweetjandy.remindr.services;


import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Remindr;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.Body;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServlet;
import java.text.ParseException;

@Service("twilioSvc")
public class TwilioService extends HttpServlet {

    @Value("${twilio-account-sid}")
    private String twiliosid;

    @Value("${twilio-auth-token}")
    private String twiliotoken;

    @Value("${twilio-number}")
    private String twilioNumber;

    private AppointmentUtility appointmentUtility;

    public TwilioService(AppointmentUtility appointmentUtility) {
        this.appointmentUtility = appointmentUtility;
    }

    public String sendInitialSMS (Remindr remindr, Contact contact) throws ParseException {
        Twilio.init(this.twiliosid, this.twiliotoken);

        String formattedDate = appointmentUtility.convertDate(remindr.getStartDateTime(),remindr.getTimeZone());

        String[] dateAndTime = formattedDate.split(" ");

        String date = dateAndTime[0];
        String time = dateAndTime[1];

        PhoneNumber phoneNumberTo = new PhoneNumber(contact.getPhoneNumber());
        PhoneNumber phoneNumberFrom = new PhoneNumber(twilioNumber);

        Message message = Message
                .creator(phoneNumberTo, phoneNumberFrom,
                        remindr.getUser().getContact().getFirstName() + " " + remindr.getUser().getContact().getLastName() + " has invited you to '" + remindr.getTitle() + "' on " + date + " at " + time + ". Accept reminders for this event? Reply yes/no.")
//                .setMediaUrl(mediaURL)
                .setProvideFeedback(true)
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

            return response.toXml();

        } catch (TwiMLException e) {
            e.printStackTrace();
        }

        return "";
    }


}
