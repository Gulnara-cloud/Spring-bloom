package com.gulnara.internship.repository;

import com.gulnara.internship.model.memory.UserMemory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserMemoryRepository extends JpaRepository<UserMemory, UUID> {

    List<UserMemory> findByUserId(UUID userId);
}
