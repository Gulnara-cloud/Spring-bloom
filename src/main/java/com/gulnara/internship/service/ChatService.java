package com.gulnara.internship.service;

import org.springframework.stereotype.Service;

@Service
public class ChatService {

    public String getAiResponse(String userMessage) {
        return "You said: " + userMessage;
    }
}
