package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import org.springframework.stereotype.Service;

/**
 * ChatServiceImpl handles chat logic between the controller
 * and the external AI client (GeminiClientService).
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final GeminiClientService geminiClientService;

    // Constructor Injection (cleaner than @Autowired field)
    public ChatServiceImpl(GeminiClientService geminiClientService) {
        this.geminiClientService = geminiClientService;
    }

    @Override
    public ChatResponseDto getChatResponse(ChatRequestDto request) {
        try {
            // Validate input
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return new ChatResponseDto("Message cannot be empty.");
            }

            // Get AI response via Gemini client
            String aiReply = geminiClientService.getGeminiResponse(request.getMessage());

            // If Gemini returned null or empty, handle gracefully
            if (aiReply == null || aiReply.trim().isEmpty()) {
                aiReply = "No response received from AI.";
            }

            // Wrap response into DTO
            return new ChatResponseDto(aiReply);

        } catch (Exception e) {
            // Handle unexpected exceptions
            return new ChatResponseDto("Error: " + e.getMessage());
        }
    }
}
