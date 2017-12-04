package com.sweetjandy.remindr.models;

import org.hibernate.annotations.Fetch;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
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

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @Column(nullable = true, unique = true)
    private String googleContact;

    @Column(nullable = true, unique = true)
    private String outlookContact;

//    @Column(nullable = false, length = 4, unique = true)
//    @NotBlank(message = "Posts must have a description!")
//    @Size(min = 4, message = "The secret code is 6 character long")
//    private long secretCode;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contact")
    private List<RemindrContact> remindrContacts;

    @ManyToOne
    private User user;

//    @Column(nullable = true)
    @ManyToOne
    private Remindr pending; // the contact's last pending remindr

    @Column(nullable = false)
    private boolean stop;

    public Contact() {
    }

    public Contact(Long id, String firstName, String lastName, String phoneNumber, String googleContact, String outlookContact) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.googleContact = googleContact;
        this.outlookContact = outlookContact;
//        this.secretCode = secretCode;
    }

    @Autowired
    public Contact(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
//        this.googleContact = googleContact;
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

    public String getGoogleContact() {
        return googleContact;
    }

    public void setGoogleContact(String googleContact) {
        this.googleContact = googleContact;
    }

    public String getOutlookContact() {
        return outlookContact;
    }

    public void setOutlookContact(String outlookContact) {
        this.outlookContact = outlookContact;
    }

//    public long getSecretCode() {
//        return secretCode;
//    }
//
//    public void setSecretCode(long secretCode) {
//        this.secretCode = secretCode;
//    }

    public List<Remindr> getRemindrs() {

        List<Remindr> remindrs = new ArrayList<>();
        for(RemindrContact remindrContact: getRemindrContacts()) {
            remindrs.add(remindrContact.getRemindr());
        }

        return remindrs;
    }

    public void setRemindrs(List<Remindr> remindrs) {

        List<RemindrContact> remindrContacts = new ArrayList<>();
        for(Remindr remindr: remindrs) {
            RemindrContact remindrContact = new RemindrContact();
            remindrContact.setContact(this);
            remindrContact.setRemindr(remindr);
            remindrContacts.add(remindrContact);
        }
        setRemindrContacts(remindrContacts);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean stillHasSameNumber(Contact myself) {
        return phoneNumber.equals(myself.phoneNumber);
    }


    public Remindr getPending() {
        return pending;
    }

    public void setPending(Remindr pending) {
        this.pending = pending;
    }
    @Override
    public boolean equals(Object object) {
        Contact contact = (Contact) object;
        if(this.getId() == contact.getId() || this.getPhoneNumber().equals(contact.getPhoneNumber())){
            return true;
        } else {
            return false;
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public List<RemindrContact> getRemindrContacts() {
        return remindrContacts;
    }

    public void setRemindrContacts(List<RemindrContact> remindrContacts) {
        this.remindrContacts = remindrContacts;
    }
}