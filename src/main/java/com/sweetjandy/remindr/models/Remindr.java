package com.sweetjandy.remindr.models;


import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.swing.*;
import javax.validation.constraints.Size;

import java.awt.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "remindrs")
public class Remindr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    @NotBlank(message = "Remindr events must have a start date/time")
    private String startDateTime;

    @Column(nullable = false)
    @NotBlank(message = "Remindr events must have a end date/time")
    private String endDateTime;

    @Column(nullable = false)
    @NotBlank(message = "Remindr events must have a location")
    private String location;

    @Column(nullable = true)
    @Value("${file-upload-path")
    private String uploadPath;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "remindr")
    @JsonBackReference
    private List<Alert> alerts;

    @ManyToOne
    @JsonManagedReference
    private User user;

    @ManyToMany(mappedBy = "remindrs")
    private List<Contact> contacts;


    public Remindr() {
    }

    public Remindr(String title, String description, String endDateTime, String startDateTime, String location, String uploadPath) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.uploadPath = uploadPath;
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

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }


    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
}