package com.gulnara.internship.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Collections;
import java.util.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    // Getters and Setters
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id; // Automatically generated UUID (primary key)

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "Password_hash", nullable = false, length = 255)
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<Conversation> conversations = new ArrayList<>();

    // Default constructor (required by JPA)
    public User() {
    }

    // Parameterized constructor (used in service/tests)
    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @ManyToMany
    @JoinTable(
            name = "user_courses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> enrolledCourses = new HashSet<>();
}