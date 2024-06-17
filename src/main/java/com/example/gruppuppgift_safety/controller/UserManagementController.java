package com.example.gruppuppgift_safety.controller;

import com.example.gruppuppgift_safety.utility.HtmlUtil;
import com.example.gruppuppgift_safety.utility.MaskingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manageuser")
public class UserManagementController {

    private final UserDetailsService userDetailsService;
    private final RegistrationController registrationController;
    private final MaskingUtils maskingUtils;
    private final HtmlUtil htmlUtil;

    @Autowired
    public UserManagementController(HtmlUtil htmlUtil,MaskingUtils maskingUtils, UserDetailsService userDetailsService, RegistrationController registrationController) {
        this.htmlUtil = htmlUtil;
        this.maskingUtils = maskingUtils;
        this.userDetailsService = userDetailsService;
        this.registrationController = registrationController;
    }

    @GetMapping
    public String userManagementPage(Model model, @RequestParam(value = "errorUsername", required = false) String errorUsername) {
        InMemoryUserDetailsManager userDetailsManager = (InMemoryUserDetailsManager) userDetailsService;
        List<UserDetails> users = new ArrayList<>();
        Map<String, String> userEmails = registrationController.getUserEmails();
        try{
            Field field = InMemoryUserDetailsManager.class.getDeclaredField("users");
            field.setAccessible(true);
            Map<String, UserDetails> usersMap = (Map<String, UserDetails>) field.get(userDetailsManager);
            users.addAll(usersMap.values());
        }catch(NoSuchFieldException | SecurityException | IllegalAccessException e){
            e.printStackTrace();
        }
        List<String> maskedEmails = new ArrayList<>();
        for (UserDetails user : users) {
            String username = user.getUsername();
            String email = userEmails.get(username);
            String maskedEmail = maskingUtils.maskEmail(email);
            maskedEmails.add(htmlUtil.escapeHtml(maskedEmail));
        }
        model.addAttribute("users", users);
        model.addAttribute("userEmails", maskedEmails);
        if (errorUsername != null) {
            model.addAttribute("errorUsername", errorUsername);
        }

        return "manageuser";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("username") String username, @RequestParam("email") String email, Model model) {
        InMemoryUserDetailsManager userDetailsManager = (InMemoryUserDetailsManager) userDetailsService;
        Map<String, String> userEmails = registrationController.getUserEmails();
        String registeredEmail = userEmails.get(username);

        if (!registeredEmail.equals(email)) {
            model.addAttribute("errorUsername", username);
            return "redirect:/manageuser?errorUsername=" + username;
        }

        userDetailsManager.deleteUser(username);
        return "redirect:/manageuser";
    }
}
