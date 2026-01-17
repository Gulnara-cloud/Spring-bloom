package com.gulnara.internship.service;

import com.gulnara.internship.dto.*;
import com.gulnara.internship.model.*;
import com.gulnara.internship.repository.ConversationRepository;
import com.gulnara.internship.repository.MessageRepository;
import com.gulnara.internship.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock private ConversationRepository conversationRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private UserRepository userRepository;
    @Mock private OpenAiClientService openAiClientService;

    @InjectMocks private ChatServiceImpl chatService;

    // 1) processChat() — NEW CONVERSATION
    @Test
    void testProcessChat_newConversation_success() {
        UUID userId = UUID.randomUUID();

        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello");
        request.setModelName("gpt-mini");

        User user = new User();
        user.setId(userId);

        Conversation newConv = Conversation.builder()
                .id(UUID.randomUUID())
                .title("New chat")
                .modelName("gpt-mini")
                .user(user)
                .updatedAt(LocalDateTime.now())
                .build();

        Message savedUserMsg = Message.builder()
                .id(1L)
                .content("Hello")
                .role(MessageRole.USER)
                .conversation(newConv)
                .createdAt(LocalDateTime.now())
                .build();

        Message savedAiMsg = Message.builder()
                .id(2L)
                .content("AI reply")
                .role(MessageRole.AI)
                .conversation(newConv)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.save(any())).thenReturn(newConv);
        when(messageRepository.save(any())).thenReturn(savedUserMsg).thenReturn(savedAiMsg);

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(newConv.getId()))
                .thenReturn(List.of(savedUserMsg));

        when(openAiClientService.generateResponse(anyList()))
                .thenReturn("AI reply");

        ChatResponseDto response = chatService.processChat(userId, request);

        assertNotNull(response);
        assertEquals(newConv.getId(), response.getConversationId());
        assertTrue(response.isNewConversation());
        assertEquals("AI reply", response.getResponse());

        verify(conversationRepository, times(1)).save(any());
        verify(messageRepository, times(2)).save(any());
        verify(openAiClientService).generateResponse(anyList());
    }

    // 2) processChat() — EXISTING CONVERSATION
    @Test
    void testProcessChat_existingConversation_success() {
        UUID userId = UUID.randomUUID();
        UUID convId = UUID.randomUUID();

        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Test");
        request.setConversationId(convId);
        request.setModelName("gpt-mini");

        User user = new User();
        user.setId(userId);

        Conversation conv = Conversation.builder()
                .id(convId)
                .title("Chat")
                .modelName("gpt-mini")
                .user(user)
                .updatedAt(LocalDateTime.now())
                .build();

        Message userMsg = Message.builder()
                .content("Test")
                .role(MessageRole.USER)
                .conversation(conv)
                .build();

        Message aiMsg = Message.builder()
                .content("AI says ok")
                .role(MessageRole.AI)
                .conversation(conv)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.findByIdAndUserId(convId, userId))
                .thenReturn(Optional.of(conv));

        when(messageRepository.save(any())).thenReturn(userMsg).thenReturn(aiMsg);

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(convId))
                .thenReturn(List.of(userMsg));

        when(openAiClientService.generateResponse(anyList()))
                .thenReturn("AI says ok");

        ChatResponseDto response = chatService.processChat(userId, request);

        assertFalse(response.isNewConversation());
        assertEquals("AI says ok", response.getResponse());
    }

    // 3) processChat() — USER NOT FOUND
    @Test
    void testProcessChat_userNotFound() {
        UUID userId = UUID.randomUUID();
        ChatRequestDto req = new ChatRequestDto();
        req.setMessage("Hello");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> chatService.processChat(userId, req));

        assertEquals("User not found", ex.getMessage());
    }

    // 4) getConversations()
    @Test
    void testGetConversations_success() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Conversation conv = Conversation.builder()
                .id(UUID.randomUUID())
                .title("Chat 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .modelName("gpt-mini")
                .user(user)
                .build();

        Message lastMsg = Message.builder()
                .content("Last message here")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId))
                .thenReturn(List.of(conv));

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId()))
                .thenReturn(List.of(lastMsg));

        List<ConversationListDto> list = chatService.getConversations(userId);

        assertEquals(1, list.size());
        assertEquals("Chat 1", list.get(0).getTitle());
    }

    // 5) getConversationDetail()
    @Test
    void testGetConversationDetail_success() {
        UUID userId = UUID.randomUUID();
        UUID convId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Conversation conv = Conversation.builder()
                .id(convId)
                .title("Detail Chat")
                .modelName("gpt-mini")
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message msg = Message.builder()
                .id(1L)
                .content("Hello")
                .role(MessageRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.findByIdAndUserId(convId, userId))
                .thenReturn(Optional.of(conv));
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(convId))
                .thenReturn(List.of(msg));

        ConversationDetailDto detail = chatService.getConversationDetail(userId, convId);

        assertNotNull(detail);
        assertEquals("Detail Chat", detail.getTitle());
        assertEquals(1, detail.getMessages().size());
    }

    // 6) deleteConversation()
    @Test
    void testDeleteConversation_success() {
        UUID userId = UUID.randomUUID();
        UUID convId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Conversation conv = new Conversation();
        conv.setId(convId);
        conv.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.findByIdAndUserId(convId, userId))
                .thenReturn(Optional.of(conv));

        chatService.deleteConversation(userId, convId);

        verify(conversationRepository, times(1)).delete(conv);
    }
}
