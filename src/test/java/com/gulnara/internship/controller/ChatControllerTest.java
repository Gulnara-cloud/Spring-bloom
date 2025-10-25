package com.gulnara.internship.controller;

import com.gulnara.internship.config.SecurityConfig;
import com.gulnara.internship.config.WebConfig;
import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.service.ChatService;
import com.gulnara.internship.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(ChatController.class)
@Import({WebConfig.class, SecurityConfig.class})
@AutoConfigureWebTestClient
class ChatControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ChatService chatService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    //  Authorized user test
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void shouldReturnAIResponse_whenRequestIsValid() {
        // given
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello AI");

        ChatResponseDto response = new ChatResponseDto();
        response.setResponse("Hi there from AI!");

        Mockito.when(chatService.getChatResponse(any(ChatRequestDto.class)))
                .thenReturn(Mono.just(response));

        // when & then
        webTestClient
                .post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.response").isEqualTo("Hi there from AI!");
    }

    //  Unauthorized user test
    @Test
    void shouldReturnUnauthorized_whenUserNotAuthenticated() {
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello");

        webTestClient
                .post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
