package com.sweetjandy.remindr.services;

import com.sweetjandy.remindr.models.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import sun.java2d.pipe.SpanShapeRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AppointmentUtility {

    private int convertAlertTimeToInt (AlertTime alertTime) {
        if(alertTime.equals(AlertTime.ZERO)) {
            return 0;
        } else if (alertTime.equals(AlertTime.FIFTEEN)){
            return 15;
        } else if (alertTime.equals(AlertTime.THIRTY)) {
            return 30;
        } else if (alertTime.equals(AlertTime.HOUR)) {
            return 60;
        } else if (alertTime.equals(AlertTime.DAY)) {
            return 60*24;
        } else if (alertTime.equals(AlertTime.WEEK)) {
            return 60*24*7;
        } else {
            throw new IllegalArgumentException("Invalid alert time");
        }
    }

    public List<Appointment> convertAlertToAppointments (Alert alert) {
        List<Appointment> appointmentList = new ArrayList<>();

        List<Contact> contacts = alert.getRemindr().getContacts();
        for(Contact contact : contacts) {
            //if they did not text stop and they have opted in
            if(!contact.isStop() && contact.getRemindrContacts().stream().filter(r -> r.getRemindr().equals(alert.getRemindr())).findFirst().get().getInviteStatus() == InviteStatus.ACCEPTED) {

                appointmentList.add(convertAlertAndContactToAppointment(alert, contact));
            }

        }

        return appointmentList;

    }

    public Appointment convertAlertAndContactToAppointment (Alert alert, Contact contact) {
        Appointment appointment = new Appointment();
        appointment.setCompositeId(alert.getId() + "_" + contact.getId());

        appointment.setName(contact.getFirstName());
        appointment.setPhoneNumber(formatPhoneNumberToTwilio(contact.getPhoneNumber()));
        appointment.setDelta(convertAlertTimeToInt(alert.getAlertTime()));
        appointment.setTitle(alert.getRemindr().getTitle());
        appointment.setDescription(alert.getRemindr().getDescription());
        appointment.setSender(alert.getRemindr().getUser().getContact().getFirstName() + " " + alert.getRemindr().getUser().getContact().getLastName());
        appointment.setLocation(alert.getRemindr().getLocation());
        try {
            appointment.setDate(convertDate(alert.getRemindr().getStartDateTime(), alert.getRemindr().getTimeZone()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date");
        }
        appointment.setTimeZone(alert.getRemindr().getTimeZone());

        return appointment;
    }

    public String convertDate(String date, String timeZone) throws ParseException {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
        DateTime date1 = formatter.parseDateTime(date);
        DateTimeZone dtZone = DateTimeZone.forID(timeZone);
        date1.withZone(dtZone);

        DateTimeFormatter formatter1 = DateTimeFormat.forPattern("MM-dd-yyyy h:mma");
        formatter1.withZoneUTC();

        return formatter1.print(date1);
    }


    String formatPhoneNumberToTwilio(String phoneNumber) {
        String dbFormat = phoneNumber.replaceAll("[^0-9]", "");
        String correctFormat = "+1" + dbFormat;
        return correctFormat;
    }

    public Date prepareTriggerDate(Appointment appointment) throws ParseException {
        DateTimeZone zone = DateTimeZone.forID(appointment.getTimeZone());
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-yyyy h:mma");

        DateTime dt = formatter.withZone(zone).parseDateTime(appointment.getDate());
        Date finalDate = dt.minusMinutes(appointment.getDelta()).toDate();
        return finalDate;
    }

}
