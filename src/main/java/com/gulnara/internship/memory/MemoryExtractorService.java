package com.gulnara.internship.memory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gulnara.internship.service.OpenAiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryExtractorService {

    private final OpenAiClientService openAiClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MemoryFact> extractFacts(String userMessage) {

        String prompt = """
        TASK:
        Extract ONLY stable, long-term personal facts about the user.

                RULES:
        - Extract facts only if the user explicitly states them.
                - Do NOT infer or guess.
        - Ignore emotions, opinions, temporary states.
                - Return STRICT JSON only.
                - If no facts exist, return [].

        JSON format:
        [
          {"key": "...", "value": "..."}
        ]

        User message:
        """ + userMessage;

                try {
                    String aiResponse = openAiClientService.simpleCompletion(prompt);

                    return objectMapper.readValue(
                            aiResponse,
                            new TypeReference<List<MemoryFact>>() {}
                    );

                } catch (Exception e) {
                    // extractor НИКОГДА не должен ломать чат
                    return Collections.emptyList();
                }
            }
        }
