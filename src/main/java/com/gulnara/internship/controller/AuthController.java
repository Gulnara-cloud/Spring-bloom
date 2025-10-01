package com.gulnara.internship.controller;

import com.gulnara.internship.dto.UserLoginDto;
import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;
import com.gulnara.internship.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
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
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userData) {
        try {
            userService.registerUser(userData);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
