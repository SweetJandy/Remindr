package com.sweetjandy.remindr.models;


import com.fasterxml.jackson.annotation.JsonBackReference;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "remindrs")
public class Remindr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Remindrs must have a title")
    @Size(min = 3, message = "The title must be at least 3 character long")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Remindrs must have a description")
    @Size(min = 10, message = "The description must be at least 10 character long")
    private String description;

    @Column(nullable = false)
    @NotBlank(message = "Remindrs must have a start date/time")
    private String startDateTime;

    @Column(nullable = false)
    @NotBlank(message = "Remindrs must have a end date/time")
    private String endDateTime;

    @Column(nullable = false)
    @NotBlank(message = "Remindrs must have a location")
    private String location;

    @Column(nullable = false)
    @NotBlank(message = "Must choose if you want to make the view public")
    private Boolean publicView;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "remindr")
    @JsonBackReference
    private List<Alert> alerts;

    @ManyToMany(mappedBy = "remindrs")
    private List<Contact> contacts;


    public Remindr() {
    }

    public Remindr(Remindr copy) {
        this.title = copy.title;
        this.description = copy.description;
        this.startDateTime = copy.startDateTime;
        this.endDateTime = copy.endDateTime;
        this.location = copy.location;
        this.publicView = copy.publicView;
//        this.contact = contact;
    }

    public Remindr(String title, String description, String endDateTime, String startDateTime, String location, Boolean publicView) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
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


    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(DateTime startDate) {
        this.startDateTime = startDateTime;
    }


    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(DateTime endDate) {
        this.endDateTime = endDateTime;
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
