package com.gulnara.internship.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for returning AI reply and current session ID.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {

    private UUID conversationId;
    private String conversationTitle;
    private boolean newConversation; // optional, default false

    private String response;      // AI message
    private LocalDateTime timestamp;
}

