package com.example.gruppuppgift_safety.model;

import jakarta.validation.constraints.*;

public class AppUser {

    @NotEmpty(message = "Name is required")
    @Size(min = 2, message = "Minimum of 2 characters for account name")
    private String name;
    @NotEmpty(message = "Email is required")
    @Email (message = "Please enter a valid e-mail address")
    private String email;
    @NotEmpty( message = "Password is required")
    @Size(min = 2, message = "Minimum of 2 characters for password")
    private String password;

    public String getName(){return name;}
    public void setName(String name) {this.name = name;    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    //Metod för att returnera strängen av vårt objekt(user)
    @Override
    public String toString() {
        return "User{" +
                "name = '" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
