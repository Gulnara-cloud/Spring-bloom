package com.gulnara.internship.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PingController {

    @GetMapping("/api/ping")
    public String ping() {
        return "Backend is connected!";
    }
}