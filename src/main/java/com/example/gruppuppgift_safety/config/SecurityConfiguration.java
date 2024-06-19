package com.example.gruppuppgift_safety.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
/**SecurityConfig klass som styr hur säkerheten för hemsidan ska se ut.
*
* I securityChain() bestämmer man dom olika momenten och hur dom ska hanteras.
* authorizeHttpRequests bestämmer vilka behörigheter dom olika rollerna har.
* formLogin bestämmer hur inloggning sker och hur både lyckad och misslyckad inloggning sker
* logout bestämmer hur utloggning sker
* csrf lägger till csrf-skydd
*
* userDetailsService() instansierar inMemoryUserDetailsManager där vi sparar användarna som skapas på hemsidan.
* Skapar standardanvändarna admin & user där vi hårdkodat användarnamn&lösenord och
*  med hjälp av passwordEncoder() krypterar vi lösenorden.
*
* Vi har även instasierat Logger och använder den för att båda beskriva metod och när någon data hanteras.
* */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration{

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {
        logger.debug("Security Chain with HttpSecurity started");
        http
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/register", "/admin", "/manageuser").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults()) //HTTP basic ingång
                .formLogin(formLogin ->{
                        formLogin
                                .defaultSuccessUrl("/", true)
                                .successHandler((request, response, authentication) -> {
                                    String username = request.getParameter("username");
                                    logger.warn("Login succeeded for user : {}", username);
                                    response.sendRedirect("/");
                                })
                                .failureUrl("/login?error=true")
                                .failureHandler((request,response,exception)->{
                                    String username = request.getParameter("username");
                                    logger.warn("Login failed with attempted User name: {}", username);
                                    response.sendRedirect("/login?error=true");
                                })
                                .permitAll();
                })
                .logout(logout ->{
                        logout
                        .logoutUrl("/performLogout")
                        .logoutSuccessUrl("/login")
                        .permitAll();
                })
//                .csrf(csrf -> csrf.disable()); // disabled for testing
               .csrf(csrf -> {
                        logger.debug("CSRF protection configuration deployed");
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                });
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        logger.debug("Default users created");
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        var user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        var admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();
        userDetailsManager.createUser(user);
        userDetailsManager.createUser(admin);
        return userDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}