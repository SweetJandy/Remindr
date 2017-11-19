package com.sweetjandy.remindr.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name="alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "Alerts must have a time")
    private Time time;

    @Column(nullable = false)
    @NotBlank(message = "Alerts must have a date")
    private Date date;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "reminder_id")
    private Reminder reminder;

    public Alert(){
    }

    public Alert(Time time, Time date, Reminder reminder) {
        this.time = time;
        this.date = date;
        this.reminder = reminder;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Time date) {
        this.date = date;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }
}
