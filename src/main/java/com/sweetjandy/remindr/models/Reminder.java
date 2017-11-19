package com.sweetjandy.remindr.models;


import com.fasterxml.jackson.annotation.JsonBackReference;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Reminders must have a title")
    @Size(min = 3, message = "The title must be at least 3 character long")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Reminders must have a description")
    @Size(min = 10, message = "The description must be at least 10 character long")
    private String description;

    @Column(nullable = false)
    @NotBlank(message = "Reminders must have a start date")
    private DateTime startDate;

    @Column(nullable = false)
    @NotBlank(message = "Reminders must have a end date")
    private DateTime endDate;

    @Column(nullable = false)
    @NotBlank(message = "Reminders must have a location")
    private String location;

    @Column(nullable = false)
    @NotBlank(message = "Must choose if you want to make the view public")
    private Boolean publicView;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reminder")
    @JsonBackReference
    private List<Alert> alerts;

    @ManyToMany(mappedBy = "reminders")
    private List<Contact> contacts;


    public Reminder() {
    }


    public Reminder(String title, String description, DateTime startDate, DateTime endDate, String location, Boolean publicView) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.publicView = publicView;
//        this.contact = contact;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getPublicView() {
        return publicView;
    }

    public void setPublicView(Boolean publicView) {
        this.publicView = publicView;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }
}
