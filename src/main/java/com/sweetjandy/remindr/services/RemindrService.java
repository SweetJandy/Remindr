package com.sweetjandy.remindr.services;

import org.springframework.stereotype.Service;

@Service
public class RemindrService {
    public String getYear (String dateTime) { return dateTime.substring(0, 4);
    }

    public String getMonth (String dateTime) {
        return dateTime.substring(5, 7);
    }

    public String getDate (String dateTime) {
        return dateTime.substring(8, 10);
    }

    public String getTime (String dateTime) {
        String twelveHour = convertTo12HrTime(dateTime.substring(11));
        return twelveHour;
    }

    public String convertTo12HrTime (String time) {
        int indexOfhalf = time.indexOf(":");

        String hour = time.substring(0, indexOfhalf);
        String minutes = time.substring(indexOfhalf+1);

        String append = "";

        // check if it's before noon
        if ((Integer.parseInt(hour) <= 11) && (Integer.parseInt(minutes) <= 59)) {

            append = "AM";

            // get rid of the zero
            if ((Integer.parseInt(hour) < 10)) {
                char charHour = hour.charAt(1);

                return charHour + ":" + minutes + " " + append;
            }

            return hour + ":" + minutes + " " + append;

        // check if it's after noon
        } else if ((Integer.parseInt(hour) >= 12) && (Integer.parseInt(minutes) <= 59)) {

            if (Integer.parseInt(minutes) <= 59) {
                append = "PM";
            }

            return (Integer.parseInt(hour) - 12) + ":" + minutes + " " + append;
        }

        return "";
    }

    public String getFinalDate (String dateTime) {

        String year = getYear(dateTime);
        String month = getMonth(dateTime);
        String date = getDate(dateTime);

        return month + '/' + date + '/' + year;
    }

}