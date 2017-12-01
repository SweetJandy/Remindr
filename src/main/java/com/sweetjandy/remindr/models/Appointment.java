package com.sweetjandy.remindr.models;

import javax.persistence.*;

@Entity
@Table(name = "appointments")
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  public Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "phoneNumber")
  private String phoneNumber;

  @Column(name = "delta")
  private int delta;

  @Column(name = "date")
  private String date;

  @Column(name = "timeZone")
  private String timeZone;


  private String compositeId;

  public Appointment() {}

  public Appointment(String name, String phoneNumber, int delta, String date, String timeZone) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.delta = delta;
    this.date = date;
    this.timeZone = timeZone;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) { this.id = id;}

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public int getDelta() {
    return this.delta;
  }

  public void setDelta(int delta) {
    this.delta = delta;
  }

  public String getDate() {
    return TimeZoneConverter.getDateFromUTC(this.date, this.timeZone);
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getCompositeId() {
    return compositeId;
  }

  public void setCompositeId(String compositeId) {
    this.compositeId = compositeId;
  }
}
