package com.gulnara.internship.controller;

import com.gulnara.internship.model.Course;
import com.gulnara.internship.service.api.CourseService;
import com.gulnara.internship.dto.CourseDto;
import com.gulnara.internship.dto.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // GET /api/courses – list of all courses
    @GetMapping
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return CourseMapper.toDtoList(courses);
    }

    // GET /api/courses/{id} – one course by id
    @GetMapping("/{id}")
    public CourseDto getCourseById(@PathVariable UUID id) {
        Course course = courseService.getCourseById(id);
        return CourseMapper.toDto(course);
    }
}
