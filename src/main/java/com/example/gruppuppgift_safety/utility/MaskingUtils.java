package com.example.gruppuppgift_safety.utility;


import org.springframework.stereotype.Component;

@Component
public class MaskingUtils {

    public static String maskEmail(String email) {
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        return localPart.charAt(0) + "*" + localPart.charAt(localPart.length() - 1) + domainPart;
    }


}
