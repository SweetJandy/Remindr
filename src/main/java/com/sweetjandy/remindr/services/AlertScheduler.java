package com.sweetjandy.remindr.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetjandy.remindr.models.Appointment;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.Date;

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
                String dateTime = appointment.getDate();
                String[] dateAndTime = dateTime.split(" ");
                String date = dateAndTime[0];
                String time = dateAndTime[1];
                String title = appointment.getTitle();
                String description = appointment.getDescription();
                String sender = appointment.getSender();
                String location = appointment.getLocation();

                DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-yyyy hh:mma");

                String messageBody = "Hi " + name + ", just a quick remindr for " + sender + "'s '" + title + "' at " + location + " on " + date + " at " + time + ".";

                try {
                    Message message = Message
                            .creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioNumber), messageBody)
                            .create();
                } catch(TwilioException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
