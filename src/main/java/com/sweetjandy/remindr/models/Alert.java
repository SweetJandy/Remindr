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
    private String dateTime;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "remindr_id")
    private Remindr remindr;

    public Alert(){
    }

    public Alert(String dateTime,  Remindr remindr) {
        this.dateTime = dateTime;
        this.remindr = remindr;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getdateTime() {
        return dateTime;
    }

    public void setdateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Remindr getRemindr() {
        return remindr;
    }

    public void setRemindr(Remindr remindr) {
        this.remindr = remindr;
    }
}
