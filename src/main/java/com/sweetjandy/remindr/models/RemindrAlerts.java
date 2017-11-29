package com.sweetjandy.remindr.models;

import java.util.List;

public class RemindrAlerts {
    private String alertTimes;
    private Long id;
    private List<Alert> alertList;
    public boolean ZERO;
    public boolean FIFTEEN;
    public boolean THIRTY;
    public boolean HOUR;
    public boolean DAY;
    public boolean WEEK;

    public boolean isZERO() {
        return ZERO;
    }

    public void setZERO(boolean ZERO) {
        this.ZERO = ZERO;
    }

    public boolean isFIFTEEN() {
        return FIFTEEN;
    }

    public void setFIFTEEN(boolean FIFTEEN) {
        this.FIFTEEN = FIFTEEN;
    }

    public boolean isTHIRTY() {
        return THIRTY;
    }

    public void setTHIRTY(boolean THIRTY) {
        this.THIRTY = THIRTY;
    }

    public boolean isHOUR() {
        return HOUR;
    }

    public void setHOUR(boolean HOUR) {
        this.HOUR = HOUR;
    }

    public boolean isDAY() {
        return DAY;
    }

    public void setDAY(boolean DAY) {
        this.DAY = DAY;
    }

    public boolean isWEEK() {
        return WEEK;
    }

    public void setWEEK(boolean WEEK) {
        this.WEEK = WEEK;
    }

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

    public List<Alert> getAlertList() {
        return alertList;
    }

    public void setAlertList(List<Alert> alertList) {
        this.alertList = alertList;
    }
}

