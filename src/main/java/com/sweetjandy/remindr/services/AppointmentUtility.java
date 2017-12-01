package com.sweetjandy.remindr.services;

import com.sweetjandy.remindr.models.Alert;
import com.sweetjandy.remindr.models.AlertTime;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Appointment;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            appointmentList.add(convertAlertAndContactToAppointment(alert, contact));
        }

        return appointmentList;

    }

    public Appointment convertAlertAndContactToAppointment (Alert alert, Contact contact) {
        Appointment appointment = new Appointment();
        appointment.setCompositeId(alert.getId() + "_" + contact.getId());

        appointment.setName(contact.getFirstName() + " " + contact.getLastName());
        appointment.setPhoneNumber(formatPhoneNumberToTwilio(contact.getPhoneNumber()));
        appointment.setDelta(convertAlertTimeToInt(alert.getAlertTime()));
        try {
            appointment.setDate(convertDate(alert.getRemindr().getStartDateTime(), alert.getRemindr().getTimeZone()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date");
        }
        appointment.setTimeZone(alert.getRemindr().getTimeZone());

        return appointment;
    }

    private String convertDate(String date, String timeZone) throws ParseException {

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//        Date date1 = simpleDateFormat.parse(date);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
        DateTime date1 = formatter.parseDateTime(date);
        DateTimeZone dtZone = DateTimeZone.forID(timeZone);
        date1.withZone(dtZone);

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd-yyyy hh:mma");
        DateTimeFormatter formatter1 = DateTimeFormat.forPattern("MM-dd-yyyy hh:mma");
        formatter1.withZoneUTC();

        return formatter1.print(date1.toDateTime(DateTimeZone.UTC));
    }


    String formatPhoneNumberToTwilio(String phoneNumber) {
        String dbFormat = phoneNumber.replaceAll("[^0-9]", "");
        String correctFormat = "+1" + dbFormat;
        return correctFormat;
    }

}
