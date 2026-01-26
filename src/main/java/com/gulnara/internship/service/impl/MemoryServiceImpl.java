package com.gulnara.internship.service.impl;

import com.gulnara.internship.model.memory.MemoryFact;
import com.gulnara.internship.model.memory.UserMemory;
import com.gulnara.internship.repository.UserMemoryRepository;
import com.gulnara.internship.service.api.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    private final UserMemoryRepository userMemoryRepository;
    private final MemoryExtractorService extractorService;

    @Override
    public void extractAndSave(UUID userId, String userMessage) {
        List<MemoryFact> facts = extractorService.extractFacts(userMessage);

        for (MemoryFact fact : facts) {
            if (fact == null) continue;

            String key = normalizeKey(fact.key());
            String value = normalizeValue(fact.value());

            if (key == null || value == null || value.isBlank()) {
                continue;
            }
            saveOrUpdate(userId, key, value);
        }
    }

    @Override
    public String buildMemoryPrompt(UUID userId) {

        List<UserMemory> memories = userMemoryRepository.findByUserId(userId);

        if (memories.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("""
        IMPORTANT MEMORY:
        """);

        for (UserMemory memory : memories) {
            sb.append("- ")
                    .append(memory.getKey())
                    .append(": ")
                    .append(memory.getValue())
                    .append("\n");
        }
        sb.append(""" 
        RULES:
        - Treat IMPORTANT MEMORY as true facts.
        - Use them consistently in future responses.
        """);

        return sb.toString();
    }

    private void saveOrUpdate(UUID userId, String key, String value) {

        userMemoryRepository.findByUserId(userId).stream()
                .filter(m -> m.getKey().equals(key))
                .findFirst()
                .ifPresentOrElse(
                        existing -> {
                            existing.setValue(value);
                            userMemoryRepository.save(existing);
                        },
                        () -> {
                            UserMemory memory = new UserMemory();
                            memory.setUserId(userId);
                            memory.setKey(key);
                            memory.setValue(value);
                            memory.setCreatedAt(LocalDateTime.now());
                            userMemoryRepository.save(memory);
                        }
                );
    }
    private String normalizeKey(String rawKey) {
        if (rawKey == null) return null;

        rawKey = rawKey.toLowerCase();

        if (rawKey.contains("user")) return "user_name";
        if (rawKey.contains("friend")) return "friend_name";

        return null;
    }

    private String normalizeValue(String rawValue) {
        if (rawValue == null) return null;

        String v = rawValue.trim();
        return v.isEmpty() ? null : v;
    }
}
