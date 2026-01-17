package com.gulnara.internship.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides access to sensitive Gemini API configuration values.
 * Values are loaded from application.yml or environment variables.
 */
@Component
public class ApiKeyProvider {

    @Value("${OPENAI_API_KEY:test-key}")
    private String openaiApiKey;

    @Value("${OPENAI_API_URL:https://fake.url}")
    private String openaiApiUrl;

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }

    public String getOpenaiApiUrl() {
        return openaiApiUrl;
    }
}
