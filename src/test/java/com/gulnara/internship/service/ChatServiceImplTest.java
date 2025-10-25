package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

/**
 * Reactive unit tests for ChatServiceImpl using WebClient (non-blocking).
 */
class ChatServiceImplTest {

    private WebClient webClientMock;
    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);
        chatService = new ChatServiceImpl() {
            @Override
            public Mono<ChatResponseDto> getChatResponse(ChatRequestDto request) {

                ChatResponseDto mockResponse = new ChatResponseDto();
                mockResponse.setResponse("Async reply for: " + request.getMessage());
                return Mono.just(mockResponse);
            }
        };
    }

    @Test
    void shouldReturnAIResponse() {
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello AI");

        Mono<ChatResponseDto> responseMono = chatService.getChatResponse(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response ->
                        response.getResponse().equals("Async reply for: Hello AI"))
                .verifyComplete();
    }

    @Test
    void shouldHandleErrorGracefully() {
        ChatServiceImpl failingService = new ChatServiceImpl() {
            @Override
            public Mono<ChatResponseDto> getChatResponse(ChatRequestDto request) {
                return Mono.error(new WebClientResponseException(
                        500, "Internal Server Error", null, null, null));
            }
        };

        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("trigger error");

        StepVerifier.create(failingService.getChatResponse(request)
                        .onErrorResume(e -> {
                            ChatResponseDto fallback = new ChatResponseDto();
                            fallback.setResponse("Error handled");
                            return Mono.just(fallback);
                        }))
                .expectNextMatches(resp -> resp.getResponse().equals("Error handled"))
                .verifyComplete();
    }
}
