package com.sweetjandy.remindr.services;

import org.springframework.stereotype.Service;

@Service
public class PhoneService {

    public boolean isNaN(String substring) {
        try {
            Integer.parseInt(substring);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public boolean validatePhoneNumber(String phoneNumber) {

        return phoneNumber != null && !phoneNumber.equals("") && phoneNumber.charAt(0) == '(' &&
                phoneNumber.charAt(4) == ')' &&
                phoneNumber.charAt(5) == ' ' &&
                phoneNumber.charAt(9) == '-' &&
                phoneNumber.length() == 14 &&
                !isNaN(phoneNumber.substring(1, 4)) &&
                !isNaN(phoneNumber.substring(6, 9)) &&
                !isNaN(phoneNumber.substring(10, phoneNumber.length()));

    }

    public String formatPhoneNumber(String phoneNumber) {
        String correctFormat = phoneNumber.replaceAll("[^0-9]", "");

        if (correctFormat.length() == 10 || (correctFormat.length() == 11 && correctFormat.charAt(0) == '1')) {
            if (correctFormat.length() == 11) {
                correctFormat = correctFormat.substring(1, correctFormat.length());
            }

            correctFormat = "(" + correctFormat.substring(0, 3) + ") " + correctFormat.substring(3, 6) + "-" + correctFormat.substring(6, correctFormat.length());

            return correctFormat;
        } else {
            return null;
        }

    }
}