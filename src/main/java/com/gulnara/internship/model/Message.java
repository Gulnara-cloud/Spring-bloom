package com.gulnara.internship.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a single message inside a chat session.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many messages belong to one chat session
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    @JsonBackReference
    private Conversation conversation;

    // Sender type: USER or AI
    @Enumerated(EnumType.STRING)
    private MessageRole role;

    // Message text (from user or AI)
    @Column(columnDefinition = "TEXT")
    private String content;

    // Timestamp of the message
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
