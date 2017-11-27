package com.sweetjandy.remindr.services;

import org.springframework.stereotype.Service;

@Service
public class PhoneService {

    public static boolean isNaN(String substring) {
        try {
            Integer.parseInt(substring);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean validatePhoneNumber(String phoneNumber) {

        return phoneNumber != null && !phoneNumber.equals("") && phoneNumber.charAt(0) == '(' &&
                phoneNumber.charAt(4) == ')' &&
                phoneNumber.charAt(8) == '-' &&
                phoneNumber.length() == 13 &&
                !isNaN(phoneNumber.substring(1, 4)) &&
                !isNaN(phoneNumber.substring(5, 8)) &&
                !isNaN(phoneNumber.substring(9, phoneNumber.length()));

    }
}
