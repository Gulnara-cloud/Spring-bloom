package com.gulnara.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gulnara.internship.config.TestSecurityConfig;
import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.dto.ConversationDetailDto;
import com.gulnara.internship.dto.ConversationListDto;
import com.gulnara.internship.service.api.ChatService;
import com.gulnara.internship.service.impl.CustomUserDetailsService;
import com.gulnara.internship.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private static final String USERNAME = "testUser";
    private UUID userId;

    @BeforeEach
    void setUpSecurityContext() {
        // кладём пользователя в SecurityContext, чтобы SecurityUtil смог взять username
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                USERNAME,
                null,
                Collections.emptyList()
        );
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        userId = UUID.randomUUID();
    }

    @Test
    void testProcessChat() throws Exception {
        ChatResponseDto response = new ChatResponseDto(
                UUID.randomUUID(),
                "New chat",
                true,
                "Hello!",
                LocalDateTime.parse("2025-01-01T12:00:00")
        );

        when(userService.getUserIdByUsername(USERNAME)).thenReturn(userId);
        when(chatService.processChat(eq(userId), any(ChatRequestDto.class)))
                .thenReturn(response);

        ChatRequestDto req = new ChatRequestDto();
        req.setMessage("Hi");
        req.setModelName("gpt-mini-1");

        mockMvc.perform(post("/api/chat/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Hello!"));
    }

    @Test
    void testGetConversations() throws Exception {
        when(userService.getUserIdByUsername(USERNAME)).thenReturn(userId);

        List<ConversationListDto> conversations = List.of(
                new ConversationListDto(),
                new ConversationListDto()
        );
        when(chatService.getConversations(userId)).thenReturn(conversations);

        mockMvc.perform(get("/api/chat/conversations"))
                .andExpect(status().isOk());
        // при желании можно добавить:
        // .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetConversationDetail() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserIdByUsername(USERNAME)).thenReturn(userId);

        ConversationDetailDto detail = new ConversationDetailDto();
        when(chatService.getConversationDetail(userId, id)).thenReturn(detail);

        mockMvc.perform(get("/api/chat/conversations/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteConversation() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserIdByUsername(USERNAME)).thenReturn(userId);
        doNothing().when(chatService).deleteConversation(userId, id);

        mockMvc.perform(delete("/api/chat/conversations/" + id))
                .andExpect(status().isNoContent());
    }
}
