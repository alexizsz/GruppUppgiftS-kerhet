package com.example.gruppuppgift_safety.utility;


import org.springframework.stereotype.Component;
/**
 * MaskingUtils används för att maskera den e-post som sparas på hemsidan.
 * Vi säger åt den att int atIndex börjar där @ börjar i en e-post genom att sätta atIndex till
 * positionen för "@" i vår String email som vi tar in som argument.
 * String localPart får två regler för start och slut. Start är index 0 i email, och sedan slutar
 * den på atIndex.
 * String domainPart får en regel, och det är att den ska börja från atIndex.
 * Sedan i våran return returnerar vi hur vi vill att mailen ska skrivas ut:
 * return
 *  localPart.charAt(0) - (oförändrad bokstav på index(0) = första bokstaven i mailen)
 *  + "*" + localPart.charAt(localPart.length() - 1) - Här(skriver vi ut en stjärna(*) + näst sista bokstaven i localPart
 *  strängen genom att ta längden på strängen(.length()) -1(näst sista bokstaven))
 *  + domainPart; - (domainPart är oförändrad och innehåller allt från @ och efter i en mail)
 */
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
