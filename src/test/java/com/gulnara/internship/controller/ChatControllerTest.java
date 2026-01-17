package com.gulnara.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gulnara.internship.config.JwtAuthenticationFilter;
import com.gulnara.internship.config.SecurityConfig;
import com.gulnara.internship.config.TestSecurityConfig;
import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.service.ChatService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Import(TestSecurityConfig.class)

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ObjectMapper objectMapper;

    @Test
    void testProcessChat() throws Exception {

        ChatResponseDto response = new ChatResponseDto(
                UUID.randomUUID(),
                "New chat",
                true,
                "Hello!",
                LocalDateTime.parse("2025-01-01T12:00:00")
        );

        Mockito.when(chatService.processChat(any(), any()))
                .thenReturn(response);

        ChatRequestDto req = new ChatRequestDto();
        req.setMessage("Hi");
        req.setModelName("gpt-mini");

        mockMvc.perform(post("/api/chat/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Hello!"));
    }

    @Test
    void testGetConversations() throws Exception {
        mockMvc.perform(get("/api/chat"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetConversationDetail() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/chat/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteConversation() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/chat/" + id))
                .andExpect(status().isOk());
    }
}
