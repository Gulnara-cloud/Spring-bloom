package com.gulnara.internship.controller;

import com.gulnara.internship.dto.UserLoginDto;
import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;
import com.gulnara.internship.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // 📌 Registration
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDto userData,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        //  Step 1: Check if there are validation errors
        if (result.hasErrors()) {
            // Re-display the form with validation messages
            return "register";
        }

        //  Step 2: Proceed with registration (no try-catch, handled globally)
        userService.registerUser(userData);

        //  Step 3: Add success message for redirect
        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully!");
        return "redirect:/register";
    }

    // 📌 Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto loginData) {
        User user = userService.findByUsername(loginData.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        boolean passwordMatches = passwordEncoder.matches(loginData.getPassword(), user.getPasswordHash());
        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        return ResponseEntity.ok("Login successful!");
    }
}
