package com.gulnara.internship.dto;

import lombok.*;
import lombok.Getter;

import java.util.UUID;

/**
 * Request DTO for sending a chat message.
 * sessionId may be null if user starts a new conversation.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

    private UUID conversationId;   // optional
    private String modelName;      // optional
    private String message;        // required
}
