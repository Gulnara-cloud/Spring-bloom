package com.gulnara.internship.service;

import com.gulnara.internship.dto.ChatRequestDto;
import com.gulnara.internship.dto.ChatResponseDto;
import com.gulnara.internship.dto.ConversationDetailDto;
import com.gulnara.internship.dto.ConversationListDto;
import com.gulnara.internship.model.Conversation;
import com.gulnara.internship.model.Message;
import com.gulnara.internship.model.MessageRole;
import com.gulnara.internship.model.User;
import com.gulnara.internship.repository.ConversationRepository;
import com.gulnara.internship.repository.MessageRepository;
import com.gulnara.internship.repository.UserRepository;
import com.gulnara.internship.service.api.MemoryService;
import com.gulnara.internship.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock private ConversationRepository conversationRepository;

    @Mock private MessageRepository messageRepository;

    @Mock private UserRepository userRepository;

    @Mock private OpenAiClientService openAiClientService;

    @Mock private MemoryService memoryService;

    @InjectMocks private ChatServiceImpl chatService;

    // 1) processChat – NEW CONVERSATION
    @Test
    void testProcessChat_newConversation_success() {
        UUID userId = UUID.randomUUID();

        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello");
        request.setModelName("gpt-mini");
        request.setConversationId(null);

        // user
        User user = new User();
        user.setId(userId);

        // new conversation that will be returned by repository
        Conversation newConv = Conversation.builder()
                .id(UUID.randomUUID())
                .title("New chat")
                .modelName("gpt-mini")
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // user message
        Message savedUserMsg = Message.builder()
                .id(1L)
                .content("Hello")
                .role(MessageRole.USER)
                .conversation(newConv)
                .createdAt(LocalDateTime.now())
                .build();

        // AI message
        Message savedAiMsg = Message.builder()
                .id(2L)
                .content("AI reply")
                .role(MessageRole.AI)
                .conversation(newConv)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // mock conversation save (new conversation)
        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(newConv);

        // first save – user message, second save – AI message
        when(messageRepository.save(any(Message.class)))
                .thenReturn(savedUserMsg)
                .thenReturn(savedAiMsg);

        // history before вызова OpenAI
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(newConv.getId()))
                .thenReturn(List.of(savedUserMsg));

        // long-term memory prompt (не null, чтобы не было NPE)
        when(memoryService.buildMemoryPrompt(any(UUID.class)))
                .thenReturn("");

        // OpenAI response
        when(openAiClientService.generateResponse(anyList()))
                .thenReturn("AI reply");

        ChatResponseDto response = chatService.processChat(userId, request);

        assertNotNull(response);
        assertEquals(newConv.getId(), response.getConversationId());
        assertTrue(response.isNewConversation());
        assertEquals("AI reply", response.getResponse());

        verify(userRepository).findById(userId);
        verify(conversationRepository, times(2)).save(any(Conversation.class));
        verify(messageRepository, times(2)).save(any(Message.class));
        verify(messageRepository).findByConversationIdOrderByCreatedAtAsc(newConv.getId());
        verify(openAiClientService).generateResponse(anyList());
        verify(memoryService).extractAndSave(userId, "Hello");
    }

    // 2) processChat – EXISTING CONVERSATION
    @Test
    void testProcessChat_existingConversation_success() {
        UUID userId = UUID.randomUUID();
        UUID convId = UUID.randomUUID();

        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Test");
        request.setModelName("gpt-mini");
        request.setConversationId(convId);

        User user = new User();
        user.setId(userId);

        Conversation conv = Conversation.builder()
                .id(convId)
                .title("Chat 1")
                .modelName("gpt-mini")
                .user(user)
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .updatedAt(LocalDateTime.now().minusMinutes(1))
                .build();

        Message userMsg = Message.builder()
                .id(1L)
                .content("Test")
                .role(MessageRole.USER)
                .conversation(conv)
                .createdAt(LocalDateTime.now())
                .build();

        Message aiMsg = Message.builder()
                .id(2L)
                .content("AI says ok")
                .role(MessageRole.AI)
                .conversation(conv)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.findByIdAndUserId(convId, userId))
                .thenReturn(Optional.of(conv));

        when(messageRepository.save(any(Message.class)))
                .thenReturn(userMsg)
                .thenReturn(aiMsg);

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(convId))
                .thenReturn(List.of(userMsg));

        when(memoryService.buildMemoryPrompt(any(UUID.class)))
                .thenReturn("");

        when(openAiClientService.generateResponse(anyList()))
                .thenReturn("AI says ok");

        ChatResponseDto response = chatService.processChat(userId, request);

        assertNotNull(response);
        assertFalse(response.isNewConversation());
        assertEquals("AI says ok", response.getResponse());

        verify(conversationRepository).findByIdAndUserId(convId, userId);
        verify(messageRepository, times(2)).save(any(Message.class));
        verify(messageRepository).findByConversationIdOrderByCreatedAtAsc(convId);
        verify(openAiClientService).generateResponse(anyList());
        verify(memoryService).extractAndSave(userId, "Test");
    }

    // 3) processChat – USER NOT FOUND
    @Test
    void testProcessChat_userNotFound() {
        UUID userId = UUID.randomUUID();

        ChatRequestDto request = new ChatRequestDto();
        request.setMessage("Hello");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> chatService.processChat(userId, request));

        assertEquals("User not found", ex.getMessage());
        verify(conversationRepository, never()).save(any());
        verify(messageRepository, never()).save(any());
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
                .modelName("gpt-mini")
                .user(user)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .updatedAt(LocalDateTime.now())
                .build();

        Message lastMsg = Message.builder()
                .id(1L)
                .content("Last message here")
                .conversation(conv)
                .role(MessageRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId))
                .thenReturn(List.of(conv));

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId()))
                .thenReturn(List.of(lastMsg));

        List<ConversationListDto> list = chatService.getConversations(userId);

        assertEquals(1, list.size());
        assertEquals("Chat 1", list.get(0).getTitle());

        verify(conversationRepository).findByUserIdOrderByUpdatedAtDesc(userId);
        verify(messageRepository, times(2))
                .findByConversationIdOrderByCreatedAtAsc(conv.getId());
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
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .updatedAt(LocalDateTime.now())
                .build();

        Message msg = Message.builder()
                .id(1L)
                .content("Hello")
                .role(MessageRole.USER)
                .conversation(conv)
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

        Conversation conv = Conversation.builder()
                .id(convId)
                .title("Chat to delete")
                .user(user)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(conversationRepository.findByIdAndUserId(convId, userId))
                .thenReturn(Optional.of(conv));

        chatService.deleteConversation(userId, convId);

        verify(conversationRepository, times(1)).delete(conv);
    }
}
