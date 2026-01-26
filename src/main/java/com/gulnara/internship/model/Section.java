package com.gulnara.internship.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    @Column(length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
