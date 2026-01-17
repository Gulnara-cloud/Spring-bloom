package com.gulnara.internship.repository;

import com.gulnara.internship.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    // Find all conversations for a user, ordered by last updated
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    // Validate ownership: find conversation by id & user id
    Optional<Conversation> findByIdAndUserId(UUID id, UUID userId);
}
