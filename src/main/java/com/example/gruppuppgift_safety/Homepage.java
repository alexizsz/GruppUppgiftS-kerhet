package com.example.gruppuppgift_safety;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/")
public class Homepage {

    @GetMapping
    public String welcome(){
        return "homepage";
    }

}
