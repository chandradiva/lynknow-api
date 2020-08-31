package com.lynknow.api.util;

import java.util.Random;

public class StringUtil {

    public static String generateOtp() {
        int len = 6;
        String numbers = "0123456789";
        Random random = new Random();
        char[] otp = new char[len];

        for (int i = 0; i < len; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }

        return new String(otp);
    }

    public static String normalizePhoneNumber(String param) {
        if (param != null) {
            return param.replaceAll("-", "").replaceAll("\\s+", "").trim();
        } else {
            return null;
        }
    }

}
