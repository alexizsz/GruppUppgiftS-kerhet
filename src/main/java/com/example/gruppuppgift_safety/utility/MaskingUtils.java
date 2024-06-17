package com.example.gruppuppgift_safety.utility;


import org.springframework.stereotype.Component;

@Component
public class MaskingUtils {

    public String maskEmail(String email) {
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String maskedPart = repeatMasking(atIndex);
            String domainPart = email.substring(atIndex);
            return maskedPart + domainPart;
        } else {
            return email;
        }
    }

    private String repeatMasking(int count){
        return new String (new char[count]).replace("\0", "*");
    }

}
