package com.gulnara.internship.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gulnara.internship.config.ApiKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenAiClientService {

    private final RestTemplate restTemplate;
    private final ApiKeyProvider apiKeyProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(List<Map<String, String>> messages) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4.1-mini",
                    "messages", messages
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKeyProvider.getOpenaiApiKey());

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            String apiUrl = apiKeyProvider.getOpenaiApiUrl() + "/v1/chat/completions";

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("OpenAI request failed", e);
        }
    }
    public String simpleCompletion(String prompt) {
        try {
            List<Map<String, String>> messages = List.of(
                    Map.of(
                            "role", "system",
                            "content", prompt
                    )
            );

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4.1-mini",
                    "messages", messages
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKeyProvider.getOpenaiApiKey());

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            String apiUrl = apiKeyProvider.getOpenaiApiUrl() + "/v1/chat/completions";

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            return "[]";
        }
    }
}
