package com.gulnara.internship.service.impl;

import com.gulnara.internship.exception.ResourceNotFoundException;
import com.gulnara.internship.model.Course;
import com.gulnara.internship.repository.CourseRepository;
import com.gulnara.internship.service.api.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
    }
}
