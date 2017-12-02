package com.sweetjandy.remindr.models;


import javax.persistence.*;

@Entity
@Table(name = "remindrs_contacts")
public class RemindrContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="remindr_id")
    private Remindr remindr;

    @ManyToOne
    @JoinColumn(name="contact_id")
    private Contact contact;

    @Column
    private InviteStatus inviteStatus;

    public InviteStatus getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(InviteStatus inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Remindr getRemindr() {
        return remindr;
    }

    public void setRemindr(Remindr remindr) {
        this.remindr = remindr;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
