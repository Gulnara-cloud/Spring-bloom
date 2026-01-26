package com.gulnara.internship.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToMany(mappedBy = "enrolledCourses")
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Section> sections = new ArrayList<>();
}
