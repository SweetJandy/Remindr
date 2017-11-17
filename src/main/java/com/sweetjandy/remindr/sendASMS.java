package com.sweetjandy.remindr;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class sendASMS {
    private static final String ACCOUNT_SID = "AC0f7bcddec33dfc906df045a25f48525c";
    private static final String AUTH_TOKEN = "a0d098bd7f86b372f1c2e87b7dae6df2";

    public static void main (String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
                .creator(new PhoneNumber("+17203930339"), new PhoneNumber("+14158141829"),
                        "Tomorrow's forecast in Financial District, San Francisco is Clear")
                .setMediaUrl("https://climacons.herokuapp.com/clear.png")
                .create();

        System.out.println(message.getSid());
    }


}
