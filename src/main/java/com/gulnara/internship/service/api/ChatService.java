package com.gulnara.internship.service.api;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.dto.ConversationDetailDto;
import com.gulnara.internship.dto.ConversationListDto;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    // Main chat processing
    ChatResponseDto processChat(UUID userId, ChatRequestDto request);

    // Get all conversations for a user
    List<ConversationListDto> getConversations(UUID userId);

    // Get full messages + metadata for one conversation
    ConversationDetailDto getConversationDetail(UUID userId, UUID conversationId);

    // Optional delete (conversation + messages)
    void deleteConversation(UUID userId, UUID conversationId);
}
