package com.gulnara.internship.memory;

import java.util.UUID;

public interface MemoryService {

    void extractAndSave(UUID userId, String userMessage);

    String buildMemoryPrompt(UUID userId);
}
