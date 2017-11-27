package com.sweetjandy.remindr.services;

import org.springframework.stereotype.Service;

@Service
public class RemindrService {
    public String getYear (String dateTime) {
        return dateTime.substring(0, 4);
    }

    public String getMonth (String dateTime) {
        return dateTime.substring(5, 7);
    }

    public String getDate (String dateTime) {
        return dateTime.substring(8, 10);
    }

    public String getTime (String dateTime) {
        return dateTime.substring(11);
    }

    public String getFinalDate (String dateTime) {

        String year = getYear(dateTime);
        String month = getMonth(dateTime);
        String date = getDate(dateTime);

        return month + '/' + date + '/' + year;
    }

}
