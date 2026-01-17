package com.gulnara.internship.repository;

import com.gulnara.internship.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get messages for a conversation (chronologically)
    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    // Optional: delete all message in a conversation
    void deleteByConversationId(UUID conversationId);
}
