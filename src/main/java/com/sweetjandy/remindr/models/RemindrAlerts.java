package com.sweetjandy.remindr.models;

public class RemindrAlerts {
    private String alertTimes;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertTimes() {
        return alertTimes;
    }

    public void setAlertTimes(String alertTimes) {
        this.alertTimes = alertTimes;
    }
}