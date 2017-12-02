package com.sweetjandy.remindr.services;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

@Service
public class RemindrService {
    public String getYear (String dateTime) {
        if (!Strings.isNullOrEmpty(dateTime)) {
            return dateTime.substring(0, 4);
        }

        return null;
    }

    public String getMonth (String dateTime) {
        if (!Strings.isNullOrEmpty(dateTime)) {
            return dateTime.substring(5, 7);
        }

        return null;
    }

    public String getDate (String dateTime) {
        if (!Strings.isNullOrEmpty(dateTime)) {
            return dateTime.substring(8, 10);
        }

        return null;
    }

    public String getTime (String dateTime) {

        if (!Strings.isNullOrEmpty(dateTime)) {
            return dateTime.substring(11);

        }

        return null;
    }

    public String convertTo12HrTime (String time) {
        if (!Strings.isNullOrEmpty(time)) {

            int indexOfhalf = time.indexOf(":");

            String hour = time.substring(0, indexOfhalf);
            String minutes = time.substring(indexOfhalf+1);

            String append = "";

        // check if it's before noon
        if (!Strings.isNullOrEmpty(time) && Integer.parseInt(hour) <= 11 && (Integer.parseInt(minutes) <= 59)) {

            append = "AM";

            // get rid of the zero
            if ((Integer.parseInt(hour) < 10)) {
                char charHour = hour.charAt(1);

                return charHour + ":" + minutes + " " + append;
            }

            return hour + ":" + minutes + " " + append;

        // check if it's after noon
        } else if (!Strings.isNullOrEmpty(time) && (Integer.parseInt(hour) > 12) && (Integer.parseInt(minutes) <= 59)) {

            if (Integer.parseInt(minutes) <= 59) {
                append = "PM";
            }

            return (Integer.parseInt(hour) - 12) + ":" + minutes + " " + append;
        } else if (!Strings.isNullOrEmpty(time) && (Integer.parseInt(hour) == 12)) {

            return "12:00 PM";
        }

        }
        return null;
    }

    public String getFinalDate (String dateTime) {

        if (!Strings.isNullOrEmpty(dateTime)) {

            String year = getYear(dateTime);
            String month = getMonth(dateTime);
            String date = getDate(dateTime);

            return month + '/' + date + '/' + year;
        }

        return null;
    }

}