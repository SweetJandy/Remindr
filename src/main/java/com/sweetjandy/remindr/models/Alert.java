package com.sweetjandy.remindr.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @Column(nullable = false)
//    @NotBlank(message = "Alerts must have a time")
//    private String dateTime;

    @Column(name = "alert_time", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertTime alertTime;

    @ManyToOne
    @JsonManagedReference
    private Remindr remindr;

    public Alert(){
    }

    public Alert(AlertTime alertTime, Remindr remindr) {
        this.alertTime = alertTime;
        this.remindr = remindr;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AlertTime getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(AlertTime alertTime) {
        this.alertTime = alertTime;
    }

    public Remindr getRemindr() {
        return remindr;
    }

    public void setRemindr(Remindr remindr) {
        this.remindr = remindr;
    }
}
