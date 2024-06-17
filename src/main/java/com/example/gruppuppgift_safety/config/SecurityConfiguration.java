package com.example.gruppuppgift_safety.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration{

    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/register", "/admin", "/manageuser").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .formLogin(formLogin ->
                        formLogin
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/login?error=true")
                                .permitAll()
                )
                .logout(logout ->
                logout
                        .logoutUrl("/performLogout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .csrf(csrf ->
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
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