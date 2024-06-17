package com.example.gruppuppgift_safety.utility;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class HtmlUtil {

    public String escapeHtml(String input){
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }

}
