package com.example.gruppuppgift_safety.controller;

import com.example.gruppuppgift_safety.model.AppUser;
import com.example.gruppuppgift_safety.utility.HtmlUtil;
import com.example.gruppuppgift_safety.utility.MaskingUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final static Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final Map<String, String> userEmails = new HashMap<>();
    private final HtmlUtil htmlUtil;

    @Autowired
    public RegistrationController(HtmlUtil htmlUtil, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, MaskingUtils maskingUtils) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.htmlUtil = htmlUtil;
    }

    @GetMapping
    public String registrationForm(Model model){
        model.addAttribute("user", new AppUser());
        return "register";
    }
 

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") AppUser appUser,BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("error", "There are errors in the form, please correct them");
            logger.debug("Registration form has validation errors for user {}", appUser.getName());
            System.out.println(appUser);
            return "register";
        }
        logger.debug("Registration request received for user with email {}", new MaskingUtils().maskEmail(appUser.getEmail()));
        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        UserDetails newUser = User.withUsername(appUser.getName())
                        .password(appUser.getPassword())
                                .roles("USER")
                                        .build();
        ((InMemoryUserDetailsManager) userDetailsService).createUser(newUser);

        userEmails.put(appUser.getName(), htmlUtil.escapeHtml(appUser.getEmail()));

        model.addAttribute("user", appUser);
        model.addAttribute("userName", appUser.getName());

            return "regSuccessful";

    }

    public Map<String, String> getUserEmails() {
        return userEmails;
    }
}
