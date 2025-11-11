package com.gulnara.internship.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides access to sensitive Gemini API configuration values.
 * Values are loaded from application.yml or environment variables.
 */
@Component
public class ApiKeyProvider {

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    @Value("${GEMINI_API_URL}")
    private String geminiApiUrl;

    public String getGeminiApiKey() {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key is missing. Please check your configuration.");
        }
        return geminiApiKey;
    }

    public String getGeminiApiUrl() {
        if (geminiApiUrl == null || geminiApiUrl.isEmpty()) {
            throw new IllegalStateException("Gemini API URL is missing. Please check your configuration.");
        }
        return geminiApiUrl;
    }
}
