package com.gulnara.internship.repository;

import com.gulnara.internship.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
}
