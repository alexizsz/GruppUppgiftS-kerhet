package com.example.gruppuppgift_safety.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manageuser")
public class UserManagement {

    @GetMapping
    public String userManagementPage(){
        return "manageuser";
    }

}
