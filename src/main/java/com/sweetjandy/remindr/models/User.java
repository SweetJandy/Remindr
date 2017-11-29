package com.sweetjandy.remindr.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must have an @ symbol")
    private String username;

    @Column(nullable = false)
    @JsonIgnore
//    @NotBlank(message = "Password cannot be blank")
    private String password;

    private transient String confirmPassword;

    private transient String newPassword;

    private transient String confirmNewPassword;

    @OneToOne
    @Valid
    private Contact contact;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonBackReference
    private List<Remindr> remindrs;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Contact> contacts;


    public User() {
        this.contact = new Contact();
        this.contacts = new ArrayList<>();
    }

    public User(User copy) {
        id = copy.id;
        username = copy.username;
        password = copy.password;
        contact = copy.contact;
    }

    public User(long id, String username, String password, Contact contact, List<Contact> contacts) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.contact = contact;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<Remindr> getRemindrs() {
        return remindrs;
    }

    public void setRemindrs(List<Remindr> remindrs) {
        this.remindrs = remindrs;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
