package com.gulnara.internship.service.api;

import java.util.UUID;

public interface MemoryService {

    void extractAndSave(UUID userId, String userMessage);

    String buildMemoryPrompt(UUID userId);
}
