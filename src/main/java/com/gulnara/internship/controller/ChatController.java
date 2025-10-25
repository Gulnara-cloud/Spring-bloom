package com.gulnara.internship.controller;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * ChatController handles asynchronous chat requests from the frontend.
 * It receives a message from the user and returns a reactive response from the AI API.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Asynchronous endpoint — returns a reactive Mono<ChatResponseDto>
     */
    @PostMapping
    public Mono<ChatResponseDto> getChatResponse(@RequestBody ChatRequestDto request) {
        // Simply forward the request to the ChatService (reactive)
        return chatService.getChatResponse(request);
    }
}
