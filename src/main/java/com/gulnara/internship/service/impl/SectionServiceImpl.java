package com.gulnara.internship.service.impl;

import com.gulnara.internship.exception.ResourceNotFoundException;
import com.gulnara.internship.model.Section;
import com.gulnara.internship.repository.SectionRepository;
import com.gulnara.internship.service.api.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

    @Override
    public List<Section> getSectionsByCourseId(UUID courseId) {
        return sectionRepository.findByCourseIdOrderByIdAsc(courseId);
    }

    @Override
    public Section getSectionByIdForCourse(UUID courseId, UUID sectionId) {
        return sectionRepository.findById(sectionId)
                .filter(section -> section.getCourse() != null &&
                        section.getCourse().getId().equals(courseId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Section not found for course: " + courseId + ", section: " + sectionId
                ));
    }
}
