package com.gulnara.internship.dto;

import java.util.UUID;

/**
 * DTO representing basic course information for API responses.
 */
public record CourseDto(UUID id, String title, String description) {
}
