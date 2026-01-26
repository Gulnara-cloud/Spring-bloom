package com.gulnara.internship.service.api;

import com.gulnara.internship.model.Course;
import java.util.List;
import java.util.UUID;

public interface CourseService {

    List<Course> getAllCourses();

    Course getCourseById(UUID courseId);
}
