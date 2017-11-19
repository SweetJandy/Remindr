package com.sweetjandy.remindr.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.CascadeType.ALL;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "Email cannot be blank")
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @OneToOne
    private Contact contact;

    @ManyToMany(cascade = ALL)
    @JoinTable(
            name = "user_contact",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "contacts_id")}
    )
    private List<Contact> contacts;


    public User() {
    }

    public User(long id, String username, String password, Contact contact) {
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

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
