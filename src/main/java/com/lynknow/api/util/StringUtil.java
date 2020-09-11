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

    public static String generateUniqueCodeCard() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString.toUpperCase();
    }

}
