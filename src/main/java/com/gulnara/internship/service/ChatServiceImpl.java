package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Asynchronous implementation using WebClient.
 * Sends non-blocking requests to an external AI API and returns a reactive Mono<ChatResponseDto>.
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final WebClient webClient;

    // Create WebClient once; can later be moved to configuration
    public ChatServiceImpl() {
        this.webClient = WebClient.builder()
                .baseUrl("https://mock-ai-api.com")   // placeholder until you connect to Gemini
                .build();
    }

    @Override
    public Mono<ChatResponseDto> getChatResponse(ChatRequestDto request) {
        // Non-blocking asynchronous POST request
        return webClient.post()
                .uri("/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponseDto.class)
                .onErrorResume(error -> {
                    // handle errors gracefully
                    ChatResponseDto fallback = new ChatResponseDto();
                    fallback.setResponse("Error contacting AI: " + error.getMessage());
                    return Mono.just(fallback);
                });
    }
}
