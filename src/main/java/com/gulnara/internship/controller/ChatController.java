package com.gulnara.internship.controller;

import com.gulnara.internship.config.SecurityUtil;
import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.dto.ConversationDetailDto;
import com.gulnara.internship.dto.ConversationListDto;
import com.gulnara.internship.service.api.ChatService;
import com.gulnara.internship.service.api.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * ChatController handles chat messages between authenticated users
 * and the external AI service (e.g., Gemini API).
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    // Constructor Injection (recommended for immutability and testing)
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping("/message")
    public ResponseEntity<ChatResponseDto> sendMessage(@RequestBody ChatRequestDto request) {

        // Get authenticated username
        String username = SecurityUtil.getAuthenticatedUsername();

        // Convert username → userId
        UUID userId = userService.getUserIdByUsername(username);

        // Call new service method (processChat)
        ChatResponseDto response = chatService.processChat(userId, request);

        // Return response with conversation context
        return ResponseEntity.ok(response);
    }
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationListDto>> getConversations() {

        String username = SecurityUtil.getAuthenticatedUsername();
        System.out.println("USERNAME FROM SECURITY CONTEXT = " + username);

        UUID userId = userService.getUserIdByUsername(username);
        List<ConversationListDto> conversations = chatService.getConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDetailDto> getConversationDetail(
            @PathVariable UUID conversationId) {

        String username = SecurityUtil.getAuthenticatedUsername();
        UUID userId = userService.getUserIdByUsername(username);

        ConversationDetailDto detail =
                chatService.getConversationDetail(userId, conversationId);

        return ResponseEntity.ok(detail);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID conversationId) {

        String username = SecurityUtil.getAuthenticatedUsername();
        UUID userId = userService.getUserIdByUsername(username);

        chatService.deleteConversation(userId, conversationId);

        return ResponseEntity.noContent().build();
    }
}

