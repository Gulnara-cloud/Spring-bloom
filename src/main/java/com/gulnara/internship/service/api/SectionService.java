package com.gulnara.internship.service.api;

import com.gulnara.internship.model.Section;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing course sections.
 */
public interface SectionService {

    /**
     * Returns all sections that belong to a specific course.
     */
    List<Section> getSectionsByCourseId(UUID courseId);

    /**
     * Returns a single section by id and validates that it belongs to the given course.
     */
    Section getSectionByIdForCourse(UUID courseId, UUID sectionId);
}
