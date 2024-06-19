package com.example.gruppuppgift_safety.utility;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
/**
 * HtmlUtils använder sig utav en inbyggd metod från springframework som tar in en sträng(input)
 * och sedan omvandlar till html symboler. Detta för att förebygga att någon skriver in något som kan påverka kod på.
 * */
@Component
public class HtmlUtil {

    public String escapeHtml(String input){
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }

}