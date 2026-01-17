package com.gulnara.internship.service;

import com.gulnara.internship.dto.*;
import com.gulnara.internship.memory.MemoryService;
import com.gulnara.internship.model.Conversation;
import com.gulnara.internship.model.Message;
import com.gulnara.internship.model.MessageRole;
import com.gulnara.internship.model.User;
import com.gulnara.internship.repository.ConversationRepository;
import com.gulnara.internship.repository.MessageRepository;
import com.gulnara.internship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MemoryService memoryService;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final OpenAiClientService openAiClientService;

    @Override
    public ChatResponseDto processChat(UUID userId, ChatRequestDto request) {
        // A) User & Conversation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Conversation conversation;

        // Check if conversation ID provided
        if (request.getConversationId() == null) {
            // New conversation
            conversation = Conversation.builder()
                    .title("New chat")
                    .modelName(request.getModelName())
                    .user(user)
                    .build();
            conversation = conversationRepository.save(conversation);

        } else {
            // Existing conversation - validate ownership
            UUID convId = request.getConversationId();
            conversation = conversationRepository.findByIdAndUserId(convId, userId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found or access denied"));
        }
        // B) Save user message
        Message userMessage = Message.builder()
                .conversation(conversation)
                .role(MessageRole.USER)
                .content(request.getMessage())
                .build();
        messageRepository.save(userMessage);
        memoryService.extractAndSave(userId, request.getMessage());

        // C) Get full conversation history (ordered)
        List<Message> history = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversation.getId());

        // Build messages for OpenAI
        List<Map<String, String>> messages = new ArrayList<>();

        // SYSTEM PROMPT
        messages.add(Map.of(
                "role", "system",
                "content", "You are a helpful conversational assistant."
        ));

        // LONG-TERM MEMORY (facts)
        String memoryPrompt = memoryService.buildMemoryPrompt(userId);
        if (!memoryPrompt.isBlank()) {
            messages.add(Map.of(
                    "role", "system",
                    "content", memoryPrompt
            ));
        }
        //  FULL CONVERSATION HISTORY
        for (Message m : history) {
            messages.add(Map.of(
                    "role", m.getRole() == MessageRole.USER ? "user" : "assistant",
                    "content", m.getContent()
            ));
        }

        //  CALL OPENAI
        String aiReply = openAiClientService.generateResponse(messages);


        // D) Save AI message
        Message aiMessage = Message.builder()
                .conversation(conversation)
                .role(MessageRole.AI)
                .content(aiReply)
                .build();
        messageRepository.save(aiMessage);

        // E) Update conversation timestamp
        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);

        // Build response DTO
        return ChatResponseDto.builder()
                .conversationId(conversation.getId())
                .conversationTitle(conversation.getTitle())
                .newConversation(request.getConversationId() == null)
                .response(aiReply)
                .timestamp(aiMessage.getCreatedAt())
                .build();
    }

    @Override
    public List<ConversationListDto> getConversations(UUID userId) {
        // Find user (validate existence)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Get all conversations for this user ordered by updatedAt DESC
        List<Conversation> conversations =
                conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        // Convert each conversation to DTO
        return conversations.stream()
                .map(conv -> ConversationListDto.builder()
                        .id(conv.getId())
                        .title(conv.getTitle())
                        .modelName(conv.getModelName())
                        .createdAt(conv.getCreatedAt())
                        .updatedAt(conv.getUpdatedAt())
                        .lastMessagePreview(getLastMessagePreview(conv.getId()))
                        .messageCount(getMessageCount(conv.getId()))
                        .build()
                )
                .toList();
    }
    private String getLastMessagePreview(UUID conversationId) {
        List<Message> messages = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
        if (messages.isEmpty()) return "";
        Message last = messages.get(messages.size() - 1);
        String text = last.getContent();

        return text.length() > 50 ? text.substring(0, 50) + "..." : text;
    }
    private int getMessageCount(UUID conversationId) {
        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId)
                .size();
    }
    @Override
    public ConversationDetailDto getConversationDetail(UUID userId, UUID conversationId) {

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate conversation ownership
        Conversation conversation = conversationRepository
                .findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation not found or access denied"));

        // Load all messages sorted ASC
        List<Message> messages = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);

        // Convert messages to DTO
        List<MessageDto> messageDtos = messages.stream()
                .map(msg -> MessageDto.builder()
                        .id(msg.getId())
                        .role(msg.getRole().name())
                        .content(msg.getContent())
                        .timestamp(msg.getCreatedAt())
                        .build()
                )
                .toList();

        // Build conversation detail DTO
        return ConversationDetailDto.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .modelName(conversation.getModelName())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .messages(messageDtos)
                .build();
    }
    @Override
    public void deleteConversation(UUID userId, UUID conversationId) {

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate conversation ownership
        Conversation conversation = conversationRepository
                .findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation not found or access denied"));

        // Delete conversation (messages removed automatically by cascade)
        conversationRepository.delete(conversation);
    }
}

