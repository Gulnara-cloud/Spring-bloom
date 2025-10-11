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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // 📌 Registration (for Thymeleaf form)
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDto userData,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Step 1: Check for validation errors
        if (result.hasErrors()) {
            return "register"; // return to the same page if validation fails
        }

        // Step 2: Register the user (global exception handling applied)
        userService.registerUser(userData);

        // Step 3: Add a success flash message for the frontend
        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully!");
        return "redirect:/register";
    }

    // 📌 Registration (for JSON requests — used in tests)
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, String>> registerUserApi(@Valid @RequestBody UserRegistrationDto userData) {
        userService.registerUser(userData);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 CREATED
    }

    // 📌 Login (JSON API for tests & Postman)
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginDto loginData) {
        Map<String, String> response = new HashMap<>();

        // Step 1: Find user by username
        User user = userService.findByUsername(loginData.getUsername());
        if (user == null) {
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Step 2: Validate the password
        boolean passwordMatches = passwordEncoder.matches(loginData.getPassword(), user.getPasswordHash());
        if (!passwordMatches) {
            response.put("error", "Invalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Step 3: Successful login
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }
}
