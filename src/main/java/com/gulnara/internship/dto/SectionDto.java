package com.gulnara.internship.dto;

import java.util.UUID;

/**
 * DTO representing a course section for API responses.
 */
public record SectionDto(UUID id, String content) {
}
