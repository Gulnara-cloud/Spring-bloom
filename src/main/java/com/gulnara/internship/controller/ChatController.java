package com.gulnara.internship.controller;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDto> sendMessage(@RequestBody ChatRequestDto request) {
        String aiReply = chatService.getAiResponse(request.getMessage());
        ChatResponseDto response = new ChatResponseDto(aiReply);
        return ResponseEntity.ok(response);
    }
}
