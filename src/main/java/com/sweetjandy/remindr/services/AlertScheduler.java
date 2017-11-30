package com.sweetjandy.remindr.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetjandy.remindr.models.Alert;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.repositories.AlertsRepository;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.twilio.Twilio;
import com.twilio.appointmentreminders.models.Appointment;
import com.twilio.appointmentreminders.models.AppointmentService;
import com.twilio.appointmentreminders.util.AppSetup;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;

public class AlertScheduler implements Job {
    
    private static AppSetup appSetup = new AppSetup();

    public static final String ACCOUNT_SID = appSetup.getAccountSid();
    public static final String AUTH_TOKEN = appSetup.getAuthToken();
    public static final String TWILIO_NUMBER = appSetup.getTwilioPhoneNumber();

    public AlertScheduler() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        AppSetup appSetup = new AppSetup();

//        EntityManagerFactory factory = appSetup.getEntityManagerFactory();
//        AppointmentService service = new AppointmentService(factory.createEntityManager());

        // Initialize the Twilio client
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String appointmentJson = dataMap.getString("appointment");

//        String[] appointmentIdArray = appointmentId.split("_");
//        String alertId = appointmentIdArray[0];
//        Alert alert = alertsRepository.findOne(Long.parseLong(alertId));
//
//        String contactId = appointmentIdArray[1];
//        Contact contact = contactsRepository.findOne(Long.parseLong(contactId));

//        Appointment appointment = service.getAppointment(Long.parseLong(appointmentId, 10));
//        Appointment appointment = appointmentUtility.convertAlertAndContactToAppointment(alert, contact);
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
                            .creator(new PhoneNumber(phoneNumber), new PhoneNumber(TWILIO_NUMBER), messageBody)
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
