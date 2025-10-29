package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ChatServiceImplTest {

    @Autowired
    private ChatServiceImpl chatService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void testGetChatResponse_returnsExpectedMessage() {
        // Arrange
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello AI");

        ChatResponseDto mockResponse = new ChatResponseDto();
        mockResponse.setResponse("Hi there!");

        // Mock external API call
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ChatResponseDto.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        ChatResponseDto actual = chatService.getChatResponse(request);

        // Assert
        assertNotNull(actual);
        assertEquals("Hi there!", actual.getResponse());
    }

    @Test
    void testGetChatResponse_handlesErrorGracefully() {
        // Arrange
        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Error test");

        // Mock API failure
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ChatResponseDto.class)
        )).thenThrow(new RuntimeException("API error"));

        // Act
        ChatResponseDto actual = chatService.getChatResponse(request);

        // Assert
        assertNotNull(actual);
        assertTrue(actual.getResponse().contains("Error"));
    }
}
