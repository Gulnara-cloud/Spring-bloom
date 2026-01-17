package com.gulnara.internship.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetailDto {

    private UUID id;
    private String title;
    private String modelName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<MessageDto> messages;
}
