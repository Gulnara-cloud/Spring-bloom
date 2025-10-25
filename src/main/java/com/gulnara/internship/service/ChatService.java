package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import reactor.core.publisher.Mono;

/**
 * ChatService defines an asynchronous contract for
 * sending user messages to an AI API and receiving responses.
 */
public interface ChatService {

    // Asynchronous method returning a reactive Mono
    Mono<ChatResponseDto> getChatResponse(ChatRequestDto request);
}
