package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatServiceImpl implements ChatService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key:dummy-key}")
    private String apiKey;

    @Value("${openai.api.url:https://mock-ai-api.com/chat}")
    private String apiUrl;

    public ChatServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ChatResponseDto getChatResponse(ChatRequestDto request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<ChatRequestDto> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ChatResponseDto> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, ChatResponseDto.class);

            return response.getBody();
        } catch (Exception e) {
            ChatResponseDto error = new ChatResponseDto();
            error.setResponse("Error contacting AI: " + e.getMessage());
            return error;
        }
    }
}
