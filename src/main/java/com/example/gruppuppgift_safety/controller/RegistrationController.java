package com.example.gruppuppgift_safety.controller;

import com.example.gruppuppgift_safety.model.AppUser;
import jakarta.validation.Valid;
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

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public RegistrationController(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
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
            System.out.println(appUser);
            return "register";
        }

        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        UserDetails newUser = User.withUsername(appUser.getName())
                        .password(appUser.getPassword())
                                .roles("USER")
                                        .build();
        ((InMemoryUserDetailsManager) userDetailsService).createUser(newUser);

        model.addAttribute("user", appUser);
        model.addAttribute("userName", appUser.getName());

            return "regSuccessful";

    }
}
