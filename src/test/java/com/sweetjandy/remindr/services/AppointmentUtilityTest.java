package com.sweetjandy.remindr.services;

import com.sweetjandy.remindr.models.Appointment;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class AppointmentUtilityTest {

    private AppointmentUtility appointmentUtility;

    @Before
    public void setup() {
        this.appointmentUtility = new AppointmentUtility();
    }

    @Test
    public void prepareTriggerDateTest() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDate("11-30-2017 7:40PM");
        appointment.setTimeZone("US/Central");

        Date result = appointmentUtility.prepareTriggerDate(appointment);
        System.out.println(result);

    }


}