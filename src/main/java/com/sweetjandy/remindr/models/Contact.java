package com.sweetjandy.remindr.models;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Column(nullable = true)
//    @NotBlank(message = "Last name cannot be blank")
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


    @ManyToMany(cascade = CascadeType.DETACH, mappedBy = "contacts")
    private List<Remindr> remindrs;

    @ManyToOne
    private User user;

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
        return remindrs;
    }

    public void setRemindrs(List<Remindr> remindrs) {
        this.remindrs = remindrs;
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
}