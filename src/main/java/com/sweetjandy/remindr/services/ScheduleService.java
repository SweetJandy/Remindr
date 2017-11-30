package com.sweetjandy.remindr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetjandy.remindr.models.Alert;
import com.sweetjandy.remindr.models.AlertTime;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.repositories.AlertsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.twilio.appointmentreminders.models.Appointment;
import com.twilio.appointmentreminders.models.AppointmentService;
import com.twilio.appointmentreminders.util.AppSetup;
import com.twilio.appointmentreminders.util.AppointmentScheduler;
import com.twilio.appointmentreminders.models.AppointmentService;
import com.twilio.appointmentreminders.util.AppSetup;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class ScheduleService {

    private Scheduler scheduler;
    private AppointmentService service;
    private AppointmentUtility appointmentUtility;

    public ScheduleService (AlertsRepository alertsRepository, AppointmentUtility appointmentUtility) throws ParseException {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

        } catch (SchedulerException se) {
            System.out.println("Unable to start scheduler service");
        }
//        AppSetup appSetup = new AppSetup();
//        EntityManagerFactory factory = appSetup.getEntityManagerFactory();
//        this.service = new AppointmentService(factory.createEntityManager());

//        Iterable<Alert> allAlerts = alertsRepository.findAll();
//        for(Alert alert : allAlerts) {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//            Date date1 = simpleDateFormat.parse(alert.getRemindr().getStartDateTime());
//            if(date1.after(new Date())){
//                schedule(alert);
//            }
//
//        }

        this.appointmentUtility = appointmentUtility;

    }

    public void schedule(Alert alert) {
        List<Appointment> appointments = appointmentUtility.convertAlertToAppointments(alert);
        for(Appointment appointment : appointments) {
            try {
                scheduleJob(appointment);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void unschedule(Alert alert){

    }

    private void scheduleJob(Appointment appointment) throws JsonProcessingException {

        String appointmentId = appointment.getCompositeId();

        DateTimeZone zone = DateTimeZone.forID(appointment.getTimeZone());
        DateTime dt;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-yyyy hh:mma");
        formatter = formatter.withZone(zone);
        dt = formatter.parseDateTime(appointment.getDate());
        Date finalDate = dt.minusMinutes(appointment.getDelta()).toDate();

        ObjectMapper objectMapper = new ObjectMapper();
        String appointmentJson = objectMapper.writeValueAsString(appointment);

        JobDetail job =
                newJob(AlertScheduler.class).withIdentity("Appointment_J_" + appointmentId)
                        .usingJobData("appointment", appointmentJson).build();

        Trigger trigger =
                newTrigger().withIdentity("Appointment_T_" + appointmentId).startAt(finalDate).build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println("Unable to schedule the Job");
        }
    }

}
