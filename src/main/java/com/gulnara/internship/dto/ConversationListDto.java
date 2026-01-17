package com.gulnara.internship.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationListDto {

    private UUID id;
    private String title;
    private String modelName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String lastMessagePreview;   // optional
    private Integer messageCount;        // optional
}
