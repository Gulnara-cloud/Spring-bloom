package com.gulnara.internship.controller;

import com.gulnara.internship.model.Section;
import com.gulnara.internship.service.api.SectionService;
import com.gulnara.internship.dto.SectionDto;
import com.gulnara.internship.dto.mapper.SectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    /**
     * GET /api/courses/{courseId}/sections
     * Returns all sections for a given course.
     * The frontend will use this to build the "left menu" or "section list".
     */
    @GetMapping
    public List<SectionDto> getSections(@PathVariable UUID courseId) {
        List<Section> sections = sectionService.getSectionsByCourseId(courseId);
        return SectionMapper.toDtoList(sections);
    }

    /**
     * GET /api/courses/{courseId}/sections/{sectionId}
     * Returns the selected section content (e.g. lecture text).
     * The frontend will display this in the main learning panel.
     */
    @GetMapping("/{sectionId}")
    public SectionDto getSection(@PathVariable UUID courseId,
                                 @PathVariable UUID sectionId) {
        Section section = sectionService.getSectionByIdForCourse(courseId, sectionId);
        return SectionMapper.toDto(section);
    }
}
