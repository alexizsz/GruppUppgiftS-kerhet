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
/**RegistrationController klass bestämmer hur registrering sker på hemsidan
* Vi har instansierat dom klasser som behövs för att föra vidare, spara och hantera data på korrekt vis.
* @Autowired
* RegistrationController() är vart vi använder oss av Dependency injection.
*
* registrationForm(Model model) använder sig av thymeleaf och Model för att föra vidare den data vi vill använda oss av
* här till HTML-sidor. I detta fall vill vi applicera en ny instansiering av användare till "formen" i html sidan.
*
* registerUser() skapar den nya användaren med hjälp av build() metoden. Vi sparar användarnamn, password(som blir
* krypterat med hjälp av passwordEncoder) och vi sparar e-posten med hjälp av HtmlUtil.escapeHtml för att radera skadlig
* kod. Sedan sparar vi vår användare till userDetailsService().
* I början av metoden använder vi en if() metod för att fånga upp felmatad data beroende på dom "reglerna" vi satt
* i vår AppUser (userDTO) klass.
* Vi använder oss av Model för att spara och föra vidare information till html/thymeleaf hantering.
* Logger används för att logga när data sparas/används och vid metodhändelser som är relevanta. Här används även
* MaskingUtils.maskEmails för att maskera den email som används i loggen.
* Vid lyckad registrering skickas man vidare till hemsidan där det står att registrering lyckats och vilken
* användare som skapats.
*
*
* */
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
        logger.debug("Access of registration form");
        model.addAttribute("user", new AppUser());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") AppUser appUser,BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("error", "There are errors in the form, please correct them");
            logger.debug("Form has validation errors for attempted registration of user: {}", appUser.getName());
            System.out.println(appUser);
            return "register";
        }
        logger.debug("Registration for user with email {}", new MaskingUtils().maskEmail(appUser.getEmail()) + " done");
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
