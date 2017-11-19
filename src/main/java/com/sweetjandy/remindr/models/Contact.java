package com.sweetjandy.remindr.models;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Column(nullable = false, unique = true, length = 20)
    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, message = "The phone number must be at least 10 digits long")
    private String phoneNumber;

    @Column(nullable = true, unique = true)
    private long googleContact;

    @Column(nullable = true, unique = true)
    private long OutlookContact;

    @Column(nullable = false, length = 4, unique = true)
    @NotBlank(message = "Posts must have a description!")
    @Size(min = 4, message = "The secret code is 6 character long")
    private long secretCode;


    @ManyToMany(mappedBy = "contacts")
    private List<User> users;

    @ManyToMany(cascade = ALL)
    @JoinTable(
            name = "contact_reminder",
            joinColumns = {@JoinColumn(name = "contact_id")},
            inverseJoinColumns = {@JoinColumn(name = "reminder_id")}
    )
    private List<Reminder> reminders;

    public Contact() {
    }

    public Contact(long id, String firstName, String lastName, String phoneNumber, long googleContact, long outlookContact, long secretCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.googleContact = googleContact;
        OutlookContact = outlookContact;
        this.secretCode = secretCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getGoogleContact() {
        return googleContact;
    }

    public void setGoogleContact(long googleContact) {
        this.googleContact = googleContact;
    }

    public long getOutlookContact() {
        return OutlookContact;
    }

    public void setOutlookContact(long outlookContact) {
        OutlookContact = outlookContact;
    }

    public long getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(long secretCode) {
        this.secretCode = secretCode;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
}