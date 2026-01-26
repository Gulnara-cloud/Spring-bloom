package com.gulnara.internship.model.memory;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_memory")
public class UserMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(name = "memory_key", nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
