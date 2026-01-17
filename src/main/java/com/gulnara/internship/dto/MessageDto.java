package com.gulnara.internship.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long id;
    private String role;         // "USER" / "AI"
    private String content;
    private LocalDateTime timestamp;
}
