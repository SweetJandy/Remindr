package com.sweetjandy.remindr.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetjandy.remindr.models.Appointment;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

public class AlertScheduler implements Job {

    public AlertScheduler() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String appointmentJson = dataMap.getString("appointment");

        String twilioAccountSid = dataMap.getString("twilioAccountSid");
        String twilioAuthToken = dataMap.getString("twilioAuthToken");
        String twilioNumber = dataMap.getString("twilioNumber");

        // Initialize the Twilio client
        Twilio.init(twilioAccountSid, twilioAuthToken);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Appointment appointment = objectMapper.readValue(appointmentJson, Appointment.class);
            if (appointment != null) {
                String name = appointment.getName();
                String phoneNumber = appointment.getPhoneNumber();
                String date = appointment.getDate();
                String messageBody = "Just a quick remindr, " + name + ", on " + date + " you have an event!";

                try {
                    Message message = Message
                            .creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioNumber), messageBody)
                            .create();
                    System.out.println("Message sent! Message SID: " + message.getSid());
                } catch(TwilioException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
