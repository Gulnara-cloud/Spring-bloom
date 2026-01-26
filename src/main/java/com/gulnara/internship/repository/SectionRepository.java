package com.gulnara.internship.repository;

import com.gulnara.internship.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
    // Returns all sections ordered by id for a specific course
    List<Section> findByCourseIdOrderByIdAsc(UUID courseId);
}
