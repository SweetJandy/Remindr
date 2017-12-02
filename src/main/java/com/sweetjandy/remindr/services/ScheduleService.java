package com.sweetjandy.remindr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetjandy.remindr.models.Alert;
import com.sweetjandy.remindr.repositories.AlertsRepository;
import com.sweetjandy.remindr.models.Appointment;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class ScheduleService {

    private Scheduler scheduler;
    private AppointmentUtility appointmentUtility;

    @Value("${twilio-account-sid}")
    private String twilioSid;

    @Value("${twilio-auth-token}")
    private String twilioToken;

    @Value("${twilio-number}")
    private String twilioNumber;

    public ScheduleService (AlertsRepository alertsRepository, AppointmentUtility appointmentUtility) throws ParseException {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

        } catch (SchedulerException se) {
            System.out.println("Unable to start scheduler service");
        }

        this.appointmentUtility = appointmentUtility;

        Iterable<Alert> allAlerts = alertsRepository.findAll();
        for(Alert alert : allAlerts) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date1 = simpleDateFormat.parse(alert.getRemindr().getStartDateTime());
            if(date1.after(new Date())){
                schedule(alert);
            }

        }

    }

    public void schedule(Alert alert) {
        List<Appointment> appointments = appointmentUtility.convertAlertToAppointments(alert);
        for(Appointment appointment : appointments) {
            try {
                scheduleJob(appointment);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (ParseException p) {
                p.printStackTrace();
            }
        }
    }

    public void unschedule(Alert alert){

    }

    private void scheduleJob(Appointment appointment) throws JsonProcessingException, ParseException {

        String appointmentId = appointment.getCompositeId();
        Date finalDate = appointmentUtility.prepareTriggerDate(appointment);

        ObjectMapper objectMapper = new ObjectMapper();
        String appointmentJson = objectMapper.writeValueAsString(appointment);

        JobDetail job =
                newJob(AlertScheduler.class).withIdentity("Appointment_J_" + appointmentId)
                        .usingJobData("appointment", appointmentJson).usingJobData("twilioAccountSid", twilioSid).usingJobData("twilioAuthToken", twilioToken).usingJobData("twilioNumber", twilioNumber)
                        .build();

        Trigger trigger =
                newTrigger().withIdentity("Appointment_T_" + appointmentId).startAt(finalDate).build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println("Unable to schedule the Job");
        }
    }


}
