package com.gulnara.internship.dto.mapper;

import com.gulnara.internship.dto.CourseDto;
import com.gulnara.internship.model.Course;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting Course entities to CourseDto.
 */
public final class CourseMapper {

    private CourseMapper() {
        // Utility class, no instances allowed
    }
    public static CourseDto toDto(Course course) {
        if (course == null) {
            return null;
        }
        return new CourseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription()
        );
    }
    public static List<CourseDto> toDtoList(List<Course> courses) {
        return courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }
}
