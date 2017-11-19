package com.sweetjandy.remindr.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;


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

    public void setUser(Contact contact) {
        this.contact = contact;
    }
}
