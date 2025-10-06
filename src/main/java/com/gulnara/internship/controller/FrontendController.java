package com.gulnara.internship.controller;

import com.gulnara.internship.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    //   Handles the main landing page (index.html)
    // When user visits "/", it returns the template named "index"

    @GetMapping("/")
    public String index() {
        return "index"; // looks for index.html in src/main/resources/templates
    }

    //   Handles the registration page (register.html)
    // When user visits "/register", it prepares a new empty UserRegistrationDto
    // and passes it to the page (so Thymeleaf can bind form fields)

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register"; // looks for register.html in src/main/resources/templates
    }
}
